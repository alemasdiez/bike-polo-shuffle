package org.kkdt.bike.polo.randomizer;

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

public class AddPlayer extends DialogFragment {
	
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
        if (parameters != null) {
        	if (parameters.containsKey(RandomizerMain.PLAYER_NAME)) { // Input already started
        		String playerName = parameters.getString(RandomizerMain.PLAYER_NAME);
     	   		editText.setText(playerName);
        	}
        }
        editText.setOnEditorActionListener(new OnEditorActionListener(){        	
        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        		if (actionId == EditorInfo.IME_ACTION_DONE) {
             	   EditText editText = (EditText) dialogView.findViewById(R.id.editAddPlayer);
             	   String name = editText.getText().toString();
             	   if (name.length() > 0) {
                 	   RandomizerMain parentActivity = (RandomizerMain) getActivity();
             		   parentActivity.addPlayer(name);
             		   dialogF.getDialog().dismiss();
             	   }             	   
        		}
        		return true;
        	}
        });
        builder.setView(dialogView);
        builder.setTitle(R.string.addPlayer)
               .setPositiveButton(R.string.addPlayer, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   EditText editText = (EditText) dialogView.findViewById(R.id.editAddPlayer);
                	   String name = editText.getText().toString();
                	   if (name.length() > 0) {
                    	   RandomizerMain parentActivity = (RandomizerMain) getActivity();
                		   parentActivity.addPlayer(name);
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
		RandomizerMain activity = (RandomizerMain)this.getActivity();
		if (activity != null) { // activity still exists. Is null when dialog is dismissed
								// due to activity destroyed.
			activity.clearDialog();
		}
	}
	
	public String getInputName() {
		
		EditText editText = (EditText) getDialog().findViewById(R.id.editAddPlayer);
		return editText.getText().toString();
	}
}
