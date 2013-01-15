package org.kkdt.bike.polo.shuffle;

import android.os.CountDownTimer;
import android.text.format.Time;

public class GameTimer extends CountDownTimer {
	private static long resolution = 1000;
	private long timeLeft;
	private int state;	
	private static final int DEFINED = 0;
	private static final int STARTED = 1;
	private static final int FINISHED = 2;
	
	OnTimer tickCallBack;
	
    // User must implement this interface
    public interface OnTimer {
        public void onTimerTick(String timeLeft);
        public void onTimerFinish();
    }
    	
	public GameTimer(int minutesInFuture, Object timerListener) {
		super((long)minutesInFuture * 60 * resolution, resolution);
		try {
			tickCallBack = (OnTimer)timerListener;
        } catch (ClassCastException e) {
            throw new ClassCastException(timerListener.toString() + " must implement OnTimer interface");
        }
		state = DEFINED;
	}
			
	@Override
	public void onFinish() {
		tickCallBack.onTimerFinish();
		state = FINISHED;
	}
	
	public void cancelTimer() {
		super.cancel();
		state = FINISHED;
	}

	public void startTimer() {
		super.start();
		state = STARTED;
	}
	
	public boolean isOngoing() {
		switch (state) {
		case DEFINED:
			return false;
		case STARTED:
			return true;			
		default:
			return false;
		}
		
	}
	
	@Override
	public void onTick(long timeLeftMs) {
		timeLeft = timeLeftMs; // store remaining time
		Time timeSplitter = new Time();
		timeSplitter.set(timeLeftMs);	
		String outputTime = timeSplitter.format("%M:%S");
		tickCallBack.onTimerTick(outputTime);
	}

	public String getTimeLeft() {
		Time timeSplitter = new Time();
		timeSplitter.set(timeLeft);		
		return timeSplitter.format("%M:%S");
	}
	
}
