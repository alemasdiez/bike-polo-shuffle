package org.kkdt.bike.polo.randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
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
	private static final int MODE_RANDOM = 0;
	private static final int MODE_EVEN = 1;
	private static final String SUPER_ARIEL = "Super Ariel";
	
	private PlayerDBDataSource dataSource;
	private List<BikePoloPlayer> players = new ArrayList<BikePoloPlayer>();
		
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
        updatePlayers();
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
        
    private void updatePlayers() {
    	
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
        	updatePlayers();
    	}    	
    }
    
    private String[] drawPlayers(List<BikePoloPlayer> listPlayers) {
    	final int NUM_PLAYERS = 6;
    	List<BikePoloPlayer> playersInGame = new ArrayList<BikePoloPlayer>();    	
    	List<BikePoloPlayer> listActivePlayers = new ArrayList<BikePoloPlayer>();    	
    	for (int i=0;i<listPlayers.size();i++) {
    		BikePoloPlayer currPlayer = listPlayers.get(i);
    		if (currPlayer.ifPlays()) {
    			listActivePlayers.add(currPlayer);
    		}
    	}
    	if (listActivePlayers.size() > 0) {
    		int playersToDraw;
    		playersToDraw = NUM_PLAYERS;    			
    		playersInGame = drawRandomPlayers(playersToDraw, listActivePlayers, MODE_EVEN);    			
    	}
    	String[] playersInGameNames = new String[playersInGame.size()];
		for (int i=0; i< playersInGame.size(); i++) {
			BikePoloPlayer tPlayer = playersInGame.get(i);
			playersInGameNames[i] = tPlayer.getName();
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}
    	return playersInGameNames;
    }
    
    private List<BikePoloPlayer> drawRandomPlayers(int playersToDraw, List<BikePoloPlayer> playersInDraw, int mode) {
    	List<BikePoloPlayer> drawnPlayers = new ArrayList<BikePoloPlayer>();		 
		if (playersToDraw < playersInDraw.size()) {
	    	Random rand = new Random();
	    	if (mode == MODE_RANDOM) {
	    		while (drawnPlayers.size() < playersToDraw) {
	    			int playerNumber = rand.nextInt(playersInDraw.size());
	    			BikePoloPlayer drawnPlayer = playersInDraw.get(playerNumber);
	    			boolean alreadyDrawn = false;
	    			for (int j= 0; j < drawnPlayers.size(); j++) {
	    				if (drawnPlayers.get(j) == drawnPlayer) {
	    					alreadyDrawn = true;
	    					break;
	    				}
	    			}
	    			if (!alreadyDrawn) {
	    				drawnPlayers.add(drawnPlayer);				
	    			}
	    		}
	    	} else { // MODE_EVEN - players with least games play first
	    		List<BikePoloPlayer> minPlaying = 
	    				BikePoloPlayer.findMinPlaying(playersInDraw);
	    		if (minPlaying.size() < playersToDraw) { // all least playing ones are in the game, we need more
	    			int playersNextDraw = playersToDraw - minPlaying.size();
	    			for (int i=0; i<minPlaying.size(); i++) {
	    				playersInDraw.remove(minPlaying.get(i));
	    			}	    		
	    			drawnPlayers = drawRandomPlayers(playersNextDraw, playersInDraw, MODE_EVEN);
	    			drawnPlayers.addAll(minPlaying);
	    		} else { // more players at minimum level that we need, draw random out of them
	    			drawnPlayers = drawRandomPlayers(playersToDraw, minPlaying, MODE_RANDOM);
	    		}
	    	}
		}
		else { // playersToDraw >= playersInDraw.size so all can play
			drawnPlayers = playersInDraw; 
		}	    		
		return drawnPlayers;
    }
        
    public void removePlayer(String name) {
    	dataSource.deletePlayer(name);
    	updatePlayers();
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
    	String[] playersInGame = drawPlayers(players);
    	if (playersInGame.length>0) {
    		// Any players in game - show dialog with results
    		NextGameDialog dialog = new NextGameDialog();
    		String buttonName = getString(R.string.nextGame);
    		Bundle dialogParams = new Bundle();
    		dialogParams.putStringArray(PLAYER_LIST, playersInGame);
    		dialog.setArguments(dialogParams);            
    		dialog.show(getSupportFragmentManager(), buttonName);
    		updatePlayers();
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
    	updatePlayers();
    }
    
    private void menuResetGameCntClick() {
		Toast.makeText(getBaseContext(), getString(R.string.resetGameCntResult),
				Toast.LENGTH_SHORT).show();
    	for (int i=0; i<players.size(); i++) {
    		BikePoloPlayer player = players.get(i); 
    		player.resetGames();
    		dataSource.updatePlayer(player);
    	}
    	updatePlayers();
    }


    private void menuRemoveAllPlayersClick() {
		Toast.makeText(getBaseContext(), getString(R.string.removeAllPlayersResult),
				Toast.LENGTH_SHORT).show();
    	dataSource.deleteAllPlayers();
    	updatePlayers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle item selection
    	switch (item.getItemId()) {
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
}

