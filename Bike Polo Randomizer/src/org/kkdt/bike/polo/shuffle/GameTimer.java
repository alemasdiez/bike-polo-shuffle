package org.kkdt.bike.polo.shuffle;

import android.os.CountDownTimer;
import android.text.format.Time;
import android.widget.TextView;

public class GameTimer extends CountDownTimer {
	private static long resolution = 1000;
	private TextView min,sec;
	
	public GameTimer(int minutesInFuture, TextView minToUpdate, TextView secToUpdate) {
		super((long)minutesInFuture * 60 * resolution, resolution);
		min = minToUpdate;
		sec = secToUpdate;
	}
	
	public GameTimer(int minutesInFuture) {
		super((long)minutesInFuture * 60 * resolution, resolution);
	}
	
	public void setOutput(TextView minToUpdate, TextView secToUpdate) {
		min = minToUpdate;
		sec = secToUpdate;		
	}
	
	@Override
	public void onFinish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTick(long timeLeftMs) {
		Time timeSplitter = new Time();
		timeSplitter.set(timeLeftMs);
		min.setText(Integer.toString(timeSplitter.minute));
		sec.setText(Integer.toString(timeSplitter.second));
	}

}
