package org.kkdt.bike.polo.shuffle;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentLatestGame extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_latest_game, container, false);

        // get common App data
        BikePoloShuffleApp app = (BikePoloShuffleApp) getActivity().getApplication();	
        
        // get both columns       
        final List<BikePoloPlayer> teamListL = app.getLatestGame(true);
		final List<BikePoloPlayer> teamListR = app.getLatestGame(false);
		ArrayAdapter<BikePoloPlayer> dAdapterL = new ArrayAdapter<BikePoloPlayer>(view.getContext(),
				R.layout.list_new_game_item,  teamListL);
		ListView dListViewL = (ListView) view.findViewById(R.id.listViewTeamL);
		dListViewL.setAdapter(dAdapterL);
		ArrayAdapter<BikePoloPlayer> dAdapterR = new ArrayAdapter<BikePoloPlayer>(view.getContext(),
				R.layout.list_new_game_item,  teamListR);
		ListView dListViewR = (ListView) view.findViewById(R.id.listViewTeamR);
		dListViewR.setAdapter(dAdapterR);        

		TextView timerMinutes = (TextView) view.findViewById(R.id.timerMinutes);
		TextView timerSeconds = (TextView) view.findViewById(R.id.timerSeconds);
		
		if (app.isTimerGoing()) { // Timer already ongoing
			app.setTimerOutput(timerMinutes, timerSeconds);
		} else { // start new timer
			int defaultTime = ShuffleMain.getSettings(ShuffleMain.DEFAULT_GAME_TIME);
	        timerMinutes.setText(Integer.toString(defaultTime));
	        app.setTimer(new GameTimer(defaultTime, timerMinutes, timerSeconds));
	        app.startTimer();			
		}
		return view;
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	
	
	
}
