package org.kkdt.bike.polo.randomizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
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
	
	private PlayerDBDataSource dataSource;
	private List<PlayerBikePolo> players = new ArrayList<PlayerBikePolo>();
		
	private class PlayerAdapter extends ArrayAdapter<PlayerBikePolo> {		
		
		PlayerAdapter(Context context, List<PlayerBikePolo> players) {
			super(context, R.layout.list_player_item, players);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v==null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_player_item, null);
			}			
			PlayerBikePolo shownPlayer = this.getItem(position);
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
    		PlayerBikePolo newPlayer = new PlayerBikePolo(playerName); 
        	dataSource.insertPlayer(newPlayer);
        	updatePlayers();
    	}    	
    }
    
    private String[] drawPlayers(List<PlayerBikePolo> listPlayers) {
    	final int NUM_PLAYERS = 6;
    	List<PlayerBikePolo> playersInGame = new ArrayList<PlayerBikePolo>();    	
    	List<PlayerBikePolo> listActivePlayers = new ArrayList<PlayerBikePolo>();    	
    	for (int i=0;i<listPlayers.size();i++) {
    		PlayerBikePolo currPlayer = listPlayers.get(i);
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
			PlayerBikePolo tPlayer = playersInGame.get(i);
			playersInGameNames[i] = tPlayer.getName();
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}
    	return playersInGameNames;
    }
    
    private List<PlayerBikePolo> drawRandomPlayers(int playersToDraw, List<PlayerBikePolo> playersInDraw, int mode) {
    	List<PlayerBikePolo> drawnPlayers = new ArrayList<PlayerBikePolo>();		 
		if (playersToDraw < playersInDraw.size()) {
	    	Random rand = new Random();
	    	if (mode == MODE_RANDOM) {
	    		while (drawnPlayers.size() < playersToDraw) {
	    			int playerNumber = rand.nextInt(playersInDraw.size());
	    			PlayerBikePolo drawnPlayer = playersInDraw.get(playerNumber);
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
	    		List<PlayerBikePolo> minPlaying = findMinPlaying(playersInDraw);
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
    
    private List<PlayerBikePolo> findMinPlaying(List<PlayerBikePolo> allPlayers) {
    	List<PlayerBikePolo> minPlayers = new ArrayList<PlayerBikePolo>();
    	if (allPlayers.size()>0) {
    		int minGames = allPlayers.get(0).getGames();
    		for (int i=0; i<allPlayers.size(); i++) {
    			PlayerBikePolo tPlayer = allPlayers.get(i);
    			if (tPlayer.getGames() < minGames) {
    				minGames = tPlayer.getGames();
    				minPlayers.removeAll(minPlayers);
    				minPlayers.add(tPlayer);
    			} else if (tPlayer.getGames() == minGames) {
    				minPlayers.add(tPlayer);
    			}
    				
    		}
    	}    	
    	return minPlayers;
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
    	PlayerBikePolo player = players.get(clickedPlayerPosition); 
    	player.setPlays(newPlayState);
    	dataSource.updatePlayer(player);
    }
    
    private void menuResetGameCntClick() {
		Toast.makeText(getBaseContext(), getString(R.string.resetGameCntResult),
				Toast.LENGTH_SHORT).show();
    	for (int i=0; i<players.size(); i++) {
    		PlayerBikePolo player = players.get(i); 
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

