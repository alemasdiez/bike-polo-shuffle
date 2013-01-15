package org.kkdt.bike.polo.shuffle;

import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentLatestGame extends DialogFragment {	
	public static final String DIALOG_TYPE = "DIALOG_TYPE";
	public static final int NO_DIALOG = 0;
	public static final int NEW_GAME = 1;
	public static final int LATEST_GAME = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	}	
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final DialogFragment fragment = this;
		// extract dialog parameters
		boolean useTimer = false;
		if ((ShuffleMain.getSettings(ShuffleMain.USE_TIMER) == ShuffleMain.YES)) {
			useTimer = true;
		}
        Bundle parameters = this.getArguments();
        int dialogType = 0; // Not in dialog
        if (parameters != null) {
        	if (parameters.containsKey(DIALOG_TYPE)) {
        		dialogType = parameters.getInt(DIALOG_TYPE);
        	}
        }
        // get common App data
        BikePoloShuffleApp app = (BikePoloShuffleApp) getActivity().getApplication();	
        
        View view; // prepare fragment view
		if (useTimer) {
			view = inflater.inflate(R.layout.fragment_latest_game_timer, container, false);
		} else {
			view = inflater.inflate(R.layout.fragment_latest_game, container, false);
		}
		
		// Prepare title and buttons
		switch (dialogType) {
		case NEW_GAME:
			getDialog().setTitle(R.string.titleNextGame);
			LinearLayout buttonHandler = (LinearLayout) view.findViewById(R.id.buttonHandler);
			Button Button1 = new Button(getDialog().getContext());
			Button1.setText(R.string.startGame);
			Button1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					ShuffleMain parentAct = (ShuffleMain)getActivity(); // parent activity
					parentAct.startGame(true); // start with timer
					fragment.dismiss();					
				}
			});
			buttonHandler.addView(Button1);
			Button Button2 = new Button(getDialog().getContext());
			Button2.setText(android.R.string.cancel);
			Button2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {					
					fragment.dismiss();
				}
			});
			buttonHandler.addView(Button2);			
			if (app.isTimerOngoing()) { // Timer already ongoing
				app.stopTimer();
			}
			break;
		case LATEST_GAME:
			getDialog().setTitle(R.string.titleLatestGame);
			getDialog().setCanceledOnTouchOutside(true);
			buttonHandler = (LinearLayout) view.findViewById(R.id.buttonHandler);
			if ((useTimer) && (app.isTimerOngoing())) { // Timer already ongoing
				Button1 = new Button(getDialog().getContext());
				Button1.setText(R.string.finishGame);
				Button1.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						ShuffleMain parentAct = (ShuffleMain)getActivity(); // parent activity
						BikePoloShuffleApp localApp = (BikePoloShuffleApp) parentAct.getApplication();
						localApp.stopTimer();
						fragment.dismiss();					
					}
				});
				buttonHandler.addView(Button1);
			}
			Button2 = new Button(getDialog().getContext());
			Button2.setText(android.R.string.ok);
			Button2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					fragment.dismiss();					
				}
			});
			buttonHandler.addView(Button2);
			break;
		default:
		}
		
		if (useTimer) { // prepare timer
			TextView timerOutput = (TextView) view.findViewById(R.id.timerOutput);
			switch (dialogType) {
			case NEW_GAME:
				int defaultTime = 1;// ShuffleMain.getSettings(ShuffleMain.DEFAULT_GAME_TIME);
				timerOutput.setText(Integer.toString(defaultTime) + ":00");
				app.setTimer(defaultTime, timerOutput);
				break;
			case LATEST_GAME:
				app.setTimerOutput(timerOutput);
			}
		}

		// prepare team columns       
		final List<BikePoloPlayer> teamListL = app.getLatestGame(true);
		final List<BikePoloPlayer> teamListR = app.getLatestGame(false);
		ArrayAdapter<BikePoloPlayer> dAdapterL = new ArrayAdapter<BikePoloPlayer>(view.getContext(),
				R.layout.list_new_game_item,  teamListL);		
		ListView dListViewL = (ListView) view.findViewById(R.id.listViewTeamL);
				ArrayAdapter<BikePoloPlayer> dAdapterR = new ArrayAdapter<BikePoloPlayer>(view.getContext(),
				R.layout.list_new_game_item,  teamListR);				
		ListView dListViewR = (ListView) view.findViewById(R.id.listViewTeamR);
		if (dialogType == NEW_GAME) { // Add possibility to exchange players
			dListViewL.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					TextView tv = (TextView)view;
					String playerName = (String) tv.getText();
					ShuffleMain parentAct = (ShuffleMain)getActivity(); // parent activity
					BaseAdapter adapter = (BaseAdapter)parent.getAdapter();
					boolean playersLeft = parentAct.changeCurrentPlayer(playerName, true, adapter);
					if (!playersLeft) { // No players left, cancel dialog
						fragment.dismiss();
					}
					return true;
				}
			}
					);
			dListViewR.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					TextView tv = (TextView)view;
					String playerName = (String) tv.getText();
					ShuffleMain parentAct = (ShuffleMain)getActivity(); // parent activity
					BaseAdapter adapter = (BaseAdapter)parent.getAdapter();
					boolean playersLeft = parentAct.changeCurrentPlayer(playerName, false, adapter);
					if (!playersLeft) { // No players left, cancel dialog
						fragment.dismiss();
					}
					return true;
				}
			}
					);
		}
		dListViewL.setAdapter(dAdapterL);
		dListViewR.setAdapter(dAdapterR);        

		return view;
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
