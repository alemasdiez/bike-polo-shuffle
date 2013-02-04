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
	public static final int NEXT_GAME = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	}	
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final DialogFragment fragment = this;
        // get common App data
        final BikePoloShuffleApp app = (BikePoloShuffleApp) getActivity().getApplication();	
		// extract dialog parameters
        Bundle parameters = this.getArguments();
        int dialogType = 0; // Not in dialog
        if (parameters != null) {
        	if (parameters.containsKey(DIALOG_TYPE)) {
        		dialogType = parameters.getInt(DIALOG_TYPE);
        	}
        }
		boolean useTimer = false;
		if (dialogType != NEXT_GAME) { // next game dialog do not use timer
			if ((app.getSettings(BikePoloShuffleApp.USE_TIMER)
					== BikePoloShuffleApp.YES)) {
				useTimer = true;
			}
		}
        
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
					if (parentAct != null) {
						BikePoloShuffleApp localApp = (BikePoloShuffleApp) parentAct.getApplication();
						if (localApp != null) {
							// extract dialog parameters
							boolean useTimer = false;
							if ((localApp.getSettings(BikePoloShuffleApp.USE_TIMER)
									== BikePoloShuffleApp.YES)) {
								useTimer = true;
							}
							parentAct.startGame(useTimer); // start game
						}
					}
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
			if (app.isTimerOngoing()) { // Timer already ongoing
				Button1 = new Button(getDialog().getContext());
				Button1.setText(R.string.finishGame);
				Button1.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						ShuffleMain parentAct = (ShuffleMain)getActivity(); // parent activity
						if (parentAct != null) {
							BikePoloShuffleApp localApp = (BikePoloShuffleApp) parentAct.getApplication();
							if (localApp != null) {
								localApp.stopTimer();
							}
						}
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
		case NEXT_GAME:
			getDialog().setTitle(R.string.nextGame);
			getDialog().setCanceledOnTouchOutside(true);
			buttonHandler = (LinearLayout) view.findViewById(R.id.buttonHandler);
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
				int defaultTime = app.getSettings(BikePoloShuffleApp.DEFAULT_GAME_TIME);
				timerOutput.setText(Integer.toString(defaultTime) + ":00");
				app.setTimer(defaultTime, timerOutput);
				break;
			case LATEST_GAME:
				app.setTimerOutput(timerOutput);
				break;
			}
		}

		// prepare team columns       
		final List<BikePoloPlayer> teamListL;
		final List<BikePoloPlayer> teamListR;
		switch (dialogType) {
		case NEW_GAME:
		case NEXT_GAME:
			teamListL = app.getNextGame(true);
			teamListR = app.getNextGame(false);
			break;
		case LATEST_GAME:
		default:
			teamListL = app.getLatestGame(true);
			teamListR = app.getLatestGame(false);
		}
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
