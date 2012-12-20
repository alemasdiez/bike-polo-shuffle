package org.kkdt.bike.polo.randomizer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
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

public class RandomizerMain extends FragmentActivity {
	public static final String PLAYER_NAME = "PLAYER_NAME";
	public static final String PLAYER_LIST = "PLAYER_LIST";
	public static final String PLAYER_NUMBER = "PLAYER_NUMBER";
	public static final int YES = 1;
	public static final int NO = 0;
	public static final int USE_TIMER = 0;
	
	private static final String SUPER_ARIEL = "Super Ariel";
	private static final int NUM_PLAYERS = 6;
	
	private PlayerDBDataSource dataSource;
	private List<BikePoloPlayer> players = new ArrayList<BikePoloPlayer>();
	private List<BikePoloPlayer> currentGame = new ArrayList<BikePoloPlayer>();
		
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_randomizer_main, menu);
        return true;
    }
        
    private void redrawPlayers() {
    	
        players = dataSource.getAllPlayers();      
        PlayerAdapter adapter = new PlayerAdapter (this, players);
        int id = R.id.listPlayers;
        View view = findViewById(id);
        ListView listPlayersView = (ListView) view;
        listPlayersView.setAdapter(adapter);
    }
    
    public void addPlayer(String playerName)
    {
    	if (dataSource.checkPlayer(playerName)) {
    		Toast.makeText(getBaseContext(), getString(R.string.playerExists),
    				Toast.LENGTH_LONG).show();
    	} else {
    		int handicap = BikePoloPlayer.findMinGamesRank(players);;
    		if (playerName.equals(SUPER_ARIEL)) {
    			handicap -= 1;
    			Toast.makeText(getBaseContext(), SUPER_ARIEL + " Mode",
    					Toast.LENGTH_SHORT).show();
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
    
    public void startGame(List<BikePoloPlayer> playersInGame, boolean useTimer) {
    	for (BikePoloPlayer tPlayer : playersInGame) {
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}
		redrawPlayers();
    }
    
    public List<BikePoloPlayer> getCurrentGame() {
    	return currentGame;
    }
    
    public String changeCurrentPlayer(String playerName) {
    	String newPlayerName = null;
    	for (BikePoloPlayer tPlayer: currentGame) {
    		if (tPlayer.getName() == playerName) {
    			List<BikePoloPlayer> otherPlayers = players;
    			otherPlayers.removeAll(currentGame);
    			currentGame.remove(tPlayer);
    			List<BikePoloPlayer> newPlayerList = drawPlayers(otherPlayers, 1);
    			if (newPlayerList.size()>0) {
        			BikePoloPlayer newPlayer = newPlayerList.get(0);
        			currentGame.add(newPlayer);
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
    
    public static int getSettings(int whichSetting) {
    	int settingValue = 0;
    	switch (whichSetting) {
    	case USE_TIMER:
    		settingValue = NO;
    		break;
    	default:
    	}
    	return settingValue;
    }
    
    private void removePlayerDialog(int whichPlayer) {    	
		RemovePlayerDialog dialog = new RemovePlayerDialog();
		String dialogName = getString(R.string.removePlayer);
		Bundle dialogParams = new Bundle();
		dialogParams.putInt(PLAYER_NUMBER, whichPlayer);
		dialogParams.putString(PLAYER_NAME, players.get(whichPlayer).getName());
		dialog.setArguments(dialogParams);            
		dialog.show(getSupportFragmentManager(), dialogName);
    }
    
    public void addPlayerButton(View view) {
        AddPlayer dialog = new AddPlayer();
        String buttonName = getString(R.string.addPlayer);
        dialog.show(getSupportFragmentManager(), buttonName);
    }
    
    public void nextGameButton(View view) {
    	List<BikePoloPlayer> playersInGame = drawPlayers(players, NUM_PLAYERS);
    	if (playersInGame.size()>0) {
    		// Any players in game - show dialog
    		NewGameDialog dialog = new NewGameDialog();
    		String buttonName = getString(R.string.nextGame);
        	currentGame = playersInGame;    		
    		dialog.show(getSupportFragmentManager(), buttonName);
    	}
    	// else - no players - do nothing    	 
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
    			Toast.makeText(getBaseContext(), getString(R.string.rankAdjusted),
    					Toast.LENGTH_SHORT).show();
    		}
    	}
    	player.setPlays(newPlayState);
    	dataSource.updatePlayer(player);
    	redrawPlayers();
    }
    
    private void menuResetGameCntClick() {
		Toast.makeText(getBaseContext(), getString(R.string.resetGameCntResult),
				Toast.LENGTH_SHORT).show();
    	for (int i=0; i<players.size(); i++) {
    		BikePoloPlayer player = players.get(i); 
    		player.resetGames();
    		dataSource.updatePlayer(player);
    	}
    	redrawPlayers();
    }


    private void menuRemoveAllPlayersClick() {
		Toast.makeText(getBaseContext(), getString(R.string.removeAllPlayersResult),
				Toast.LENGTH_SHORT).show();
    	dataSource.deleteAllPlayers();
    	redrawPlayers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle item selection
    	switch (item.getItemId()) {
    	case R.id.menuResetGameCnt:
    		//menuResetGameCntClick();   		
    		Resources res = getResources();
    		String piwo = res.getDrawable(R.drawable.ic_action_cut).toString();
    		Toast.makeText(getApplicationContext(), piwo, Toast.LENGTH_SHORT);
    		return true;
    	case R.id.menuClearPlayerList:
    		menuRemoveAllPlayersClick();
    		return true;
    	case R.id.menuShowLastGame:
        	if (currentGame.size()>0) {
        		// Any players in game - show dialog
        		LastGameDialog dialog = new LastGameDialog();
        		String buttonName = getString(R.string.nextGame);
        		dialog.show(getSupportFragmentManager(), buttonName);
        	}
        	// else - no players - do nothing    	     		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
}

