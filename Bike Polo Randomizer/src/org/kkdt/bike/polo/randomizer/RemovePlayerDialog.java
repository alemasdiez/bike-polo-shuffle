package org.kkdt.bike.polo.randomizer;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RemovePlayerDialog extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle parameters = this.getArguments();
        final String playerName = parameters.getString(RandomizerMain.PLAYER_NAME);
        String title = getString(R.string.removePlayer) + " " + playerName + "?";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)        		 	   
        		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {        				
        		        RandomizerMain parentActivity = (RandomizerMain)getActivity();
        		        parentActivity.removePlayer(playerName);        		        
        			}
        		}               
        		)
        		.setNegativeButton(android.R.string.cancel, null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
}
