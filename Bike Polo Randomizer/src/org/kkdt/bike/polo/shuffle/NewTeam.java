package org.kkdt.bike.polo.shuffle;

import org.kkdt.bike.polo.shuffle.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewTeam extends DialogFragment {
	private int currentPos; // current player position
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_add_player, null);
		EditText editText = (EditText) dialogView.findViewById(R.id.editAddPlayer);
		final DialogFragment dialogF = this;
        // get parameters
        Bundle parameters = this.getArguments();
        int playerPos = -1; // player position in main activity
        if (parameters != null) {
        	if (parameters.containsKey(ShuffleMain.TEAM_NAME)) { // Input already started
        		String teamName = parameters.getString(ShuffleMain.TEAM_NAME);        		
     	   		editText.setText(teamName);
     	   		parameters.remove(ShuffleMain.TEAM_NAME);
        	}
        	if (parameters.containsKey(ShuffleMain.PLAYER_NUMBER)) { // Input already started
        		playerPos = parameters.getInt(ShuffleMain.PLAYER_NUMBER);        		
        	} else {
        		dialogF.dismiss();
        	}
        }
        currentPos = playerPos; // position to be used in inner classes
        editText.setOnEditorActionListener(new OnEditorActionListener(){        	
        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        		if (actionId == EditorInfo.IME_ACTION_DONE) {
             	   EditText editText = (EditText) dialogView.findViewById(R.id.editAddPlayer);
             	   String name = editText.getText().toString();
             	   if (name.length() > 0) {
                 	   ShuffleMain parentActivity = (ShuffleMain) getActivity();
             		   parentActivity.setNewTeam(currentPos, name);
             		   dialogF.getDialog().dismiss();
             	   }             	   
        		}
        		return true;
        	}
        });
        builder.setView(dialogView);
        builder.setTitle(R.string.newTeam)
               .setPositiveButton(R.string.addPlayer, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   EditText editText = (EditText) dialogView.findViewById(R.id.editAddPlayer);
                	   String name = editText.getText().toString();
                	   if (name.length() > 0) {
                    	   ShuffleMain parentActivity = (ShuffleMain) getActivity();
                		   parentActivity.setNewTeam(currentPos, name);
                	   }
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
	
	public String getInputName() {
		
		EditText editText = (EditText) getDialog().findViewById(R.id.editAddPlayer);
		return editText.getText().toString();
	}
	
	public int getPlayerNumber() {
		return currentPos;
	}
}
