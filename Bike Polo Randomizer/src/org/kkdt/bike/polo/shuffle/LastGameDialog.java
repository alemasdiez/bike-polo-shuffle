package org.kkdt.bike.polo.shuffle;



import java.util.List;

import org.kkdt.bike.polo.shuffle.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LastGameDialog extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ShuffleMain parentAct = (ShuffleMain)getActivity(); // parent activity
        
        // extract dialog parameters
        String closeDialogString = getString(android.R.string.ok);

        // prepare dialog view
        LayoutInflater inflater = parentAct.getLayoutInflater();	
        final View dialogView = inflater.inflate(R.layout.dialog_new_game_land, null);

        // get common App data
        BikePoloShuffleApp app = (BikePoloShuffleApp) getActivity().getApplication();	
        
        // get both columns
        
        final List<BikePoloPlayer> playerListDialogL = app.getLatestGame(true);
        int i=0;
        String[] playerListLeft = new String[playerListDialogL.size()];        
        for (BikePoloPlayer player : playerListDialogL) {
			playerListLeft[i++] = player.getName(); 
		}
        final List<BikePoloPlayer> playerListDialogR = app.getLatestGame(false);
        i=0;
        String[] playerListRight = new String[playerListDialogR.size()];        
        for (BikePoloPlayer player : playerListDialogR) {
			playerListRight[i++] = player.getName(); 
		}
        ArrayAdapter<String> dAdapterL = new ArrayAdapter<String>(dialogView.getContext(),
        			R.layout.list_new_game_item,  playerListLeft);
        ListView dListViewL = (ListView) dialogView.findViewById(R.id.newGameListViewLeft);
        dListViewL.setAdapter(dAdapterL);
        ArrayAdapter<String> dAdapterR = new ArrayAdapter<String>(dialogView.getContext(),
        		R.layout.list_new_game_item,  playerListRight);
        ListView dListViewR = (ListView) dialogView.findViewById(R.id.newGameListViewRight);
        dListViewR.setAdapter(dAdapterR);        

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());        
        builder.setView(dialogView)
        	   .setTitle(R.string.lastGameTitle)        		 	   
               .setPositiveButton(closeDialogString, new DialogInterface.OnClickListener() {
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
		ShuffleMain activity = (ShuffleMain)this.getActivity();
		if (activity != null) { // activity still exists. Is null when dialog is dismissed
								// due to activity destroyed.
			activity.clearDialog();
		}
	}
		    
}
