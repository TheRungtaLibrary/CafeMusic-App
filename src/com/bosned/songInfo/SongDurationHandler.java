package com.bosned.songInfo;

import com.bosned.WidgetHandler;
import com.lockscreen.LockScreenAppActivity;

import android.app.DialogFragment;
import android.os.Handler;
import android.util.Log;

public class SongDurationHandler extends DialogFragment{
	
	String songDurationText = "";
    
	static int currentDuration = 0;
	static int totalDurationInSecs = 0;
	int hours = 0;
    int minutes = 0;
    int seconds = 0;
	static Double percentage = (double) 0;
    static long currentSeconds = 0;
	static long totalSeconds = 0;
	
	public void calculateSongDuration(){
		
		//try {
			hours = WidgetHandler.mp.getCurrentPosition() / 3600000;
			minutes = WidgetHandler.mp.getCurrentPosition() % 3600000 / 60000;
			seconds = WidgetHandler.mp.getCurrentPosition() % 3600000 % 60000 / 1000;
			String currHrsMins = "";
			String currSecs = "";
			
			if (hours > 0 && hours <10)
			{	    	
				currHrsMins = ("0" + hours + ":");
			}
			if (hours >=10)
			{	    	
				currHrsMins = (currHrsMins + hours + ":");
			}
			if (minutes >= 0 && minutes < 10)
			{
				currHrsMins = (currHrsMins + "0" + minutes);
			}
			else {
				currHrsMins = (currHrsMins + minutes);
			}
			if(seconds >= 0 && seconds < 10){
				currSecs = ("0" + (Integer.toString(seconds)));
			}
			else{
				currSecs = (Integer.toString(seconds));
			}
			
			//songDurationText = ""+currHrsMins+":"+currSecs;
			WidgetHandler.songDurationView.setText(new String(""+currHrsMins+":"+currSecs));
			
			//return songDurationText;
			
			if(LockScreenAppActivity.LockscreenMinutes2 != null){		    			
				LockScreenAppActivity.LockscreenMinutes2.setText(new String(currHrsMins));
				
			}
			
			if(LockScreenAppActivity.LockscreenSeconds != null){
				LockScreenAppActivity.LockscreenSeconds.setText(new String(currSecs));
					
			}
			
			currHrsMins = null;
			currSecs = null;
			
			/*} finally{
			// TODO Auto-generated catch block
			System.gc();
		}*/
		
	}
	
	public static void calculateProgressPercentage(){	
		
		currentSeconds = (long) (((WidgetHandler.mp.getCurrentPosition() / 1000)));
 
		totalSeconds = (long) (((WidgetHandler.mp.getDuration() / 1000)));
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		WidgetHandler.barSongDuration.setProgress(percentage.intValue());
	}
	
	public static int progressToTimer(int progress, int totalDuration){
		totalDurationInSecs = (int)((totalDuration)/1000);
		currentDuration = (int)(((double)progress/100)*totalDurationInSecs);
		return currentDuration * 1000;
		
	}

}
