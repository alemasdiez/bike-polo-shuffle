package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class ShuffleMain extends FragmentActivity {
	public static final String PLAYER_NAME = "PLAYER_NAME";
	public static final String PLAYER_LIST_L = "PLAYER_LIST_L";
	public static final String PLAYER_LIST_R = "PLAYER_LIST_R";
	public static final String PLAYER_NEXT_LIST_L = "PLAYER_NEXT_LIST_L";
	public static final String PLAYER_NEXT_LIST_R = "PLAYER_NEXT_LIST_R";
	public static final String PLAYER_BLACK_LIST = "PLAYER_BLACK_LIST";
	public static final String PLAYER_NUMBER = "PLAYER_NUMBER";
	public static final String TEAM_NAME = "TEAM_NAME";
	
	private static final String SUPER_ARIEL = "Super Ariel";
	private static final String CURRENT_DIALOG = "CURRENT_DIALOG";
	private static final boolean LEFT = true;
	private static final boolean RIGHT = false;
	private static final int NUM_PLAYERS = 6;	
	private static final int NO_DIALOG = 0;
	private static final int NEW_GAME = 1;
	private static final int LATEST_GAME = 2;
	private static final int REMOVE_PLAYER = 3;
	private static final int ADD_PLAYER = 4;
	private static final int NEXT_GAME = 5;
	private static final int NEW_TEAM = 6;
	private BikePoloDBDataSource dataSource;
	private List<BikePoloPlayer> players = new ArrayList<BikePoloPlayer>();
	private List<BikePoloPlayer> blackList = new ArrayList<BikePoloPlayer>();
	private RemovePlayerDialog dialogRemovePlayer = new RemovePlayerDialog();
	private FragmentLatestGame newGame = new FragmentLatestGame();
	private FragmentLatestGame latestGame = new FragmentLatestGame();
    private AddPlayer dialogAddPlayer = new AddPlayer();
    private NewTeam dialogNewTeam = new NewTeam();
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
			ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.ViewFlipperPlayerList);
			TextView playerName = (TextView) v.findViewById(R.id.playerNameOnList);
			TextView playerGames = (TextView) v.findViewById(R.id.gamesPlayedOnList);
			TextView playerNamev2 = (TextView) v.findViewById(R.id.playerNameOnListv2);
			TextView playerTeam = (TextView) v.findViewById(R.id.teamOnList);
			ImageView playerTeamImg = (ImageView) v.findViewById(R.id.imgTeam);
			CheckBox playerInGamev2 = (CheckBox) v.findViewById(R.id.checkboxInPlayv2);
			Spinner teamSelect = (Spinner) v.findViewById(R.id.teamSelectOnList);
			ImageView playerRemoveImg = (ImageView) v.findViewById(R.id.removePlayerOnList);
			
			MySwipeListener swiper = new MySwipeListener(v) {
				
				public void onLeftSwipe(View v) {
					int whichPlayer = (Integer)v.getTag();
					ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.ViewFlipperPlayerList);
					if (vf != null) {
						BikePoloPlayer player = players.get(whichPlayer);
						player.switchView(vf, LEFT);
						dataSource.updatePlayer(player);
					} 								
				}
				public void onRightSwipe(View v) {
					int whichPlayer = (Integer)v.getTag();
					ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.ViewFlipperPlayerList);
					if (vf != null) {
						BikePoloPlayer player = players.get(whichPlayer);
						player.switchView(vf, RIGHT);
						dataSource.updatePlayer(player);
					} 					
				}
				public void onRightSwipeAttempt(View v) {
					int whichPlayer = (Integer)v.getTag();
					ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.ViewFlipperPlayerList);
					if (vf != null) {
						BikePoloPlayer player = players.get(whichPlayer);
						player.switchViewAttempt(vf, RIGHT);						
					} 										
				}
				public void onLeftSwipeAttempt(View v) {
					int whichPlayer = (Integer)v.getTag();
					ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.ViewFlipperPlayerList);
					if (vf != null) {
						BikePoloPlayer player = players.get(whichPlayer);
						player.switchViewAttempt(vf, LEFT);						
					} 										
				}

			}; 
			v.setTag(position);
			v.setOnTouchListener(swiper);
			teamSelect.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {				
				public void onItemSelected(AdapterView<?> parent, View view, 
			            int pos, long id) {
					String teamSelected = (String)parent.getItemAtPosition(pos);
					int playerPos = (Integer)parent.getTag();
					BikePoloPlayer player = players.get(playerPos);
					String currentTeam = player.getTeamName();
					if (teamSelected == getString(R.string.noTeam)) {
						teamSelected = ""; // no team selected
					}
					if (teamSelected == getString(R.string.newTeam)) {
						// define new team dialog
	        			Bundle dialogParams = new Bundle();
	        			dialogParams.putInt(PLAYER_NUMBER, playerPos);
	        			dialogNewTeam.setArguments(dialogParams);            
	        			dialogNewTeam.show(getSupportFragmentManager(), getString(R.string.addPlayer));
	        			currentDialog = NEW_TEAM;
						//storage will be done in setNewTeam function invoked from dialog
					} else if (! teamSelected.equals(currentTeam)) {
						if (teamSelected.equals("")) {
							teamSelected = null; // change to internal representation
						}
						player.setTeamName(teamSelected);
						dataSource.updatePlayer(player);
						redrawPlayers();
					} // else no change - do nothing
			    }

			    public void onNothingSelected(AdapterView<?> parent) {
			        // Nothing to do. No change in team.
			    }	
			});
			// Prepare player data
			String playerNameValue = shownPlayer.getName();			
			String playerGamesValue = Integer.toString(shownPlayer.getGames());		
			if (shownPlayer.getHandicap() > 0) {
				playerGamesValue += " (+" + shownPlayer.getHandicap() + ")";
			}
			boolean ifPlaysValue = shownPlayer.ifPlays();
			String playerTeamName = shownPlayer.getTeamName();
			int playerTeamPosition = 0;
			List<String> allTeamNamesList = null;
			try {
				allTeamNamesList = dataSource.getAllTeamNames();
			} catch (IllegalStateException e) {
				//DB not yet ready, prepare empty list
				allTeamNamesList = new ArrayList<String>();
			}
			allTeamNamesList.add(0, getString(R.string.noTeam)); // No team indicator
			allTeamNamesList.add(getString(R.string.newTeam));
			if (playerTeamName != "") {
				playerTeamPosition = allTeamNamesList.indexOf(playerTeamName);
			}
			ArrayAdapter<String> allTeamNames = new ArrayAdapter<String>(this.getContext(), 
					android.R.layout.simple_spinner_item, allTeamNamesList);
			
			
			// Fill view 1
			playerName.setText(playerNameValue);
			if (ifPlaysValue) { // plays
				playerName.setPaintFlags(playerName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			} else { // does not play
				playerName.setPaintFlags(playerName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
			playerTeam.setText(playerTeamName);
			if (playerTeamName.equals("")) { // hide team icon if no team defined for player
				playerTeamImg.setVisibility(View.INVISIBLE);
			} else { // unhide
				playerTeamImg.setVisibility(View.VISIBLE);
			}
			playerGames.setText(playerGamesValue);
			
			// Fill view 2
			playerNamev2.setText(playerNameValue);			
			playerInGamev2.setChecked(ifPlaysValue);			
			playerInGamev2.setTag(position); // tag needed to find player
			allTeamNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			teamSelect.setAdapter(allTeamNames);
			teamSelect.setSelection(playerTeamPosition);
			teamSelect.setTag(position); // tag needed to find player
			playerRemoveImg.setTag(position);
			
			// select correct view
			if (shownPlayer.getModView()) {				
				vf.setDisplayedChild(1); 	// show player modification view					
			} else {
				vf.setDisplayedChild(0);	// show default player view				
			}
			
			return v;
		}
	}	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomizer_main);
        dataSource = new BikePoloDBDataSource(this);
		dataSource.open();		
		players = dataSource.getAllPlayers(false); // refresh players list, no team sorting

		if (savedInstanceState != null) {        	
    		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();
    		app.clearLatestGame();
    		List<BikePoloPlayer> latestGameL = new ArrayList<BikePoloPlayer>();
    		List<BikePoloPlayer> latestGameR = new ArrayList<BikePoloPlayer>();
    		List<BikePoloPlayer> nextGameL = new ArrayList<BikePoloPlayer>();
    		List<BikePoloPlayer> nextGameR = new ArrayList<BikePoloPlayer>();
        	if (savedInstanceState.containsKey(PLAYER_LIST_L)) { // latest game L team stored
        		latestGameL.clear();	// clear list
        		int[] playerIds = savedInstanceState.getIntArray(PLAYER_LIST_L);
        		for (int i=0; i<playerIds.length; i++) {
        			if (playerIds[i]>0) {
        				BikePoloPlayer player = dataSource.getPlayer(Integer.toString(playerIds[i]));
        				if (player != null) { // player found in db
        					latestGameL.add(player);
        				}
        			}         			
        		}
        	}
        	if (savedInstanceState.containsKey(PLAYER_LIST_R)) { // latest game R team stored
        		latestGameR.clear();	// clear list
        		int[] playerIds = savedInstanceState.getIntArray(PLAYER_LIST_R);
        		for (int i=0; i<playerIds.length; i++) {
        			if (playerIds[i]>0) {
        				BikePoloPlayer player = dataSource.getPlayer(Integer.toString(playerIds[i]));
        				if (player != null) { // player found in db
        					latestGameR.add(player);
        				}
        			}
        		}
        	}
        	app.setLatestGame(latestGameL, latestGameR);
        	if (savedInstanceState.containsKey(PLAYER_NEXT_LIST_L)) { // next game L team stored
        		nextGameL.clear();	// clear list
        		int[] playerIds = savedInstanceState.getIntArray(PLAYER_NEXT_LIST_L);
        		for (int i=0; i<playerIds.length; i++) {
        			if (playerIds[i]>0) {
        				BikePoloPlayer player = dataSource.getPlayer(Integer.toString(playerIds[i]));
        				if (player != null) { // player found in db
        					nextGameL.add(player);
        				}
        			}
        		}
        	}
        	if (savedInstanceState.containsKey(PLAYER_NEXT_LIST_R)) { // latest game R team stored
        		nextGameR.clear();	// clear list
        		int[] playerIds = savedInstanceState.getIntArray(PLAYER_NEXT_LIST_R);
        		for (int i=0; i<playerIds.length; i++) {
        			if (playerIds[i]>0) {
        				BikePoloPlayer player = dataSource.getPlayer(Integer.toString(playerIds[i]));
        				if (player != null) { // player found in db
        					nextGameR.add(player);
        				}
        			} 
        		}
        	}
        	app.setNextGame(nextGameL, nextGameR);
        	if (savedInstanceState.containsKey(PLAYER_BLACK_LIST)) { // black list stored
        		blackList.clear();	// clear list
        		int[] playerIds = savedInstanceState.getIntArray(PLAYER_BLACK_LIST);
        		for (int i=0; i<playerIds.length; i++) {
        			if (playerIds[i]>0) {
        				BikePoloPlayer player = dataSource.getPlayer(Integer.toString(playerIds[i]));
        				if (player != null) { // player found in db
        					blackList.add(player);
        				}
        			} 
        		}
        	}
        	if (savedInstanceState.containsKey(CURRENT_DIALOG)) {
        		// restore dialogs
        		currentDialog = savedInstanceState.getInt(CURRENT_DIALOG);
    			Bundle dialogParams = new Bundle();
        		switch (currentDialog) {
        		case NEW_GAME:
        			dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
        					FragmentLatestGame.NEW_GAME);
        			newGame.setArguments(dialogParams);            
        			newGame.show(getSupportFragmentManager(), getString(R.string.play));
        			break;
        		case LATEST_GAME:
        			dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
        					FragmentLatestGame.LATEST_GAME);
        			latestGame.setArguments(dialogParams);
        			latestGame.show(getSupportFragmentManager(), getString(R.string.play));       		
        			break;
        		case NEXT_GAME:
        			dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
        					FragmentLatestGame.NEXT_GAME);
        			newGame.setArguments(dialogParams);            
        			newGame.show(getSupportFragmentManager(), getString(R.string.nextGame));
        			break;
        		case REMOVE_PLAYER:
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
        		case NEW_TEAM:
        			dialogParams = new Bundle();
        			addedName = savedInstanceState.getString(TEAM_NAME);
        			int playerPos = savedInstanceState.getInt(PLAYER_NUMBER);
        			dialogParams.putString(TEAM_NAME, addedName);
        			dialogParams.putInt(PLAYER_NUMBER, playerPos);
        			dialogNewTeam.setArguments(dialogParams);            
        			dialogNewTeam.show(getSupportFragmentManager(), getString(R.string.addPlayer));
        			break;        			
        		default:	// no dialog
        			break;
        		}
        	}

        } else { // first run
        	currentDialog = NO_DIALOG;
        	for (BikePoloPlayer player : players) { // return all players to main view 
        		if (player.getModView()) {
        			player.switchView(null, LEFT); // no view to flip yet
        			dataSource.updatePlayer(player);
        		}
        	}
    		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();
    		app.cancelNotifications();
        }
        if (currentDialog == NO_DIALOG) { // check if activity started from notification
        	Intent recIntent = getIntent();
        	Bundle extras = recIntent.getExtras();
        	if (extras != null) {         
        		if (extras.containsKey(BikePoloShuffleApp.SHOW_LAST_GAME)) {
        			recIntent.removeExtra(BikePoloShuffleApp.SHOW_LAST_GAME);
        			currentDialog = LATEST_GAME;        		
        			Bundle dialogParams = new Bundle();
        			dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
        					FragmentLatestGame.LATEST_GAME);
        			latestGame.setArguments(dialogParams);
        			latestGame.show(getSupportFragmentManager(), getString(R.string.play));
        		}        	
        	}
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible
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
		List<BikePoloPlayer> latestGameL = app.getLatestGame(LEFT);
		List<BikePoloPlayer> latestGameR = app.getLatestGame(RIGHT);
		List<BikePoloPlayer> nextGameL = app.getNextGame(LEFT);
		List<BikePoloPlayer> nextGameR = app.getNextGame(RIGHT);
		
		if (latestGameL.size() + latestGameR.size() + nextGameL.size() + nextGameR.size() + 
				blackList.size() > 0) {
			// db will be needed
    		dataSource.open(); // may have been closed by onPause;    			
    		players = dataSource.getAllPlayers(false); // update players list, no team sorting
		}
    	if (latestGameL.size()>0) { // at least one game played
    		int[] currentGamePlayerIds = new int[latestGameL.size()];
    		int i = 0;
    		for (BikePoloPlayer player : latestGameL) {
    			currentGamePlayerIds[i++] = player.getIdInt(); 
    		}
    		outState.putIntArray(PLAYER_LIST_L, currentGamePlayerIds);
    	}
    	if (latestGameR.size()>0) { // at least one game played
    		int[] currentGamePlayerIds = new int[latestGameR.size()];
    		int i = 0;
    		for (BikePoloPlayer player : latestGameR) {
    			currentGamePlayerIds[i++] = player.getIdInt(); 
    		}
    		outState.putIntArray(PLAYER_LIST_R, currentGamePlayerIds);    		
    	}
    	if (nextGameL.size()>0) { // at least one game played
    		int[] currentGamePlayerIds = new int[nextGameL.size()];
    		int i = 0;
    		for (BikePoloPlayer player : nextGameL) {
    			currentGamePlayerIds[i++] = player.getIdInt(); 
    		}
    		outState.putIntArray(PLAYER_NEXT_LIST_L, currentGamePlayerIds);
    	}
    	if (nextGameR.size()>0) { // at least one game played
    		int[] currentGamePlayerIds = new int[nextGameR.size()];
    		int i = 0;
    		for (BikePoloPlayer player : nextGameR) {
    			currentGamePlayerIds[i++] = player.getIdInt(); 
    		}
    		outState.putIntArray(PLAYER_NEXT_LIST_R, currentGamePlayerIds);    		
    	}
    	if (blackList.size()>0) { // at least one player removed from last game
    		int[] currentGamePlayerIds = new int[blackList.size()];
    		int i = 0;
    		for (BikePoloPlayer player : blackList) {
    			currentGamePlayerIds[i++] = player.getIdInt(); 
    		}
    		outState.putIntArray(PLAYER_BLACK_LIST, currentGamePlayerIds);    		
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
    	case NEW_TEAM:
    		outState.putString(TEAM_NAME, dialogNewTeam.getInputName());
    		outState.putInt(PLAYER_NUMBER, dialogNewTeam.getPlayerNumber());
    		break;    		
    	default:
    		break;
    	}
    }
            
    private void redrawPlayers() {    	
    	boolean teamPlay = false;
    	BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();
		if (app.getSettings(BikePoloShuffleApp.TEAM_PLAY) == BikePoloShuffleApp.YES) {
			teamPlay = true;	
		}
        players = dataSource.getAllPlayers(teamPlay);         // get sorted results
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
    
    public void startGame(boolean useTimer) {
		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
    	List<BikePoloPlayer> playersInGameL = app.getNextGame(LEFT);
		List<BikePoloPlayer> playersInGameR = app.getNextGame(RIGHT);
		app.setLatestGame(playersInGameL, playersInGameR);
    	for (BikePoloPlayer tPlayer : playersInGameL) {
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}
    	for (BikePoloPlayer tPlayer : playersInGameR) {
			tPlayer.playGame();
			dataSource.updatePlayer(tPlayer);
		}
    	Time now = new Time();
    	now.setToNow();
		int duration = app.getSettings(BikePoloShuffleApp.DEFAULT_GAME_TIME);
    	BikePoloGame latestGame = new BikePoloGame(playersInGameL, playersInGameR,
    			now.format("%y-%m-%d %T"), duration); 
    	dataSource.insertGame(latestGame);
		playersInGameL = new ArrayList<BikePoloPlayer>(); // next game is moved to latest
		playersInGameR = new ArrayList<BikePoloPlayer>(); // clear next game lists
		app.setNextGame(playersInGameL, playersInGameR);
		redrawPlayers();
		clearDialog();
		app.cancelNotifications();
		if (useTimer) {
			app.startTimer();
		}
    }
        
    public boolean changeCurrentPlayer(String playerName, 
    		boolean leftTeam, BaseAdapter notify) {
    	BikePoloPlayer newPlayer = null;
		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
		List<BikePoloPlayer> nextGameL = app.getNextGame(LEFT);
		List<BikePoloPlayer> nextGameR = app.getNextGame(RIGHT);
    	List<BikePoloPlayer> changedList = app.getNextGame(leftTeam);    	
    	for (BikePoloPlayer tPlayer: changedList) {
    		if (tPlayer.getName() == playerName) {
    			List<BikePoloPlayer> otherPlayers = 
    					dataSource.getAllPlayers(false); // fetch all players list, no team sorting
    			otherPlayers.removeAll(nextGameL); // remove L team players
    			otherPlayers.removeAll(nextGameR); // remove R team players
    			otherPlayers.removeAll(blackList); // remove blacklisted players
    			blackList.add(tPlayer); // do not select this player again for this game
    			changedList.remove(tPlayer);
    			notify.notifyDataSetChanged();    			
    			List<BikePoloPlayer> newPlayerList = drawPlayers(otherPlayers, 1);
    			if (newPlayerList.size()>0) {
        			newPlayer = newPlayerList.get(0);
        			changedList.add(newPlayer);
        			notify.notifyDataSetChanged();
    			}
    			break;
    		}
    	}
    	if (nextGameL.size() + nextGameR.size() > 0) { // any player left in game
    		return true;
    	} else { // no players left
    		return false;
    	}
    		
    }
    
    public void removePlayer(String name) {
    	dataSource.deletePlayer(name);
    	redrawPlayers();
    }
        
    public void clearDialog() {
    	currentDialog = NO_DIALOG;
    }
        
    public void addPlayer() {
        String buttonName = getString(R.string.addPlayer);
        dialogAddPlayer.show(getSupportFragmentManager(), buttonName);
        currentDialog = ADD_PLAYER;
    }
    
    public void drawNextGame() {
    	BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();    		
    	boolean teamPlay = false;
    	if (app.getSettings(BikePoloShuffleApp.TEAM_PLAY) == BikePoloShuffleApp.YES) {
    		teamPlay = true;
    	}
    	players = dataSource.getAllPlayers(teamPlay); // refresh players list
    	blackList.clear(); // remove blacklist from previous game
    	List<BikePoloPlayer> nextGameL = app.getNextGame(LEFT);
    	List<BikePoloPlayer> nextGameR = app.getNextGame(RIGHT);

    	if (nextGameL.size() + nextGameR.size() == 0) { // next game not yet drawn
    		if (!teamPlay) { // single player draw
    			List<BikePoloPlayer> playersInGame = drawPlayers(players, NUM_PLAYERS);
    			if (playersInGame.size()>0) {    		// Any players in game    		
    				int halfNumPlayers = (int)Math.ceil((double)playersInGame.size()/2);        
    				for (int i=0;i<playersInGame.size();i++) {
    					if (i<halfNumPlayers) {
    						nextGameL.add(playersInGame.get(i));
    					} else {
    						nextGameR.add(playersInGame.get(i));
    					}
    				}
    			}
    		} else { // team draw
    			int teamSize = NUM_PLAYERS / 2;
    			List<BikePoloPlayer> playersInGame = drawPlayers(players, NUM_PLAYERS); // draw to check number of players available    			
				int halfNumPlayers = (int)Math.ceil((double)playersInGame.size()/2);        
    			playersInGame = drawPlayers(players, 1); // draw 1 player for L team
    			List<BikePoloPlayer> remainingPlayers = new ArrayList<BikePoloPlayer>();
    			if (playersInGame.size() == 1) { // player found
    				nextGameL.clear();
    				nextGameR.clear();
    				BikePoloPlayer selectedPlayer = playersInGame.get(0);
    				String selectedTeam = selectedPlayer.getTeamName();
    				for (BikePoloPlayer player : players) {
    					if (player != selectedPlayer) {
    						if (player.ifPlays()) {
    							if (player.getTeamName().equals(selectedTeam)) {
    								if (playersInGame.size() < teamSize) {
    									if (playersInGame.size() < halfNumPlayers) {
    										playersInGame.add(player);
    									}
    								}
    							}
    						}
    					}
    				}
    				nextGameL.addAll(playersInGame);
    				remainingPlayers.addAll(players);
    				remainingPlayers.removeAll(nextGameL);
    				playersInGame = drawPlayers(remainingPlayers,1); // draw 1 player for team 2
    				if (playersInGame.size() == 1) { // player found
    					selectedPlayer = playersInGame.get(0);
    					selectedTeam = selectedPlayer.getTeamName();
    					for (BikePoloPlayer player : players) {
    						if (player != selectedPlayer) {
        						if (player.ifPlays()) {
        							if (player.getTeamName().equals(selectedTeam)) {
        								if (playersInGame.size() < teamSize) {
        									if (playersInGame.size() < halfNumPlayers) {
        										if (!nextGameL.contains(player)) {
            										playersInGame.add(player);
            									}
        									}
    									}
    								}
    							}    						
    						}
    					}
    					nextGameR.addAll(playersInGame);
    					remainingPlayers.removeAll(nextGameR);
    					if (nextGameL.size() < teamSize) { // draw additional players
    						List<BikePoloPlayer> addPl = drawPlayers(remainingPlayers,teamSize - 
    								nextGameL.size()); 
    						if (addPl.size() > 0){
    							nextGameL.addAll(addPl);
    							remainingPlayers.removeAll(addPl);
    						}
    					}
    					if (nextGameR.size() < teamSize) { // draw additional players
    						List<BikePoloPlayer> addPl = drawPlayers(remainingPlayers,teamSize - 
    								nextGameR.size()); 
    						if (addPl.size() > 0){
    							nextGameR.addAll(addPl);
    							remainingPlayers.removeAll(addPl);
    						}
    					}
    				}
    			}
    		}
    	}
    	app.setNextGame(nextGameL, nextGameR);
    }
    
    public void newGameDialog() {
    	drawNextGame(); // draw next game if necessary
    	blackList.clear(); // remove blacklist from previous game
    	BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();    		
    	List<BikePoloPlayer> nextGameL = app.getNextGame(LEFT);
    	List<BikePoloPlayer> nextGameR = app.getNextGame(RIGHT);
    	if (nextGameL.size() + nextGameR.size() > 0) { // next game already drawn 
    		if (!app.isTimerOngoing()) { // No game in progress, so can start new one    			
    			String buttonName = getString(R.string.play);
    			Bundle dialogParams = new Bundle();
    			dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
    					FragmentLatestGame.NEW_GAME);
    			newGame.setArguments(dialogParams);            
    			newGame.show(getSupportFragmentManager(), buttonName);
    			currentDialog = NEW_GAME;
    		} else {
    			showToast(getString(R.string.finishGameFirst));
    		}
		
    	} else { // no next game drawn i.e. no players available
    		showToast(getString(R.string.addPlayersFirst));
    	}
    }

    public void previewNextGame() {
    	drawNextGame(); // draw next game if necessary
		// show next game dialog
		String buttonName = getString(R.string.nextGame);
		Bundle dialogParams = new Bundle();
		dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
				FragmentLatestGame.NEXT_GAME);
		newGame.setArguments(dialogParams);            
		newGame.show(getSupportFragmentManager(), buttonName);
		currentDialog = NEXT_GAME;    		
    }

    
    public void latestGame() {
		BikePoloShuffleApp app = (BikePoloShuffleApp) getApplication();	
		List<BikePoloPlayer> latestGameL = app.getLatestGame(LEFT);
		List<BikePoloPlayer> latestGameR = app.getLatestGame(RIGHT);
    	if (latestGameL.size() + latestGameR.size() > 0) { // any player in game
    		// Any players in game - show dialogLastGame
    		String buttonName = getString(R.string.play);
    		Bundle dialogParams = new Bundle();
			dialogParams.putInt(FragmentLatestGame.DIALOG_TYPE, 
					FragmentLatestGame.LATEST_GAME);
			latestGame.setArguments(dialogParams);            
			latestGame.show(getSupportFragmentManager(), buttonName);
    		currentDialog = LATEST_GAME;        		
    	} else {    	// else - no last game players
			showToast(getString(R.string.noGameHistory));
    	}
    }
    
    public void setNewTeam(int playerPos, String teamName) {
    	if (teamName.equals(getString(R.string.newTeam)) | 
    			teamName.equals(getString(R.string.noTeam))) {
    		showToast(getString(R.string.nameErr));
    		redrawPlayers();
    	} else {
    		BikePoloPlayer player = players.get(playerPos);
    		player.setTeamName(teamName);
    		dataSource.updatePlayer(player);
    		redrawPlayers();
    	}
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
    
    private void menuResetGamesClick() {
		showToast(getString(R.string.resetGamesResult));
    	for (int i=0; i<players.size(); i++) {
    		BikePoloPlayer player = players.get(i); 
    		player.resetGames();
    		dataSource.updatePlayer(player);
    	}
    	dataSource.deleteAllGames();
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
    	newGameDialog();    	 
    }    

    public void removePlayerClick(View img) {
    	int whichPlayer = (Integer)img.getTag();
		String dialogName = getString(R.string.removePlayer);
		Bundle dialogParams = new Bundle();
		dialogParams.putString(PLAYER_NAME, players.get(whichPlayer).getName());
		dialogRemovePlayer.setArguments(dialogParams);            
		dialogRemovePlayer.show(getSupportFragmentManager(), dialogName);
		currentDialog = REMOVE_PLAYER;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle item selection
    	switch (item.getItemId()) {
    	case R.id.menuNewGame:
    		newGameDialog();
    		return true;
    	case R.id.menuAddPlayer:
    		addPlayer();
    		return true;
    	case R.id.menuShowLastGame:
    		latestGame();
        	return true;
    	case R.id.menuGameHistory:
    		List<BikePoloGame> gameData = dataSource.getAllGames();
    		if (gameData.size() > 0) {
    			Intent intent = new Intent(this, GameHistory.class);
    			startActivity(intent);
    		} else {
    			showToast(getString(R.string.noGameHistory));
    		}
    		return true;    		
    	case R.id.menuGamePreview:
    		previewNextGame();
        	return true;    		
    	case R.id.menuResetGames:
    		menuResetGamesClick();   		
    		return true;
    	case R.id.menuClearPlayerList:
    		menuRemoveAllPlayersClick();
    		return true;
    	case R.id.menuSettings:
    		Intent intent = new Intent(this, SettingsActivity.class);
    		startActivity(intent);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void showToast(String text) {
    	Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }
}

