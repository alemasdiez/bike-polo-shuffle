package org.kkdt.bike.polo.randomizer;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NextGameDialog extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DialogFragment dialogFragment = this;
        Bundle parameters = this.getArguments();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_game_land, null);
        String[] playerListTmp = parameters.getStringArray(RandomizerMain.PLAYER_LIST);
        int halfNumPlayers = (int)Math.ceil((double)playerListTmp.length/2);        
        String[] playerListLeft = new String[halfNumPlayers];
        String[] playerListRight = new String[playerListTmp.length - halfNumPlayers];
        for (int i=0;i<playerListTmp.length;i++) {
        	if (i<halfNumPlayers) {
        		playerListLeft[i] = playerListTmp[i];
        	} else {
        		playerListRight[i-halfNumPlayers] = playerListTmp[i];
        	}
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayAdapter<String> dAdapterL = new ArrayAdapter<String>(dialogView.getContext(),
        			R.layout.list_new_game_item,  playerListLeft);
        ListView dListViewL = (ListView) dialogView.findViewById(R.id.newGameListViewLeft);
        dListViewL.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		dialogFragment.dismiss();
        	}
        }
        );
        dListViewL.setAdapter(dAdapterL);
        ArrayAdapter<String> dAdapterR = new ArrayAdapter<String>(dialogView.getContext(),
        		R.layout.list_new_game_item,  playerListRight);
        ListView dListViewR = (ListView) dialogView.findViewById(R.id.newGameListViewRight);
        dListViewR.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		dialogFragment.dismiss();
        	}
        }
        		);
        dListViewR.setAdapter(dAdapterR);        
        builder.setView(dialogView);        
        builder.setTitle(R.string.nextGameTitle)        		 	   
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                   }
               }               
               );
        // Create the AlertDialog object and return it        
        return builder.create();
    }
		    
}
