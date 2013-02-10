package org.kkdt.bike.polo.shuffle;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.TextView;

public class BikePoloShuffleApp extends Application 
	implements GameTimer.OnTimer {
	private static final int GAME_ONGOING = 1;
	private static final int GAME_END = 2;	
	public static final String SHOW_LAST_GAME = "SHOW_LAST_GAME";
	public static final int NO = 0;
	public static final int YES = 1;
	public static final int USE_TIMER = 0;
	public static final int DEFAULT_GAME_TIME = 1;
	public static final int DISPLAY_NOTIFICATIONS = 2;
	public static final int TEAM_PLAY = 3;
	
	private List<BikePoloPlayer> latestGameL = new ArrayList<BikePoloPlayer>();
	private List<BikePoloPlayer> latestGameR = new ArrayList<BikePoloPlayer>();
	private List<BikePoloPlayer> nextGameL = new ArrayList<BikePoloPlayer>();
	private List<BikePoloPlayer> nextGameR = new ArrayList<BikePoloPlayer>();
	private GameTimer timer;
	private TextView timerOutput;
	private int activeNotifications = 0;
	
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
	
	// Next game common handling
	public List<BikePoloPlayer> getNextGame(boolean left) {
		if (left) {
			return nextGameL;
		} else {
			return nextGameR;
		}
	}
	
	public void setNextGame(List<BikePoloPlayer> teamL, List<BikePoloPlayer> teamR) {
		nextGameL = teamL;
		nextGameR = teamR;
	}
	
	public void clearNextGame() {
		nextGameL.clear();
		nextGameR.clear();
	}
	
	// Timer common handling
	public void setTimer(int minutesInFuture, TextView outputToUpdate) {
		timer = new GameTimer(minutesInFuture, this);
		timerOutput = outputToUpdate;
	}
	
	public void startTimer() {
		timer.startTimer();
	}
	
	public void stopTimer() {
		timer.cancelTimer();
		cancelNotifications();
	}
	
	public void setTimerOutput(TextView out) {
		timerOutput = out;
		if (timer.isOngoing()) { // timer ongoing, set current time
			timerOutput.setText(timer.getTimeLeft());			
		}
		
	}
	
	public boolean isTimerOngoing() {
		if (timer != null) {
			return timer.isOngoing();
		} else {
			return false;
		}
	}
	
	// notification handling	
    private void notifyUser(int type, String timeLeft) {
    	if (this.getSettings(DISPLAY_NOTIFICATIONS) == YES) { // notifications allowed
    		activeNotifications |= type; // add to active notifications indicator
    		String contentTitle = "";
    		String contentText = "";
    		int defaults = 0;
    		int mId = R.id.timerLeft + type;
    		boolean autocancel = true;
    		boolean ongoing = false;
    		switch (type) {
    		case GAME_ONGOING:
    			contentTitle = getString(R.string.notifGameOngoing);
    			contentText = getString(R.string.timerLeft) + " " + timeLeft;
    			ongoing = true;
    			autocancel = false;
    			break;
    		case GAME_END:
    			contentTitle = getString(R.string.notifGameEnd);
    			defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS 
    					| Notification.DEFAULT_SOUND;
    			break;
    		}
    		NotificationCompat.Builder mBuilder =
    				new NotificationCompat.Builder(this)
    		.setAutoCancel(autocancel)
    		.setOngoing(ongoing)
    		.setOnlyAlertOnce(true)    			
    		.setPriority(Notification.PRIORITY_LOW)
    		.setSmallIcon(R.drawable.ic_bar_new_game)
    		.setDefaults(defaults)
    		.setContentTitle(contentTitle)
    		.setContentText(contentText);    		
    		// Creates an explicit intent for an Activity in your app
    		Intent resultIntent = new Intent(this, ShuffleMain.class);
    		resultIntent.putExtra(SHOW_LAST_GAME, true);

    		// The stack builder object will contain an artificial back stack for the
    		// started Activity.
    		// This ensures that navigating backward from the Activity leads out of
    		// your application to the Home screen.
    		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    		// Adds the back stack for the Intent (but not the Intent itself)
    		stackBuilder.addParentStack(ShuffleMain.class);
    		// Adds the Intent that starts the Activity to the top of the stack
    		stackBuilder.addNextIntent(resultIntent);
    		PendingIntent resultPendingIntent =
    				stackBuilder.getPendingIntent(
    						0,
    						PendingIntent.FLAG_UPDATE_CURRENT
    						);
    		mBuilder.setContentIntent(resultPendingIntent);
    		NotificationManager mNotificationManager =
    				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    		// mId allows you to update the notification later on.
    		mNotificationManager.notify(mId, mBuilder.build());
    	}
    }

    private void cancelNotify(int type) {
    	activeNotifications -= type; // remove from active notifications indicator    	
    	if (Context.NOTIFICATION_SERVICE!=null) {
	        String ns = Context.NOTIFICATION_SERVICE;
	        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
	        nMgr.cancel(R.id.timerLeft + type);
	    }
    }
    
    public void cancelNotifications() {
    	if ((activeNotifications & GAME_ONGOING) > 0) {
    		cancelNotify(GAME_ONGOING);
    	}
    	if ((activeNotifications & GAME_END) > 0) {
    		cancelNotify(GAME_END);
    	}
    }
    
    // Implement GameTimer.OnTick interface
	public void onTimerTick(String timeLeft) {
		notifyUser(GAME_ONGOING,timeLeft);
		timerOutput.setText(timeLeft);
	}
	
	public void onTimerFinish() {
		cancelNotifications(); // cancel game ongoing notification		
		notifyUser(GAME_END,"");	// show game end notification
		timerOutput.setText(timer.getTimeLeft());		
	}

	// Settings handling
    public int getSettings(int whichSetting) {
    	int settingValue = 0;
    	SharedPreferences appPref = 
    			PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	switch (whichSetting) {
    	case USE_TIMER:
    		if (appPref.getBoolean(getString(R.string.prefTimerUseTitle), true)) {
    			settingValue = YES;
    		}    		
    		break;
    	case DEFAULT_GAME_TIME:    
    		settingValue = Integer.parseInt((appPref.getString(getString(R.string.prefTimerTitle), "10")));
    		break;
    	case DISPLAY_NOTIFICATIONS:
    		if (appPref.getBoolean(getString(R.string.prefNotifTitle), true)) {
    			settingValue = YES;
    		}
    		break;
    	case TEAM_PLAY:
    		if (appPref.getBoolean(getString(R.string.prefTeamTitle), true)) {
    			settingValue = YES;    		
    		}
    		break;
    	default:
    	}
    	return settingValue;
    }

}
