package com.lockscreen;
import receiver.LockScreenReceiver;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ToggleButton;

import com.bosned.HomeScreen;
import com.bosned.MediaPlayerService;
import com.cafe.R;
import com.bosned.WidgetHandler;

public class LockScreenAppActivity extends Activity {

    /** Called when the activity is first created. */

 	  RelativeLayout LockscreenParent;
 	  public static TextView LocksreenArtist;
 	  public static TextView LockscreenMinutes2;
 	  TextView LockscreenColon;
 	  public static TextView LockscreenSeconds;
 	  public static TextView LockscreenSong;
 	  Typeface tf1;
 	  Typeface tf2;
 	  Activity currenLockScreentActivity;
 	  ImageView prevBtnLockscreen;
 	  ToggleButton playPauseBtnLockscreen;
 	  ImageView nextBtnLockscreen;
 	

    public void onCreate(Bundle savedInstanceState) {

    	   super.onCreate(savedInstanceState);
    	   getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	   setContentView(R.layout.customlockscreen);
    	   
    	   currenLockScreentActivity = this;
    	   
    	   tf1 = Typeface.createFromAsset(this.currenLockScreentActivity.getAssets(), "neuropol-x-free.regular.ttf");
    	   tf2 = Typeface.createFromAsset(this.currenLockScreentActivity.getAssets(), "MAXIMUMSECURITY.ttf");
    	   
    	   LockscreenParent = (RelativeLayout) findViewById(R.id.LocksreenParent);
    	   LocksreenArtist = (TextView) findViewById(R.id.LocksreenArtist);
    	   //LockscreenMinutes1 = (TextView) findViewById(R.id.LocksreenMinutes1);
    	   LockscreenMinutes2 = (TextView) findViewById(R.id.LocksreenMinutes2);
    	   LockscreenColon  = (TextView) findViewById(R.id.LocksreenColon);
    	   LockscreenSeconds  = (TextView) findViewById(R.id.LocksreenSeconds);
    	   LockscreenSong  = (TextView) findViewById(R.id.LocksreenSong);
    	   prevBtnLockscreen = (ImageView) findViewById(R.id.prevBtnLockscreen);
    	   playPauseBtnLockscreen = (ToggleButton) findViewById(R.id.playPauseBtnLockscreen);
    	   nextBtnLockscreen = (ImageView) findViewById(R.id.nextBtnLockscreen);
    	   
    	   LocksreenArtist.setTypeface(tf2);
    	  // LockscreenMinutes1.setTypeface(tf1);
    	   LockscreenMinutes2.setTypeface(tf1);
    	   LockscreenColon.setTypeface(tf2);
    	   LockscreenSeconds.setTypeface(tf2);
    	   LockscreenSong.setTypeface(tf1);
    	   
    	   LockscreenSong.setSelected(true);
    	   LocksreenArtist.setSelected(true);
    	   //LockscreenMinutes1.setSelected(true);
    	   LockscreenMinutes2.setSelected(true);
    	   LockscreenSeconds.setSelected(true);
    	   
    	   LocksreenArtist.setTextSize(35 * getResources().getDisplayMetrics().density);
    	   //LockscreenMinutes1.setTextSize(125 * getResources().getDisplayMetrics().density);
    	   LockscreenMinutes2.setTextSize(125 * getResources().getDisplayMetrics().density);
    	   LockscreenColon.setTextSize(50 * getResources().getDisplayMetrics().density);
    	   LockscreenSeconds.setTextSize(60 * getResources().getDisplayMetrics().density);
    	   LockscreenSong.setTextSize(60 * getResources().getDisplayMetrics().density);    	   
    	   
	   	   if(WidgetHandler.songNameView != null){
	   	      LockScreenAppActivity.LockscreenSong.setText(WidgetHandler.songNameView.getText());
	   	   }
		   	 	
	   	   if(WidgetHandler.songArtistView != null){
	   		LockScreenAppActivity.LocksreenArtist.setText(WidgetHandler.songArtistView.getText());
	   	   }
		   	 			   
		   if(!HomeScreen.mp.isPlaying()){
			   playPauseBtnLockscreen.setBackground(currenLockScreentActivity.getResources().getDrawable(R.drawable.alt_play_btn_selector));
		   }

		 		
			 	
    	   if(getIntent()!=null&&getIntent().hasExtra("kill")&&getIntent().getExtras().getInt("kill")==1){
    	      // Toast.makeText(this, "" + "kill activityy", Toast.LENGTH_SHORT).show();
    	        	finish();
    	    	}

        try{
     // initialize receiver

       // startService(new Intent(this,MyService.class));

  /*      KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        k1 = km.newKeyguardLock("IN");
        k1.disableKeyguard();*/
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        
        LockscreenParent.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				HomeScreen.isAlreadyLocked = false;
				
				v.setVisibility(View.GONE);

				//if(MediaPlayerService.isMyServiceRunning(HomeScreen.class)){
					finish();	
					
					stopService(new Intent(currenLockScreentActivity, MyService.class));
				
                
				
				return true;
			}
		});
        
        prevBtnLockscreen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new MediaPlayerService().prevPlay();
				LockScreenAppActivity.LockscreenSong.setText(WidgetHandler.songNameView.getText());	 		
		 		LockScreenAppActivity.LocksreenArtist.setText(WidgetHandler.songArtistView.getText());
			}
		});
        
		nextBtnLockscreen.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new MediaPlayerService().nextPlay();
						LockScreenAppActivity.LockscreenSong.setText(WidgetHandler.songNameView.getText());	 		
				 		LockScreenAppActivity.LocksreenArtist.setText(WidgetHandler.songArtistView.getText());
						
					}
		});
		
		playPauseBtnLockscreen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("playPauseBtnLockscreen.setOnClickListener","...............................................");
				new MediaPlayerService().pausePlay();
			}
		});

        }catch (Exception e) {
			// TODO: handle exception
		}

    }
    class StateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    System.out.println("call Activity off hook");
                	finish();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }

    //only used in lockdown mode
    @Override
    protected void onPause() {
        super.onPause();

        // Don't hang around.
       // finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Don't hang around.
       // finish();
    }





    @Override
  public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

    	if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode == KeyEvent.KEYCODE_POWER)||(keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)) {
    	    //this is where I can do my stuff
    	    return true; //because I handled the event
    	}
       if((keyCode == KeyEvent.KEYCODE_HOME)){

    	   return true;
        }

	return false;

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
    	if (event.getKeyCode() == KeyEvent.KEYCODE_POWER ||(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)||(event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
    	    //Intent i = new Intent(this, NewActivity.class);
    	    //startActivity(i);
    	    return false;
    	}
    	 if((event.getKeyCode() == KeyEvent.KEYCODE_HOME)){

      	   return true;
         }
    return false;
    }

	/*public void unloack(){

          finish();

	}*/
    public void onDestroy(){
       // k1.reenableKeyguard();

        super.onDestroy();
    }

}