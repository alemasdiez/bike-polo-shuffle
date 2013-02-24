package org.kkdt.bike.polo.shuffle;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class MySwipeListener implements OnTouchListener {
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 60;
	private static final int SWIPE_THRESHOLD_VELOCITY = 20;	
	private final GestureDetector gdt;
	private View v;

	MySwipeListener(View listenerView) {
		v = listenerView;
		gdt = new GestureDetector(v.getContext(), new GestureListener());
	}
	
	public boolean onTouch(View v, final MotionEvent event) {
		gdt.onTouchEvent(event);
		return true;
	}

	private final class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) { // needed to process onClick or onSelect
            return true;
        }		
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			float dX = e2.getX()-e1.getX();
			float dY = e1.getY()-e2.getY();

			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH &&
					Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&
					Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {

				if (dX>0) {
					onRightSwipe(v);
				} else {
					onLeftSwipe(v);
				}

				return true;

			} 
			if (dX>0) {
				onRightSwipeAttempt(v);
			} else {
				onLeftSwipeAttempt(v);
			}
			return false;

		}
	}
	
	public abstract void onLeftSwipe(View v);

	public abstract void onRightSwipe(View v);
	
	public void onRightSwipeAttempt(View v) {};		
	
	public void onLeftSwipeAttempt(View v) {};			
}


