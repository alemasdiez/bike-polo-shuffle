package org.kkdt.bike.polo.randomizer;



import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewGameDialog extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RandomizerMain parentAct = (RandomizerMain)getActivity(); // parent activity
        
        // extract dialog parameters
        String startGameString = getString(android.R.string.ok);
        if ((RandomizerMain.getSettings(RandomizerMain.USE_TIMER) == RandomizerMain.YES)) {
        	startGameString = getString(R.string.startTimer);
        }

        // prepare dialog view
        LayoutInflater inflater = parentAct.getLayoutInflater();	
        final View dialogView = inflater.inflate(R.layout.dialog_new_game_land, null);
        
        // split list into two columns
        final List<BikePoloPlayer> playerListDialog = parentAct.getCurrentGame();       
        int halfNumPlayers = (int)Math.ceil((double)playerListDialog.size()/2);        
        String[] playerListLeft = new String[halfNumPlayers];
        String[] playerListRight = new String[playerListDialog.size() - halfNumPlayers];
        for (int i=0;i<playerListDialog.size();i++) {
        	if (i<halfNumPlayers) {
        		playerListLeft[i] = playerListDialog.get(i).getName();
        	} else {
        		playerListRight[i-halfNumPlayers] = playerListDialog.get(i).getName();
        	}
        }
        ArrayAdapter<String> dAdapterL = new ArrayAdapter<String>(dialogView.getContext(),
        			R.layout.list_new_game_item,  playerListLeft);
        ListView dListViewL = (ListView) dialogView.findViewById(R.id.newGameListViewLeft);
        dListViewL.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        		TextView tv = (TextView)view;
        		String playerName = (String) tv.getText();
        		playerName = parentAct.changeCurrentPlayer(playerName);
        		tv.setText(playerName);
        		return true;
        	}
        }
        );
        dListViewL.setAdapter(dAdapterL);
        ArrayAdapter<String> dAdapterR = new ArrayAdapter<String>(dialogView.getContext(),
        		R.layout.list_new_game_item,  playerListRight);
        ListView dListViewR = (ListView) dialogView.findViewById(R.id.newGameListViewRight);
        dListViewR.setOnItemLongClickListener(new OnItemLongClickListener() {
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        		TextView tv = (TextView)view;
        		String playerName = (String) tv.getText();
        		playerName = parentAct.changeCurrentPlayer(playerName);
        		tv.setText(playerName);
        		return true;
        	}
        }
        );
        dListViewR.setAdapter(dAdapterR);        

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());        
        builder.setView(dialogView)
        	   .setTitle(R.string.nextGameTitle)        		 	   
               .setPositiveButton(startGameString, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   if (RandomizerMain.getSettings(RandomizerMain.USE_TIMER) 
                			   == RandomizerMain.YES) {
                		   parentAct.startGame(playerListDialog, true); // start with timer
                	   } else {
                		   parentAct.startGame(playerListDialog, false); // start without timer
                	   }
                   }
               }               
               )
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            	   public void onClick(DialogInterface dialog, int id) {
            	   }
               }
               );        
        // Create the AlertDialog object and return it        
        return builder.create();
    }

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		RandomizerMain activity = (RandomizerMain)this.getActivity();
		if (activity != null) { // activity still exists. Is null when dialog is dismissed
								// due to activity destroyed.
			activity.clearDialog();
		}
	}

}
