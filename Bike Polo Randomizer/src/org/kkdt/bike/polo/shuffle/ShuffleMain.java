package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShuffleMain extends FragmentActivity {
	public static final String PLAYER_NAME = "PLAYER_NAME";
	public static final String PLAYER_LIST_L = "PLAYER_LIST_L";
	public static final String PLAYER_LIST_R = "PLAYER_LIST_R";
	public static final String PLAYER_NUMBER = "PLAYER_NUMBER";	
	public static final int YES = 1;
	public static final int NO = 0;
	public static final int USE_TIMER = 0;
	public static final int DEFAULT_GAME_TIME = 1;
	
	private static final String SUPER_ARIEL = "Super Ariel";
	private static final String CURRENT_DIALOG = "CURRENT_DIALOG";
	private static final int NUM_PLAYERS = 6;	
	private static final int NO_DIALOG = 0;
	private static final int NEW_GAME = 1;
	private static final int LAST_GAME = 2;
	private static final int REMOVE_PLAYER = 3;
	private static final int ADD_PLAYER = 4;
	
	private PlayerDBDataSource dataSource;
	private List<BikePoloPlayer> players = new ArrayList<BikePoloPlayer>();
	private LastGameDialog dialogLastGame = new LastGameDialog();
	private RemovePlayerDialog dialogRemovePlayer = new RemovePlayerDialog();
	private NewGameDialog dialogNewGame = new NewGameDialog();
    private AddPlayer dialogAddPlayer = new AddPlayer();
	private int currentDialog = NO_DIALOG;
		
	private class PlayerAdapter extends ArrayAdapter<BikePoloPlayer> {		
		
		PlayerAdapter(Context context, List<BikePoloPlayer> players) {
			super(context, R.layout.list_player_item, players);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v==null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_player_item, null);
			}			
			BikePoloPlayer shownPlayer = this.getItem(position);
			TextView playerName = (TextView) v.findViewById(R.id.playerNameOnList);
			TextView playerGames = (TextView) v.findViewById(R.id.gamesPlayedOnList);
			CheckBox playerInGame = (CheckBox) v.findViewById(R.id.checkboxInPlay);
			playerName.setOnLongClickListener(new View.OnLongClickListener() {
				public boolean onLongClick(View v) {					
					int whichPlayer = (Integer)v.getTag();
					removePlayerDialog(whichPlayer);					
					return true;
				}
			}
			);
			String playerData = shownPlayer.getName(); 		
			playerName.setText(playerData);
			playerName.setTag(position);
			playerData = "" + shownPlayer.getGames();
			if (shownPlayer.getHandicap() > 0) {
				playerData += " (+" + shownPlayer.getHandicap() + ")";
			}
			playerGames.setText(playerData);
			boolean ifPlaysData = shownPlayer.ifPlays();
			playerInGame.setChecked(ifPlaysData);
			playerInGame.setTag(position);
			return v;
		}
	}	
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomizer_main);
        dataSource = new PlayerDBDataSource(this);
		dataSource.open();
        if (savedInstanceState != null) {        	
    		players = dataSource.getAllPlayers(); // refresh players list
    		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();
    		app.clearLatestGame();
    		List<BikePoloPlayer> latestGameL = new ArrayList<BikePoloPlayer>();
    		List<BikePoloPlayer> latestGameR = new ArrayList<BikePoloPlayer>();
        	if (savedInstanceState.containsKey(PLAYER_LIST_L)) { // latest game L team stored
        		int[] playerNumbers = savedInstanceState.getIntArray(PLAYER_LIST_L);
        		for (int i=0; i<playerNumbers.length; i++) {
        			if ((playerNumbers[i]<players.size()) && (playerNumbers[i]>=0)) {
        				latestGameL.add(players.get(playerNumbers[i]));
        			} 
        			else {
        				showToast("Fault. Wrong current player \n"+playerNumbers[i]);
        			}
        			
        		}
        	}
        	if (savedInstanceState.containsKey(PLAYER_LIST_R)) { // latest game R team stored
        		latestGameR.clear();	// clear list
        		int[] playerNumbers = savedInstanceState.getIntArray(PLAYER_LIST_R);
        		for (int i=0; i<playerNumbers.length; i++) {
        			if ((playerNumbers[i]<players.size()) && (playerNumbers[i]>=0)) {
        				latestGameR.add(players.get(playerNumbers[i]));
        			} 
        			else {
        				showToast("Fault. Wrong current player \n"+playerNumbers[i]);
        			}
        			
        		}
        	}
        	app.setLatestGame(latestGameL, latestGameR);
        	if (savedInstanceState.containsKey(CURRENT_DIALOG)) {
        		// restore dialogs
        		currentDialog = savedInstanceState.getInt(CURRENT_DIALOG);
        		switch (currentDialog) {
        		case NEW_GAME:
        			dialogNewGame.show(getSupportFragmentManager(), getString(R.string.nextGame));
        			break;
        		case LAST_GAME:        		
        			dialogLastGame.show(getSupportFragmentManager(), getString(R.string.nextGame));       		
        			break;
        		case REMOVE_PLAYER:
        			Bundle dialogParams = new Bundle();
        			String removedName = savedInstanceState.getString(PLAYER_NAME);
        			dialogParams.putString(PLAYER_NAME, removedName);
        			dialogRemovePlayer.setArguments(dialogParams);            
        			dialogRemovePlayer.show(getSupportFragmentManager(), getString(R.string.removePlayer));
        			break;
        		case ADD_PLAYER:
        			dialogParams = new Bundle();
        			String addedName = savedInstanceState.getString(PLAYER_NAME);
        			dialogParams.putString(PLAYER_NAME, addedName);
        			dialogAddPlayer.setArguments(dialogParams);            
        			dialogAddPlayer.show(getSupportFragmentManager(), getString(R.string.addPlayer));
        			break;
        		default:	// no dialog
        			break;
        		}
        	}

        } else { // first run
        	currentDialog = NO_DIALOG;
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        dataSource.open();
        redrawPlayers();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	dataSource.close();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_randomizer_main, menu);
        return true;
    }
    
    @Override
    
    protected void onSaveInstanceState(Bundle outState) {
		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
		List<BikePoloPlayer> latestGameL = app.getLatestGame(true);
		List<BikePoloPlayer> latestGameR = app.getLatestGame(false);

    	if (latestGameL.size()>0) { // at least one game played
    		dataSource.open(); // may have been closed by onPause;    			
    		players = dataSource.getAllPlayers(); // update players list
    		int[] currentGamePlayerIds = new int[latestGameL.size()];
    		int i = 0;
    		for (BikePoloPlayer player : latestGameL) {
    			currentGamePlayerIds[i++] = players.indexOf(player); 
    		}
    		outState.putIntArray(PLAYER_LIST_L, currentGamePlayerIds);
    	}
    	if (latestGameR.size()>0) { // at least one game played
    		dataSource.open(); // may have been closed by onPause;    			
    		players = dataSource.getAllPlayers(); // update players list
    		int[] currentGamePlayerIds = new int[latestGameR.size()];
    		int i = 0;
    		for (BikePoloPlayer player : latestGameR) {
    			currentGamePlayerIds[i++] = players.indexOf(player); 
    		}
    		outState.putIntArray(PLAYER_LIST_R, currentGamePlayerIds);    		
    	}
    	dataSource.close();	// close if open
    	outState.putInt(CURRENT_DIALOG, currentDialog);
    	switch (currentDialog) {
    	case REMOVE_PLAYER:
    		outState.putString(PLAYER_NAME, dialogRemovePlayer.getRemovedName());
    		break;
    	case ADD_PLAYER:
    		outState.putString(PLAYER_NAME, dialogAddPlayer.getInputName());
    		break;
    	default:
    		break;
    	}
    }
        
    private void redrawPlayers() {
    	
        players = dataSource.getAllPlayers();         // get sorted results
        PlayerAdapter adapter = new PlayerAdapter (this, players);
        int id = R.id.listPlayers;
        View view = findViewById(id);
        ListView listPlayersView = (ListView) view;
        listPlayersView.setAdapter(adapter);
    }
    
    public void addPlayer(String playerName)
    {
    	if (dataSource.checkPlayer(playerName)) {
    		showToast(getString(R.string.playerExists));
    	} else {
    		int handicap = BikePoloPlayer.findMinGamesRank(players);;
    		if (playerName.equals(SUPER_ARIEL)) {
    			handicap -= 1;
    			showToast(SUPER_ARIEL + " Mode");
    					
    		} 
    		BikePoloPlayer newPlayer = new BikePoloPlayer(playerName, handicap); 
        	dataSource.insertPlayer(newPlayer);
        	redrawPlayers();
    	}    	
    }
    
    private List<BikePoloPlayer> drawPlayers(List<BikePoloPlayer> listPlayers,
    		int playersToDraw) {
    	List<BikePoloPlayer> playersInGame = new ArrayList<BikePoloPlayer>();    	
    	List<BikePoloPlayer> listActivePlayers = new ArrayList<BikePoloPlayer>();    	
    	for (int i=0;i<listPlayers.size();i++) {
    		BikePoloPlayer currPlayer = listPlayers.get(i);
    		if (currPlayer.ifPlays()) {
    			listActivePlayers.add(currPlayer);
    		}
    	}
    	if (listActivePlayers.size() > 0) {
    		playersInGame = BikePoloPlayer.drawRandomPlayers(playersToDraw, listActivePlayers,
    				BikePoloPlayer.MODE_EVEN);    			
    	}
    	return playersInGame;
    }
    
    public void startGame(List<BikePoloPlayer> playersInGameL,
    		List<BikePoloPlayer> playersInGameR, boolean useTimer) {
    	for (BikePoloPlayer tPlayer : playersInGameL) {
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}
    	for (BikePoloPlayer tPlayer : playersInGameR) {
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}    	
		redrawPlayers();
		if (useTimer) {
			Intent intent = new Intent(this, LatestGameActivity.class);
			intent.putExtra("PIWO", "tez bym sie...");
			startActivity(intent);
		}
    }
        
    public String changeCurrentPlayer(String playerName, boolean left) {
    	String newPlayerName = null;
		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
		List<BikePoloPlayer> latestGameL = app.getLatestGame(true);
		List<BikePoloPlayer> latestGameR = app.getLatestGame(false);
    	List<BikePoloPlayer> changedList = app.getLatestGame(left);
    	for (BikePoloPlayer tPlayer: changedList) {
    		if (tPlayer.getName() == playerName) {
    			List<BikePoloPlayer> otherPlayers = players; // fetch all players list
    			otherPlayers.removeAll(latestGameL); // remove L team players
    			otherPlayers.removeAll(latestGameR); // remove R team players
    			changedList.remove(tPlayer);
    			List<BikePoloPlayer> newPlayerList = drawPlayers(otherPlayers, 1);
    			if (newPlayerList.size()>0) {
        			BikePoloPlayer newPlayer = newPlayerList.get(0);
        			changedList.add(newPlayer);
        			newPlayerName = newPlayer.getName();    				
    			}
    			break;
    		}
    	}
    	
    	return newPlayerName;    
    }
    
    public void removePlayer(String name) {
    	dataSource.deletePlayer(name);
    	redrawPlayers();
    }
        
    public void clearDialog() {
    	currentDialog = NO_DIALOG;
    }
    
    private void removePlayerDialog(int whichPlayer) {    	
		String dialogName = getString(R.string.removePlayer);
		Bundle dialogParams = new Bundle();
		dialogParams.putString(PLAYER_NAME, players.get(whichPlayer).getName());
		dialogRemovePlayer.setArguments(dialogParams);            
		dialogRemovePlayer.show(getSupportFragmentManager(), dialogName);
		currentDialog = REMOVE_PLAYER;
    }
    
    public void addPlayer() {
        String buttonName = getString(R.string.addPlayer);
        dialogAddPlayer.show(getSupportFragmentManager(), buttonName);
        currentDialog = ADD_PLAYER;
    }
    
    public void nextGame() {
		players = dataSource.getAllPlayers(); // refresh players list    	
    	List<BikePoloPlayer> playersInGame = drawPlayers(players, NUM_PLAYERS);
    	if (playersInGame.size()>0) {
    		// Any players in game - show dialogLastGame
    		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
    		List<BikePoloPlayer> latestGameL = app.getLatestGame(true);
    		List<BikePoloPlayer> latestGameR = app.getLatestGame(false);
    		String buttonName = getString(R.string.nextGame);
    		int halfNumPlayers = (int)Math.ceil((double)playersInGame.size()/2);        
            latestGameL.clear();
            latestGameR.clear();
    		for (int i=0;i<playersInGame.size();i++) {
            	if (i<halfNumPlayers) {
            		latestGameL.add(playersInGame.get(i));
            	} else {
            		latestGameR.add(playersInGame.get(i));
            	}
            }
    		app.setLatestGame(latestGameL, latestGameR);
    		dialogNewGame.show(getSupportFragmentManager(), buttonName);
    		currentDialog = NEW_GAME;
    	} else {
    		showToast("Add players first");
    	}
    }
    
    public static int getSettings(int whichSetting) {
    	int settingValue = 0;
    	switch (whichSetting) {
    	case USE_TIMER:
    		settingValue = YES;
    		break;
    	case DEFAULT_GAME_TIME:
    		settingValue = 10;
    		break;
    	default:
    	}
    	return settingValue;
    }

    
    public void checkboxInPlayClick(View view) {
    	CheckBox clickedCheckBox = (CheckBox) view;
    	boolean newPlayState = clickedCheckBox.isChecked();
    	int clickedPlayerPosition = (Integer)clickedCheckBox.getTag();
    	BikePoloPlayer player = players.get(clickedPlayerPosition);
    	if (newPlayState) { // player returns to game, handicap shall be recalculated
    		int minGamesRank = BikePoloPlayer.findMinGamesRank(players);
    		if (player.getGamesRank() < minGamesRank) {
    			int handicap = minGamesRank - player.getGames();
    			player.setHandicap(handicap);
    			showToast(getString(R.string.rankAdjusted));    					
    		}
    	}
    	player.setPlays(newPlayState);
    	dataSource.updatePlayer(player);
    	redrawPlayers();
    }
    
    private void menuResetGameCntClick() {
		showToast(getString(R.string.resetGameCntResult));
    	for (int i=0; i<players.size(); i++) {
    		BikePoloPlayer player = players.get(i); 
    		player.resetGames();
    		dataSource.updatePlayer(player);
    	}
    	redrawPlayers();
    }


    private void menuRemoveAllPlayersClick() {
		showToast(getString(R.string.removeAllPlayersResult));				
    	dataSource.deleteAllPlayers();
    	redrawPlayers();
    }

    public void addPlayerButton(View view) {
        addPlayer();
    }
    
    public void nextGameButton(View view) {
    	nextGame();    	 
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle item selection
    	switch (item.getItemId()) {
    	case R.id.menuNewGame:
    		nextGame();
    		return true;
    	case R.id.menuAddPlayer:
    		addPlayer();
    		return true;
    	case R.id.menuShowLastGame:
    		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
    		List<BikePoloPlayer> latestGameL = app.getLatestGame(true);
        	if (latestGameL.size()>0) { // left game populated first
        		// Any players in game - show dialogLastGame
        		String buttonName = getString(R.string.nextGame);
        		dialogLastGame.show(getSupportFragmentManager(), buttonName);
        		currentDialog = LAST_GAME;        		
        	}
        	// else - no last game players - do nothing
        	return true;
    	case R.id.menuResetGameCnt:
    		menuResetGameCntClick();   		
    		return true;
    	case R.id.menuClearPlayerList:
    		menuRemoveAllPlayersClick();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void showToast(String text) {
    	Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }
}

