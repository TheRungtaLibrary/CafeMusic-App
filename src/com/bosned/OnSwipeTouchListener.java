package com.bosned;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

class OnSwipeTouchListener implements OnTouchListener {

    public final GestureDetector gestureDetector;
   Activity activity;

    public OnSwipeTouchListener (Activity ctx){
    	activity = ctx;
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } 
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;
                }
             catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {/*
		// TODO Auto-generated method stub
		Log.d("onTouch : ", "::::::::::");
		Log.d("clickCount : ", "::::::::::"+HomeScreen.clickCount);
		switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
        case MotionEvent.ACTION_DOWN:
            HomeScreen.startTime = System.currentTimeMillis();
            HomeScreen.clickCount++;
            break;
        case MotionEvent.ACTION_UP:
            long time = System.currentTimeMillis() - HomeScreen.startTime;
            HomeScreen.duration=  HomeScreen.duration + time;
            Log.d("clickCount : ", "::::::::::"+HomeScreen.clickCount);
            if(HomeScreen.clickCount == 2)
            {
                if(HomeScreen.duration<= HomeScreen.MAX_DURATION)
                {	
                	HomeScreen.countRepeat++;
                	Log.d("countRepeat : ", "::::::::::"+HomeScreen.countRepeat);
          			if((HomeScreen.countRepeat % 2) == 0){ 	
          				HomeScreen.isRepeat = false;
          				Log.d("isRepeat : ", "::::::::::"+HomeScreen.isRepeat);
          				Toast.makeText(activity, "Repeat OFF",Toast.LENGTH_SHORT).show();
	          		}
	          		else if((HomeScreen.countRepeat % 2) != 0){
	          			HomeScreen.isRepeat = true;
	          			Log.d("isRepeat : ", "::::::::::"+HomeScreen.isRepeat);
          				Toast.makeText(activity, "Repeat ON",Toast.LENGTH_SHORT).show();		          				
	          		}
                }  
                HomeScreen.clickCount = 0;
                HomeScreen.duration = 0;
                break;
            }

        }*/
		return true; 
	
	}
}
