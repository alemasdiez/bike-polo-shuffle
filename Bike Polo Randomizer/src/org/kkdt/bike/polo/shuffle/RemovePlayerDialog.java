package org.kkdt.bike.polo.shuffle;



import org.kkdt.bike.polo.shuffle.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RemovePlayerDialog extends DialogFragment {
	private String playerName;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle parameters = this.getArguments();
        playerName = parameters.getString(ShuffleMain.PLAYER_NAME);
        String title = getString(R.string.removePlayer) + " " + playerName + "?";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)        		 	   
        		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {        				
        		        ShuffleMain parentActivity = (ShuffleMain)getActivity();
        		        parentActivity.removePlayer(playerName);        		        
        			}
        		}               
        		)
        		.setNegativeButton(android.R.string.cancel, null);
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
	
	public String getRemovedName() {
		return playerName;
	}
	
}
