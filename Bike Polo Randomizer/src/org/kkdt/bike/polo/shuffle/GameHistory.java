package org.kkdt.bike.polo.shuffle;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TwoLineListItem;

public class GameHistory extends Activity {
	private PlayerDBDataSource dataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataSource = new PlayerDBDataSource(this);
		dataSource.open();		

		setContentView(R.layout.activity_game_history);

		List<BikePoloGame> gameData = dataSource.getAllGames(); 
		ArrayAdapter<BikePoloGame> adapter = new ArrayAdapter<BikePoloGame>(this,android.R.layout.simple_list_item_2,gameData){
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				TwoLineListItem row;
				if(convertView == null){
					LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					row = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);
				}else{
					row = (TwoLineListItem)convertView;
				}
				BikePoloGame game = this.getItem(position);
				row.getText1().setText(game.toString());
				String playerListOut = "";
				for (int playerId: game.getPlayers(BikePoloGame.LEFT)) {
					String id = Integer.toString(playerId);
					BikePoloPlayer player = dataSource.getPlayer(id);
					if (player != null) {
						playerListOut += player.getName() + ", ";
					}
				}
				playerListOut += "\n";
				for (int playerId: game.getPlayers(BikePoloGame.RIGHT)) {
					String id = Integer.toString(playerId);
					BikePoloPlayer player = dataSource.getPlayer(id);
					if (player != null) {
						playerListOut += player.getName() + ", ";
					}
				}				
				if (playerListOut.length()>2) {
					playerListOut = playerListOut.substring(0, playerListOut.length()-2); // cut last ", "
				}
				row.getText2().setText(playerListOut);
				return row;
			}
		};
		int id = R.id.gameHistoryList;
		View view = findViewById(id);
		ListView listGamesView = (ListView) view;
		listGamesView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
		dataSource.open();
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

}
