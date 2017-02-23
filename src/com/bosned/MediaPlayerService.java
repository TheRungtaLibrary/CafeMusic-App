package com.bosned;

import java.io.File;

import com.cafe.R.drawable;
import com.cafe.R;
import com.lockscreen.MyService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MediaPlayerService extends Service {
	
    public static final String ACTION_STOP="ACTION_STOP";
    public static final String ACTION_PLAY_PAUSE="ACTION_PLAY_PAUSE";
    public static final String ACTION_PREV="ACTION_PREV";
    public static final String ACTION_SHUFFLE="ACTION_SHUFFLE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_REPEAT = "ACTION_REPEAT";
    public static final String ACTION_SHARE = "ACTION_SHARE";
    public static final String ACTION_OPEN = "ACTION_OPEN";
    public static final String ACTION_CLOSE = "ACTION_CLOSE";
    public static final String ACTION_START_NOTIFY = "ACTION_START_NOTIFY";
    
    static Notification.Builder notifyBuilder = new Notification.Builder(HomeScreen.currentActivityContext);
    public static int counterRepeat = 0;
    WidgetHandler widgetHandler = null;
    Intent mNotificationIntent;
    Intent notificationIntent;
    
    
public MediaPlayerService() {
	// TODO Auto-generated constructor stub
    if(HomeScreen.mp != null && HomeScreen.currentActivityContext != null && HomeScreen.songAdapter != null){
    	widgetHandler = new WidgetHandler(HomeScreen.mp,HomeScreen.currentActivityContext, HomeScreen.songAdapter.getCursor());
    }

}
    
@Override
public int onStartCommand(Intent intent, int flags, int startId) {
	
    if (intent != null) {
    	
    	String action= (String) intent.getExtras().get("DO");
		Log.d("Inside MediaPlayerService :::::::::::::","action : "+action);
		
		if(action.equals(ACTION_START_NOTIFY)){
			startNotify();
		}
		if(action.equals(ACTION_OPEN)){
			Intent openAppIntent = new Intent(this, HomeScreen.class);
			startActivity(openAppIntent);
		}
		if(action.equals(ACTION_PREV)){
			prevPlay();
		}
		else if(action.equals(ACTION_NEXT)){
			nextPlay();
		}
		else if(action.equals(ACTION_PLAY_PAUSE)){
			pausePlay();
		}
		else if(action.equals(ACTION_REPEAT)){
			repeatPlay();
		}
		else if(action.equals(ACTION_SHARE)){
			sharePlay();
		}
		else if(action.equals(ACTION_CLOSE)){
			closePlay();
		}
		
    }
    return START_NOT_STICKY;
    }
	

private void startNotify() {
	// TODO Auto-generated method stub
	  notifyBuilder.setContent(HomeScreen.customNotify);
	  notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
	  notifyBuilder.setContentTitle("Cafe Music");
	  notifyBuilder.setOngoing(false);	    	  
	  //notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	  
	  notificationIntent = new Intent(HomeScreen.currentActivityContext, HomeScreen.class);
	  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	  PendingIntent pendingIntent = PendingIntent.getActivity(HomeScreen.currentActivityContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    
	  notifyBuilder.setContentIntent(pendingIntent);
	  
	  startForeground(911, notifyBuilder.build());
	  
	  //HomeScreen.currentActivityContext.stopService(new Intent(HomeScreen.currentActivityContext, MyService.class));
	  
	  //notifyMgr.notify(notifyID, notifyBuilder.build());
}

private void sharePlay() {
	// TODO Auto-generated method stub
	BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
	
	if(bt == null){
		Toast toast = Toast.makeText(getApplication().getApplicationContext(), "Your device doesn't have bluetooth connectivity", Toast.LENGTH_LONG);
		toast.show();
	}
	else{
		// bring up Android chooser	
		
		Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		sendBroadcast(it); 
		
		Uri songUri = Uri.parse(WidgetHandler.currentSongPath);
		File sourceFile = new File("/"+songUri);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("audio/");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sourceFile));
		startActivity(intent);
	}
}

