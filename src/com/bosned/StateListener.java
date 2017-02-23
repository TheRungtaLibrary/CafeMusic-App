package com.bosned;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StateListener extends PhoneStateListener{
	
	public boolean isRinging = false;
	
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {   	

        super.onCallStateChanged(state, incomingNumber);
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
            	isRinging = true;
            	HomeScreen.mp.pause();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                System.out.println("call Activity off hook");
                isRinging = true;
                HomeScreen.mp.pause();
            	//getApplication().startActivity(myIntent);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
            	Log.d("TelephonyManager.CALL_STATE_IDLE: mp.start() "," ......................................... "+isRinging);
            	if(isRinging){
            		HomeScreen.mp.start();
            	}
            	
                break;
        }
    }
};
