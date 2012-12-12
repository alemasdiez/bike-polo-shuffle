package org.kkdt.bike.polo.randomizer;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NextGameDialog extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle parameters = this.getArguments();
        CharSequence[] playerList = parameters.getStringArray(RandomizerMain.PLAYER_LIST);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(playerList, new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int id) {
                   }
               }
               );        
//        .setTitle(R.string.nextGame)        		 	   
//               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                   public void onClick(DialogInterface dialog, int id) {
//                   }
//               }               
//               );
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
}