public void pausePlay() {
	// TODO Auto-generated method stub
	Log.d("Inside pausePlay :::::::::::::","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    if(HomeScreen.mp.isPlaying())
    {
      if(HomeScreen.mp != null){
      Log.d("The song is playing !! ", "");
      HomeScreen.mp.pause();
      
      if(HomeScreen.customNotify != null){
    	  HomeScreen.customNotify.setImageViewResource(R.id.playBtn, R.drawable.play_logo_on);
      }
      
      if(HomeScreen.notifyMgr != null && HomeScreen.customNotify != null){
      	updateNotify(HomeScreen.notifyMgr, HomeScreen.customNotify);
      }
 
      }
    }
    else
    { 
    	Log.d("The songs is not playing !! ", "");
    	if(HomeScreen.mp != null){            		
    		HomeScreen.mp.start();
    		Log.d("The songs is playing ?? ", ""+HomeScreen.mp.isPlaying());
    		
    	      if(HomeScreen.customNotify != null){
    	    	  HomeScreen.customNotify.setImageViewResource(R.id.playBtn, R.drawable.play_logo);
    	      }
    	      
    	      if(HomeScreen.notifyMgr != null && HomeScreen.customNotify != null){
    	      	updateNotify(HomeScreen.notifyMgr, HomeScreen.customNotify);
    	      }
    	}
    
    }
}

public void prevPlay(){
	
	Log.d("prevBtnClickCounter .............................", "..............."+HomeScreen.prevBtnClickCounter);

	HomeScreen.prevBtnClickCounter = (1 + HomeScreen.prevBtnClickCounter);
	HomeScreen.mp.stop();
	int i = -1 + (widgetHandler.playedSongsIndex.size() - HomeScreen.prevBtnClickCounter);
	
	if ((i < 0) || (widgetHandler.playedSongsIndex.size() == 0))
	{
	  Log.d("Inside if when .............................", "playSongAtThisIndex < 0");
	  //HomeScreen.prevTrackIndex = HomeScreen.randomTrackIndex.nextInt(HomeScreen.songAdapter.getCursor().getCount());
	  //widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), HomeScreen.prevTrackIndex, true);
	  widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), 0, true);
	}
	else if(i > widgetHandler.playedSongsIndex.size()){	            	
		//HomeScreen.prevTrackIndex = HomeScreen.randomTrackIndex.nextInt(HomeScreen.songAdapter.getCursor().getCount());
		//widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), HomeScreen.prevTrackIndex, true);
		widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), 0, true);
	}
	else{
		HomeScreen.prevTrackIndex = ((Integer)widgetHandler.playedSongsIndex.get(i)).intValue();
		widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), HomeScreen.prevTrackIndex, false);
	}

	if(HomeScreen.customNotify != null){
    	HomeScreen.customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
    }
    
    if(HomeScreen.notifyMgr != null && HomeScreen.customNotify != null){
    	updateNotify(HomeScreen.notifyMgr, HomeScreen.customNotify);
    }
	
}

public void nextPlay(){

	HomeScreen.mp.stop();
    int i = (widgetHandler.playedSongsIndex.size() - HomeScreen.prevBtnClickCounter);

    if ((i < 0) || (widgetHandler.playedSongsIndex.size() == 0))
    {
      Log.d("Inside if when .............................", "playSongAtThisIndex < 0");
      //HomeScreen.prevTrackIndex = HomeScreen.randomTrackIndex.nextInt(HomeScreen.songAdapter.getCursor().getCount());
      
     // widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), HomeScreen.prevTrackIndex, true);
      
      widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), 0, true);
    }
    else if(i > (widgetHandler.playedSongsIndex.size() - 1)){	            	
    	//prevTrackIndex = randomTrackIndex.nextInt(cursor.getCount());
    	int prevTrackIndex = WidgetHandler.playedSongsIndex.get(WidgetHandler.playedSongsIndex.size() - 1);
    	if(prevTrackIndex >= HomeScreen.songAdapter.getCursor().getCount() - 1){
    		//prevTrackIndex = HomeScreen.randomTrackIndex.nextInt(HomeScreen.songAdapter.getCursor().getCount() - 1);
    		prevTrackIndex = 0; 	
	        }
    	else{
    		prevTrackIndex = prevTrackIndex + 1;
    	}
    	widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), prevTrackIndex, true);
    }
    else{
    	HomeScreen.prevTrackIndex = ((Integer)widgetHandler.playedSongsIndex.get(i)).intValue();
    	widgetHandler.playSong(HomeScreen.mp,HomeScreen.songAdapter.getCursor(), HomeScreen.prevTrackIndex, false);
    	HomeScreen.prevBtnClickCounter = HomeScreen.prevBtnClickCounter - 1;
    }


	
    	
        if(HomeScreen.customNotify != null){
        	HomeScreen.customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
        }
        
        if(HomeScreen.notifyMgr != null && HomeScreen.customNotify != null){
        	updateNotify(HomeScreen.notifyMgr, HomeScreen.customNotify);
        }
		
}

