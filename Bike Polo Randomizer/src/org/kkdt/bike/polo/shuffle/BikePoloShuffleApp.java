package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.widget.TextView;

public class BikePoloShuffleApp extends Application {
	private List<BikePoloPlayer> latestGameL = new ArrayList<BikePoloPlayer>();
	private List<BikePoloPlayer> latestGameR = new ArrayList<BikePoloPlayer>();
	private GameTimer timer;
	
	// Latest game common handling
	public List<BikePoloPlayer> getLatestGame(boolean left) {
		if (left) {
			return latestGameL;
		} else {
			return latestGameR;
		}
	}
	
	public void setLatestGame(List<BikePoloPlayer> teamL, List<BikePoloPlayer> teamR) {
		latestGameL = teamL;
		latestGameR = teamR;
	}
	
	public void clearLatestGame() {
		latestGameL.clear();
		latestGameR.clear();
	}
	
	// Timer common handling
	public void setTimer(GameTimer newTimer) {
		timer = newTimer;
	}
	
	public void startTimer() {
		timer.start();
	}
	
	public void stopTimer() {
		timer.cancel();
		timer = null;
	}
	
	public void setTimerOutput(TextView min, TextView sec) {
		timer.setOutput(min, sec);
	}
	
	public boolean isTimerGoing() {
		if (timer != null) {
			return true;
		} else {
			return false;
		}
	}
}