private void repeatPlay(){
	
	counterRepeat++;
	
	Log.d("counterRepeat is ......................... == ","....."+counterRepeat);
	
		if((counterRepeat % 2) == 0){
		
		int i = 1 + HomeScreen.songAdapter.getCursor().getPosition();
  	    
        Log.d("I is ......................... == ","....."+i);
        
        HomeScreen.customNotify.setImageViewResource(R.id.repeatBtn, R.drawable.repeat_logo);
		HomeScreen.customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
		
		if(HomeScreen.notifyMgr != null && HomeScreen.customNotify != null){
	    	updateNotify(HomeScreen.notifyMgr, HomeScreen.customNotify);
	    }
		
	
	}
	else if((counterRepeat % 2) != 0){
	
	HomeScreen.customNotify.setImageViewResource(R.id.repeatBtn, R.drawable.repeat_logo_on);	
	HomeScreen.customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
	if(HomeScreen.notifyMgr != null && HomeScreen.customNotify != null){
    	updateNotify(HomeScreen.notifyMgr, HomeScreen.customNotify);
    }
	
	}
}

public void closePlay() {
	// TODO Auto-generated method stub
	Log.d("closePlay called......................... == ",".....");
	
	stopSelf();
	
	/*if(HomeScreen.notifyMgr != null){
	  	//notifyMgr.cancel(notifyID);
		HomeScreen.notifyMgr.cancelAll();	
	  }*/
	
	if(HomeScreen.mp != null){
		HomeScreen.mp.pause();
		HomeScreen.viewDash();
	}
	
}

public void updateNotify(NotificationManager mgr, RemoteViews rView){
	  Log.d("NotificationManager ......................... == ","....."+mgr);
	  //startNotify();
	  int notifyID = 911;
	  notifyBuilder.setContent(rView);
	  notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
	  notifyBuilder.setContentTitle("cafe");
	  notifyBuilder.setOngoing(false);
	  mgr = (NotificationManager) HomeScreen.currentActivityContext.getSystemService(HomeScreen.currentActivityContext.NOTIFICATION_SERVICE);
	  
	  mNotificationIntent = new Intent(HomeScreen.currentActivityContext, HomeScreen.class);
	  mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	  PendingIntent pendingIntent = PendingIntent.getActivity(HomeScreen.currentActivityContext, 0, mNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	    
	  notifyBuilder.setContentIntent(pendingIntent);
	  mgr.notify(notifyID, notifyBuilder.build());
}


@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}

/*33@Override
public void onTaskRemoved(Intent rootIntent) {
	Log.d("onTaskRemoved called ......................... == ",".....////////////////////////////////////////////");
    System.out.println("onTaskRemoved called");
    stopSelf();
    super.onTaskRemoved(rootIntent);
}*/

/** Called when The service is no longer used and is being destroyed */
@Override
public void onDestroy() {
	Log.d("MediaPlayerService Destroyed ......................... == ","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	stopNotifyService();
	if(HomeScreen.notifyMgr != null){
	  	//notifyMgr.cancel(notifyID);
		HomeScreen.notifyMgr.cancelAll();
	}
	
}

public void stopNotifyService(){
	HomeScreen.currentActivityContext.stopService(new Intent(HomeScreen.currentActivityContext, MediaPlayerService.class));
}

public static boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) HomeScreen.currentActivityContext.getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.getName().equals(service.service.getClassName())) {
            return true;
        }
    }
    return false;
}

}