package com.bosned;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.ContactsContract.Directory;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
//import com.bosned.songInfo.SongDurationHandler;
import com.digitalaria.gama.wheel.Wheel;
import com.digitalaria.gama.wheel.WheelAdapter;
import com.digitalaria.gama.wheel.WheelAdapter.OnItemClickListener;
import com.digitalaria.gama.wheel.WheelAdapter.OnWheelRotationListener;
import com.lockscreen.LockScreenAppActivity;
import com.lockscreen.MyService;
import com.cafe.R;
import com.bosned.audioMod.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import receiver.LockScreenReceiver;

public class HomeScreen extends FragmentActivity
  implements LoaderManager.LoaderCallbacks<Cursor>, OnQueryTextListener
{
  private static String TAG = "CursorLoader";
  public static MediaPlayer mp = new MediaPlayer();
  static ListView songList;
  public static Activity currentActivityContext;
  CursorLoader cursorLoader; 
  ViewFlipper flipperAreaTwo;
  public static LoaderManager loaderManager;
  WidgetHandler widgetHandler;
  Typeface songNameFont;
  public static Notification.Builder notifyBuilder;
  public static NotificationManager notifyMgr;
  int notifyID = 911;
  AudioRecord audioRecord;
  public Wheel wheel;
  private Resources res; 
  public static int prevBtnClickCounter = 0;
  public static int prevTrackIndex;
  public static SimpleCursorAdapter songAdapter;
  SearchView musicSearch;
  SearchManager manager;
  String cursorFilter;
  String[] mSelectionArgs = {""};
  String selection = "";
  public static int firstItemClicked = 0;
  DrawerLayout drawer_layout;
  public static RemoteViews customNotify;
  ImageView imageviewDefault;
  TextView songDurationView;
  TextView songArtistView;
  PowerManager pm;	
  AudioFx audio;
  LinearLayout equalizerlayout1;
  public static boolean isAlreadyLocked = false;
  static LinearLayout dash1;
  static LinearLayout dash2;
  static LinearLayout dash3;
  static LinearLayout dash4;
  AlertDialog.Builder mBuilder;
  LayoutInflater inflater;
  AlertDialog alert;
  int positionLongClick = 0;
  public static int currentSongPosition = 0;
  
  private final String htmlText1 = "<div>Icons made by <a href='http://www.freepik.com' title='Freepik'>Freepik</a> from <a href='http://www.flaticon.com' title='Flaticon'>www.flaticon.com</a> is licensed by <a href='http://creativecommons.org/licenses/by/3.0/' title='Creative Commons BY 3.0'>CC BY 3.0</a></div>";
  
  private final String htmlText2 = "<div xmlns:cc='http://creativecommons.org/ns#' xmlns:dct='http://purl.org/dc/terms/' about='http://freemusicarchive.org/music/Christian_Bjoerklund/Skapmat/christian_bjoerklund_-_skpmat_ep_-_01_-_hallon'> <span property='dct:title'>Hallon</span> (<a rel='cc:attributionURL' property='cc:attributionName' href='http://freemusicarchive.org/music/Christian_Bjoerklund/'>Christian Bjoerklund</a>) /  <a rel='license' href='http://creativecommons.org/licenses/by-nc-sa/3.0/'>CC BY-NC-SA 3.0</a></div>";
  
  int resId = R.raw.hallon;
  String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/hallon.mp3";
  
  TextView tvFile_Title,tvFile_One,tvFile_Second,tvFile_Third,tvFile_Fourth,tvFile_Fifth
	,tvFile_Sixth,tvFile_Seventh,tvFile_Eighth,tvFile_Ninth,tvLicense_One,tvLicense_Second,tvLicense_Third
	,tvLicense_Fourth,tvLicense_Fifth,tvLicense_Sixth,tvLicense_Seventh,tvLicense_Eighth,tvLicense_Ninth;
	TextView[] arrayTV = {tvFile_Title,tvFile_One,tvFile_Second,tvFile_Third,tvFile_Fourth,tvFile_Fifth
			,tvFile_Sixth,tvFile_Seventh,tvFile_Eighth,tvFile_Ninth,tvLicense_One,tvLicense_Second,tvLicense_Third
			,tvLicense_Fourth,tvLicense_Fifth,tvLicense_Sixth,tvLicense_Seventh,tvLicense_Eighth,tvLicense_Ninth
		}; 
  
	  private int[] icons = {
		  		R.drawable.equalizer, R.drawable.recorder, 
		  		R.drawable.visualizer, R.drawable.share_logo, R.drawable.repeat_logo, R.drawable.edit, R.drawable.about};
	  
  		LinearLayout contentLayout;
		ImageView handleOpen;
		ImageView handleClose;
		private int numArtists;
		private RelativeLayout visualizerLayout1;
		public static Intent intentLockScreen = null;
		
		public static File customCover = null;
  
  private Drawable[] getDrawableFromData(int[] paramArrayOfInt)
  {
    Drawable[] arrayOfDrawable = new Drawable[paramArrayOfInt.length];
    for (int i = 0; ; i++)
    {
      if (i >= paramArrayOfInt.length)
        return arrayOfDrawable;
      arrayOfDrawable[i] = this.res.getDrawable(paramArrayOfInt[i]);
      
    }
  }


  @SuppressWarnings("deprecation")
public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.main);
    
    currentActivityContext = this;  
	
    visualizerLayout1 = (RelativeLayout) findViewById(R.id.VisualizerArea);
    equalizerlayout1 = (LinearLayout) findViewById(R.id.viewEqualizer);
    flipperAreaTwo = (ViewFlipper) findViewById(R.id.FlipAreaTwo);    
    //songNameTV = (TextView) v.findViewById(R.id.songNameTV);
    handleOpen = (ImageView) findViewById(R.id.handleOpen);
    handleClose = (ImageView) findViewById(R.id.handleClose);
    songNameFont = Typeface.createFromAsset(getAssets(), "neuropol-x-free.regular.ttf"); 
    musicSearch = (SearchView) findViewById(R.id.inputSearch);
    manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    imageviewDefault = (ImageView) findViewById(R.id.imageviewDefault);
    imageviewDefault.setBackgroundDrawable(getResources().getDrawable(R.drawable.trial));
    songDurationView = ((TextView) findViewById(2131230734));
    songDurationView.setVisibility(View.GONE);
    songArtistView = ((TextView) findViewById(R.id.ArtistNameView));
    
    // FACEBOOK, TWITTER AND LINKED SHARE CODE TO BE IN FIRST UPDATE
    //ShareButtonHolder = (LinearLayout) findViewById(R.id.ShareButtonHolder);
    
    dash1 = (LinearLayout) findViewById(R.id.dash1);
/*    dash2 = (LinearLayout) findViewById(R.id.dash2);
    dash3 = (LinearLayout) findViewById(R.id.dash3);
    dash4 = (LinearLayout) findViewById(R.id.dash4);*/
    
    songArtistView.setVisibility(View.GONE);
    
    musicSearch.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
    musicSearch.setIconifiedByDefault(true);
    musicSearch.setOnQueryTextListener(this);
    
    int id = musicSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
    TextView textView = (TextView) musicSearch.findViewById(id);
    textView.setTextColor(getResources().getColor(R.color.white));
    
    drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
    songList = (ListView) findViewById(R.id.left_drawer);
    contentLayout = (LinearLayout) findViewById(R.id.ContentAreaLayout);
    
    int width = getResources().getDisplayMetrics().widthPixels;
    DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) contentLayout.getLayoutParams();
    params.width = 9*(width)/10;
    contentLayout.setLayoutParams(params);
    
    final Animation animateFadeOut =  new AlphaAnimation(1.0f, 0f);
    animateFadeOut.setDuration(1000);
    animateFadeOut.setStartOffset(100);
    animateFadeOut.setFillAfter(true);
    
    final Animation animateFadeIn =  new AlphaAnimation(0f, 1.0f);
    animateFadeIn.setDuration(1000);
    animateFadeIn.setStartOffset(100);
    animateFadeIn.setFillAfter(true);
    
    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    handleOpen.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			drawer_layout.openDrawer(contentLayout);
		}
	});
    
    handleClose.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			drawer_layout.closeDrawer(contentLayout);			
		}
	});
    
    drawer_layout.setDrawerListener(new DrawerListener() {
		
		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDrawerOpened(View arg0) {
			// TODO Auto-generated method stub
			handleOpen.setVisibility(View.GONE);
		}
		
		@Override
		public void onDrawerClosed(View arg0) {
			// TODO Auto-generated method stub
			handleOpen.setVisibility(View.VISIBLE);
		}
	});
    
    songList.setFastScrollEnabled(true);
    songList.setDivider(getResources().getDrawable(R.drawable.divider_background));
    songList.setDividerHeight(4);
    songList.setBackgroundColor(getResources().getColor(android.R.color.transparent));	
    
    
    final String[] uiBindFrom = {  MediaStore.Audio.Media.TITLE,  
			 MediaStore.Audio.Media.ARTIST,
		  };		
	int[] uiBindTo = { R.id.songNameTV, R.id.songArtistTV };
	
	songAdapter = new SimpleCursorAdapter(this, R.layout.row, null, uiBindFrom, uiBindTo,0);
	
	songList.setAdapter(songAdapter);
	songList.setFastScrollEnabled(true);
	songList.setTextFilterEnabled(true);
	
	try {
		if(!(new File(sdcardPath).exists())){
			CopyRAWtoSDCard(resId, sdcardPath);
		}		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
    loaderManager = getSupportLoaderManager();
    loaderManager.initLoader(1, null, this);

    customCover = saveCustomCover();
    
    audio = new AudioFx(currentActivityContext, mp, equalizerlayout1, visualizerLayout1);
    
    audioRecord = new AudioRecord(currentActivityContext, customCover);    
    
    pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    
    init();  
	  
	listenToTelephony();  
	  
  	}
  
  protected void onStop(){
	  super.onStop();

	  //boolean isScreenOn = pm.isScreenOn();
	  startService(new Intent(this,MyService.class));
	  
	  /*if(!isScreenOn && isAlreadyLocked == false){	
		  Log.d("isAlreadyLocked .............. "," = "+isAlreadyLocked);
		  if(isAlreadyLocked == false){
		  	  if(intentLockScreen == null){
				  Log.d("To create LockScreen ............",".........................");
				  intentLockScreen = new Intent(this, LockScreenAppActivity.class);
				  intentLockScreen.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				  intentLockScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				  //intent.putExtra("countSongs", ""+songAdapter.getCursor().getCount());
				  //intent.putExtra("countArtists", ""+numArtists);			  
				  startActivity(intentLockScreen);
				  isAlreadyLocked = true;
		  }
	  }
	  else {*/
	  notifyBuilder = new Notification.Builder(this.currentActivityContext);	 
	  
	  customNotify = new RemoteViews(getPackageName(), R.layout.notify);
	  customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
	  
	  Intent startNotify = new Intent(this, MediaPlayerService.class);
	  startNotify.putExtra("DO", "ACTION_START_NOTIFY");
	  startService(startNotify);
	  
	  //PREV TRACK INTENT
	  
	  Intent intent = new Intent(this, MediaPlayerService.class);
	  intent.putExtra("DO", "ACTION_PREV");
	  
	  PendingIntent prevPendingIntent = PendingIntent.getService(getApplicationContext(),
	            0, intent,
	            PendingIntent.FLAG_UPDATE_CURRENT);	  	
	  
	  customNotify.setOnClickPendingIntent(R.id.LeftPanel, prevPendingIntent);

	  //NEXT TRACK INTENT ----------------------------
	  
	  Intent nextIntent = new Intent(this, MediaPlayerService.class);
	  nextIntent.putExtra("DO", "ACTION_NEXT");
	  
	  PendingIntent nextPendingIntent = PendingIntent.getService(getApplicationContext(),
	            3, nextIntent,
	            PendingIntent.FLAG_UPDATE_CURRENT);	
	  
	  customNotify.setOnClickPendingIntent(R.id.RightPanel, nextPendingIntent);
	  
	  //PLAY/PAUSE TRACK INTENT ----------------------
	  
	  Intent playIntent = new Intent(this, MediaPlayerService.class);
	  playIntent.putExtra("DO", "ACTION_PLAY_PAUSE");
	  
	  PendingIntent playPendingIntent = PendingIntent.getService(getApplicationContext(),
	            4, playIntent,
	            PendingIntent.FLAG_UPDATE_CURRENT);	
	  
	  customNotify.setOnClickPendingIntent(R.id.playBtn, playPendingIntent);
	  
	  //SHARE TRACK INTENT ---------------------------
	  
	  Intent shareIntent = new Intent(this, MediaPlayerService.class);
	  shareIntent.putExtra("DO", "ACTION_SHARE");
	  
	  PendingIntent sharePendingIntent = PendingIntent.getService(getApplicationContext(),
	            5, shareIntent,
	            PendingIntent.FLAG_UPDATE_CURRENT);
	  
	  customNotify.setOnClickPendingIntent(R.id.shareBtn, sharePendingIntent);
	  
	  //REPEAT TRACK INTENT ---------------------------
	  
	  Intent repeatIntent = new Intent(this, MediaPlayerService.class);
	  repeatIntent.putExtra("DO", "ACTION_REPEAT"); 
	  
	  PendingIntent repeatPendingIntent = PendingIntent.getService(getApplicationContext(),
	            6, repeatIntent,
	            PendingIntent.FLAG_UPDATE_CURRENT);
	  
	  customNotify.setOnClickPendingIntent(R.id.repeatBtn, repeatPendingIntent);
	  
	  //CLOSE INTENT ---------------------------
	  
	  Intent closeIntent = new Intent(this, MediaPlayerService.class);
	  closeIntent.putExtra("DO", "ACTION_CLOSE"); 
	  
	  PendingIntent closePendingIntent = PendingIntent.getService(getApplicationContext(),
	            7, closeIntent,
	            PendingIntent.FLAG_UPDATE_CURRENT);
	  
	  customNotify.setOnClickPendingIntent(R.id.closeBtn, closePendingIntent);
	  
	  
	  // ALL INTENT CALLS
	  notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	  
 // }
	  
  }
  
  protected void onResume(){
	  super.onResume();
	  Log.d("Resumed  .............. "," ============= !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	  new MediaPlayerService().stopNotifyService();
	  if(notifyMgr != null){
		  	//notifyMgr.cancel(notifyID);
		  	notifyMgr.cancelAll();
		  }
	  
  }
  
  protected void onRestart(){
	  super.onRestart();
	  isAlreadyLocked = false;
	  new MediaPlayerService().stopNotifyService();
	  if(notifyMgr != null){
	  	//notifyMgr.cancel(notifyID);
	  	notifyMgr.cancelAll();
	  }
  }
  
  protected void onDestroy(){
	  super.onDestroy();
	  Log.d("onDestroy called !!!!!!!!!!!!!!","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	  this.currentActivityContext.stopService(new Intent(this.currentActivityContext, MediaPlayerService.class));
	  
	  this.currentActivityContext.stopService(new Intent(this.currentActivityContext, MyService.class));
	  //new MediaPlayerService().stopNotifyService();
		if(notifyMgr != null){
		  	//notifyMgr.cancel(notifyID);
			notifyMgr.cancelAll();	
		  } 
		
		intentLockScreen = null;
  }
  
  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
  
  
	private void init() {
		
/*		SongListArea = (LinearLayout) findViewById(R.id.ContentAreaLayout);
		SongListArea.addView(songList);*/
		
		songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				
				Log.d("No of artist :::::::::::","::::::::::::::::::"+numArtists);
				
				
				songList.setItemChecked(position, true);				
				
				firstItemClicked++;
				
				if(firstItemClicked != 0){
					songDurationView.setVisibility(View.VISIBLE);
					songArtistView.setVisibility(View.VISIBLE);
					setDefaultChild();
				}
				
				drawer_layout.closeDrawer(contentLayout);
				
				if(flipperAreaTwo.getDisplayedChild() != 2){
					
					// TODO Auto-generated method stub				
					
					widgetHandler.playSong(mp,songAdapter.getCursor(), position, true);
				}
				else{
					Toast toast = Toast.makeText(getApplication().getApplicationContext(), "Sorry You are recording !! ", 200);
					toast.show();
				}
			}
		});
		
		songList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int delPosition,
					long arg3) {
				// TODO Auto-generated method stub
				
				try{
					
					positionLongClick = delPosition;				
					
					songAdapter.getCursor().moveToPosition(positionLongClick);
					
					Log.d("positionLongClick : ",":::::::::::::::::"+positionLongClick);
					Log.d("HomeScreen.currentSongPosition : ",":::::::::::::::::"+HomeScreen.currentSongPosition);
					
					drawer_layout.closeDrawer(contentLayout);
					
					mBuilder = new AlertDialog.Builder(currentActivityContext); 
					
					TextView tv = new TextView(currentActivityContext);
					tv.setText("Confirm Delete");
					tv.setTypeface(songNameFont);
					tv.setTextColor(currentActivityContext.getResources().getColor(R.color.Orange));
					tv.setTextSize(currentActivityContext.getResources().getDimension(R.dimen.TextSize_ArtistNameView));
					tv.setGravity(Gravity.CENTER);
					
					mBuilder.setView((View)tv)
							.setCancelable(true)
		            .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
		             @Override
		             public void onClick(DialogInterface dialog, int which) {
		                 dialog.cancel();
		                 
		              }
		            })
		            .setPositiveButton("Yup", new DialogInterface.OnClickListener() {
		             @Override
		             public void onClick(DialogInterface dialog, int which) {
		            	 try{
		            		 //songAdapter.getCursor().moveToPosition(positionLongClick);
		            		 
		            	 	String selectedFilePath = songAdapter.getCursor().getString(songAdapter.getCursor().getColumnIndex("_data"));
		            	 	
		            	 	Log.d("selectedFilePath : ",":::::::::::::::::"+new File(selectedFilePath).getAbsolutePath());
		            	 	Log.d("id : ",":::::::::::::::::"+songAdapter.getCursor().getString(songAdapter.getCursor().getColumnIndex("_id")));
		            	 	//songAdapter.getCursor().getColumnIndex("_id");
		            	 	
		            	 	File file = new File(selectedFilePath);
							boolean deleted = file.getCanonicalFile().delete();					
							
							if(deleted){																
								
								String id = songAdapter.getCursor().getString(songAdapter.getCursor().getColumnIndex("_id"));
								Log.d("id  = ", "................. "+id);
								Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(id));
				                getContentResolver().delete(uri, null, null);

							   songAdapter.notifyDataSetChanged();
							   Toast toast = Toast.makeText(getApplication().getApplicationContext(), "The song has been deleted !! ", 200);
							   toast.show();
							   
							   if(positionLongClick == HomeScreen.currentSongPosition){
									new MediaPlayerService().nextPlay();
								}
							}
		            	 }
		            	 catch(Exception e){
		            		 e.printStackTrace();
		            	 }
		             }
		         });
					
			     alert = mBuilder.create();			     
			     alert.show();
			     
		     	Button btn2 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		        Button btn1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
		        btn1.setTypeface(songNameFont);
		        btn2.setTypeface(songNameFont);
			     
				}
				catch(UnsupportedOperationException e){
					e.printStackTrace();
				}
				
				return false;
			}
			
		});
		
		res = getApplicationContext().getResources();
		wheel = (Wheel) findViewById(R.id.wheel);
		wheel.setItems(getDrawableFromData(icons));
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d("Density is ::::::::::::::::::::::::::", "::::::::::"+metrics.densityDpi);
		if(metrics.densityDpi == 240){
			wheel.setWheelDiameter(145);
		}
		else{
			wheel.setWheelDiameter(200);
		}
		
		wheel.setWheelBackground(0, R.layout.circular_menu);
		wheel.configureWheelBackground(true);
		wheel.setRotatedItem(false);
		
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.d("onCreateLoader .............. "," = ");
		
		new MediaScannerWrapper(currentActivityContext, sdcardPath, "audio/*").scan();
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(Uri.fromFile(new File(sdcardPath)));
		currentActivityContext.sendBroadcast(intent);
		
		//Contacts.CONTENT_FILTER_URI
		if (cursorFilter != null) {
			
			selection = MediaStore.Audio.Media.DISPLAY_NAME + " like '%" + cursorFilter
	                + "%' OR "+ MediaStore.Audio.Media.ARTIST + " like '%" + cursorFilter
	                + "%' OR " + MediaStore.Audio.Media.ALBUM + " like '%" + cursorFilter
	                + "%'";						
			
			
			
			mSelectionArgs[0] = cursorFilter;
			
			Log.d("mSelectionArgs set to Search String .............. "," ============= "+cursorFilter);
			
			cursorLoader = new CursorLoader(
			          this, 
			          MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			                null, 
			                selection, 
			                null,
			                "upper("+MediaStore.Audio.Media.TITLE + ") ASC");

        } else {
            
        	selection = MediaStore.Audio.Media.DISPLAY_NAME + " like '%" + ".mp3"
	                + "%' OR "+ MediaStore.Audio.Media.DISPLAY_NAME + " like '%" + ".mp4"
	                + "%' OR " + MediaStore.Audio.Media.DISPLAY_NAME + " like '%" + ".wav"
	                + "%' OR " + MediaStore.Audio.Media.DISPLAY_NAME + " like '%" + ".m4a"
	                + "%'";

        	cursorLoader = new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, "upper("+MediaStore.Audio.Media.TITLE + ") ASC");
        	
        }		
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0, final Cursor cursor) {
		// TODO Auto-generated method stub
		
		Log.d("onLoadFinished .............. "," = ");
		
		if(cursorLoader != null && songAdapter != null){
			songAdapter.swapCursor(cursor);
			Log.d("No of songs :::::::::::","::::::::::::::::::"+cursor.getCount());
		}
		else{
			Log.v(TAG,"OnLoadFinished: mAdapter is null");
			
		}			
		
		
		
		if(firstItemClicked == 0){
			if(widgetHandler == null){
				widgetHandler = new WidgetHandler(mp,currentActivityContext, songAdapter.getCursor());
			}
			
			widgetHandler.playSong(mp,cursor, 0, true);
			viewDash();
		}
		
		songDurationView.setVisibility(View.VISIBLE);
		songArtistView.setVisibility(View.VISIBLE);
		setDefaultChild();
		
				
		
		songList.setOnScrollListener(new OnScrollListener() {

	        @Override
	        public void onScroll(AbsListView view, int firstVisibleItem,
	                int visibleItemCount, int totalItemCount) {

	            }

	        int mPosition=0;
	        int mOffset=0;

	        @Override
	        public void onScrollStateChanged(AbsListView view, int scrollState) {
	            // TODO Auto-generated method stub
	             int position = songList.getFirstVisiblePosition();
	               View v = songList.getChildAt(0);
	                int offset = (v == null) ? 0 : v.getTop();                
	        }
	    });
      
		wheel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(WheelAdapter<?> parent, View view,
					int position, long id) {	
				
				// TODO Auto-generated method stub
				Log.d("The clicked Item ID is: .............", "............"+id);
				Log.d("The Parent of SongList: .............", "............"+songList.getParent());
				int currentChild = flipperAreaTwo.getDisplayedChild();
				
				switch (position) {
				case 0:					
					if(currentChild != 2){
						//Toast.makeText(currentActivityContext, "EQUALIZER ON",100).show();
						flipperAreaTwo.setDisplayedChild(2);												
					}
					else{
						//Toast.makeText(currentActivityContext, "EQUALIZER OFF",100).show();
						setDefaultChild();
					}
					
					WidgetHandler.songNameView.setVisibility(View.VISIBLE);
					songArtistView.setVisibility(View.VISIBLE);
					//ShareButtonHolder.setVisibility(View.GONE);
					
					return;
				case 1:					
					if(currentChild != 3){
						Toast.makeText(currentActivityContext, "RECORD ON",100).show();
						flipperAreaTwo.setDisplayedChild(3);
						flipperAreaTwo.setClickable(false);
						if(mp.isPlaying())
			            {
			              if(mp != null){
			              Log.d("The song is playing !! ", "");
			              mp.pause();
			     //         Log.d("The songs is playing ?? ", mp.isPlaying());
			              }
			            }
						
						WidgetHandler.songNameView.setVisibility(View.GONE);
						songArtistView.setVisibility(View.GONE);
						//ShareButtonHolder.setVisibility(View.VISIBLE);
					}
					else{
						Toast.makeText(currentActivityContext, "RECORD OFF",100).show();
						setDefaultChild();
						if(!mp.isPlaying())
			            {
			              if(mp != null){
			            	Log.d("The song is playing !! ", "");
			              	mp.start();	              
			              }
			            }
						
						WidgetHandler.songNameView.setVisibility(View.VISIBLE);
						songArtistView.setVisibility(View.VISIBLE);
						//ShareButtonHolder.setVisibility(View.GONE);
						
					}
					
					return;
					
				case 2:
					
					if(flipperAreaTwo.getDisplayedChild() != 4){
						//Toast.makeText(currentActivityContext, "VISUALIZER ON",100).show();
						flipperAreaTwo.setDisplayedChild(4);											
						
					}
					else{
						//Toast.makeText(currentActivityContext, "VISUALIZER OFF",100).show();
						setDefaultChild();
						
					}
					
					WidgetHandler.songNameView.setVisibility(View.VISIBLE);
					songArtistView.setVisibility(View.VISIBLE);
					//ShareButtonHolder.setVisibility(View.GONE);
		            
		            return;
			   case 3:
				    //Toast.makeText(currentActivityContext, "FILE SHARE",100).show();
					BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();					
					if(bt == null){
						Toast toast = Toast.makeText(getApplication().getApplicationContext(), "Your device doesn't have bluetooth connectivity", Toast.LENGTH_SHORT);
						toast.show();
					}
					else{
						// bring up Android chooser								
						
						Uri songUri = Uri.parse(WidgetHandler.currentSongPath);
						File sourceFile = new File("/"+songUri);
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_SEND);
						intent.setType("audio/");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sourceFile));
						startActivity(intent);
						
					}
					return;
			   case 4:
				   MediaPlayerService.counterRepeat++;	
				   if((MediaPlayerService.counterRepeat % 2) == 0){
						Toast.makeText(currentActivityContext, "Repeat Off",Toast.LENGTH_SHORT).show();	
					}
					else if((MediaPlayerService.counterRepeat % 2) != 0){
						Toast.makeText(currentActivityContext, "Repeat On",Toast.LENGTH_SHORT).show();	
					}
				   return;
			   case 5:	
				   WidgetHandler.editAudioTags.setEditDialog();
				   /*if(mp.isPlaying()){
					   mp.pause();
				   }*/
				   return;
			   case 6:	
				   initiatePopupWindow(currentActivityContext);
				   return;
				}
				
			}
			
		});		
		
		wheel.setOnWheelRotationListener(new OnWheelRotationListener() {

			@Override
			public void onWheelRotationStart() {
				// TODO Auto-generated method stub
				
				if(wheel.getSelectedItem() != -1){
					
					wheel.setSelectionAngle(0);
				}
			}

			@Override
			public void onWheelRotationEnd() {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		  mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

		  		@Override
		  		public void onCompletion(MediaPlayer mp) {
		  			
		  			try {
						Log.d("Song completed .............. "," ============= "+MediaPlayerService.counterRepeat);
						
						if((MediaPlayerService.counterRepeat % 2) == 0){
							Log.d("Is Repeat OFF ......................... == ","....."+WidgetHandler.playedSongsIndex.size());
							int i = WidgetHandler.playedSongsIndex.get(WidgetHandler.playedSongsIndex.size() - 1);
							//int i = 1 + songAdapter.getCursor().getPosition();
							
							Log.d("i >= songAdapter.getCursor().getCount() == ","i : "+i+" ..... songAdapter.getCursor().getCount()"+songAdapter.getCursor().getCount());
							
						 	if(i >= songAdapter.getCursor().getCount() - 1){
						      	//i = randomTrackIndex.nextInt(songAdapter.getCursor().getCount() - 1);
						 
						      	//widgetHandler.playSong(mp,songAdapter.getCursor(),i, true);
						 		
						 		widgetHandler.playSong(mp,songAdapter.getCursor(),0, true);
						    }
						 	else{
						 		widgetHandler.playSong(mp,songAdapter.getCursor(),i+1, true);
						 	}
						 	
						 	Log.d("New Song Name ......................... == ","....."+WidgetHandler.songNameView.getText());
						 	
						 	if(LockScreenAppActivity.LockscreenSong != null){
						 		LockScreenAppActivity.LockscreenSong.setText(WidgetHandler.songNameView.getText());
						 	}
						 	
						 	if(LockScreenAppActivity.LocksreenArtist != null){
						 		LockScreenAppActivity.LocksreenArtist.setText(WidgetHandler.songArtistView.getText());
						 	}
						 	
						 	if(HomeScreen.notifyMgr != null && customNotify != null){
						 		customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
						 		new MediaPlayerService().updateNotify(notifyMgr, customNotify);	   			 		
						 	}
						}
						else if((MediaPlayerService.counterRepeat % 2) != 0){
							int currentIndex = WidgetHandler.playedSongsIndex.get(WidgetHandler.playedSongsIndex.size() - 1);
							Log.d("Is Repeat ON ......................... == ","....."+currentIndex);
							widgetHandler.playSong(mp,songAdapter.getCursor(),currentIndex, true);
							
							if(LockScreenAppActivity.LockscreenSong != null){
						 		LockScreenAppActivity.LockscreenSong.setText(WidgetHandler.songNameView.getText());
						 	}
							
							if(LockScreenAppActivity.LocksreenArtist != null){
						 		LockScreenAppActivity.LocksreenArtist.setText(WidgetHandler.songArtistView.getText());
						 	}
							
							if(HomeScreen.notifyMgr != null && customNotify != null){
						 		customNotify.setTextViewText(R.id.notifySong, WidgetHandler.songNameView.getText());
						 		new MediaPlayerService().updateNotify(notifyMgr, customNotify);	
						 	}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						Log.d("ArrayIndexOutOfBoundsException  ......................... == ",".....App will play First song !!");
						
						widgetHandler.playSong(mp,songAdapter.getCursor(),0, true);
					}  			 			 			
		  		}   		
		  	}); 
		
		flipperAreaTwo.setOnTouchListener(new OnSwipeTouchListener(currentActivityContext){
		    public void onSwipeRight() {
		    	
			   // if(firstItemClicked != 0){
		    	
		    	Log.d("prevBtnClickCounter .............................", "..............."+prevBtnClickCounter);
			    	
			    	prevBtnClickCounter = (1 + prevBtnClickCounter);
		            mp.stop();
		            int i = -1 + (widgetHandler.playedSongsIndex.size() - prevBtnClickCounter);
		      
		            if ((i < 0) || (widgetHandler.playedSongsIndex.size() == 0) || (i > widgetHandler.playedSongsIndex.size()))
		            {
		              Log.d("Inside if when .............................", "playSongAtThisIndex < 0");
		              //prevTrackIndex = randomTrackIndex.nextInt(cursor.getCount());
		              //widgetHandler.playSong(mp,cursor, prevTrackIndex, true);
		              widgetHandler.playSong(mp,cursor, 0, true);
		            }
		            /*else if(i > widgetHandler.playedSongsIndex.size()){	            	
		            	prevTrackIndex = randomTrackIndex.nextInt(cursor.getCount());
		            	widgetHandler.playSong(mp,cursor, prevTrackIndex, true);
		            }*/
		            else{
		            	prevTrackIndex = ((Integer)widgetHandler.playedSongsIndex.get(i)).intValue();
		            	widgetHandler.playSong(mp,cursor, prevTrackIndex, false);
		            }

		    }
		    public void onSwipeLeft() {
		    	
			    	mp.stop();
		            int i = (widgetHandler.playedSongsIndex.size() - prevBtnClickCounter);
		      
		            if ((i < 0) || (widgetHandler.playedSongsIndex.size() == 0))
		            {
		              Log.d("Inside if when .............................", "playSongAtThisIndex < 0");
		              //prevTrackIndex = randomTrackIndex.nextInt(cursor.getCount());
		              
		              widgetHandler.playSong(mp,cursor, 0, true);
		            }
		            else if(i > (widgetHandler.playedSongsIndex.size() - 1)){	            	
		            	//prevTrackIndex = randomTrackIndex.nextInt(cursor.getCount());
		            	int prevTrackIndex = WidgetHandler.playedSongsIndex.get(WidgetHandler.playedSongsIndex.size() - 1);
		            	if(prevTrackIndex >= songAdapter.getCursor().getCount() - 1){
		            		//prevTrackIndex = randomTrackIndex.nextInt(songAdapter.getCursor().getCount() - 1);
		            		prevTrackIndex = 0;
		   		          	
		   		        }
		            	else{
		            		prevTrackIndex = prevTrackIndex + 1;
		            	}
		            	widgetHandler.playSong(mp,cursor, prevTrackIndex, true);
		            }
		            else{
		            	prevTrackIndex = ((Integer)widgetHandler.playedSongsIndex.get(i)).intValue();
		            	widgetHandler.playSong(mp,cursor, prevTrackIndex, false);
		            	prevBtnClickCounter = prevBtnClickCounter - 1;
		            }

		    }
		
		public boolean onTouch(View v, MotionEvent event) {

		//	if(firstItemClicked != 0){e
			// TODO Auto-generated method stub
			switch(event.getAction() & MotionEvent.ACTION_MASK)
	        {
	        case MotionEvent.ACTION_DOWN:
	            break;
	        case MotionEvent.ACTION_UP:
	        	
	            if(mp.isPlaying())
	            {
	              if(mp != null){
	            	  Log.d("The song is playing !! ", "");
	              	  mp.pause();
	              	Toast.makeText(currentActivityContext, "Music Paused !!", 1000).show();		              	
		              	viewDash();
	              	
	             }
	           }
	            else
	            { 
	            	Log.d("The songs is not playing !! ", "");
	            	if(mp != null){            		
	            		if(flipperAreaTwo.getDisplayedChild() != 3){
		            		mp.start();
		            		Log.d("The songs is playing ?? ", ""+mp.isPlaying());
		            		Toast.makeText(currentActivityContext, "Music is Playing !!", 1000).show();
	            		}
		            	hideDash();
	            		
	            	}	            
	            }

	        }
			
	//	}
		
		    return gestureDetector.onTouchEvent(event);
		}
		});
      
    }  
	
  public void onLoaderReset(Loader<Cursor> paramLoader)
  {
    if (songAdapter != null)
    	songAdapter.swapCursor(null);
    
     Log.v("onLoaderReset called !!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "onLoaderReset called !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      //Log.v(TAG, "OnLoadFinished: mAdapter is null");
  }
  
  @Override
  public void onBackPressed() {
	  moveTaskToBack(true);
  }

@Override
public boolean onQueryTextChange(String newText) {
	// TODO Auto-generated method stub
	
	cursorFilter = !TextUtils.isEmpty(newText) ? newText : null;
    //getLoaderManager().restartLoader(0, null, this);
	loaderManager.restartLoader(0, null, this);
    return true;

}

public void restartLoaderCursor(){
	loaderManager.restartLoader(0, null, this);
}


@Override
public boolean onQueryTextSubmit(String query) {
	// TODO Auto-generated method stub
	return false;
}

public static void viewDash(){
  	LinearLayout[] dashArray = {dash1,dash2,dash3,dash4};
  	
  	for(LinearLayout i:dashArray){
  		Log.d("i is :",":::::::::::: "+i);
  		if(i != null){
  			i.setVisibility(View.VISIBLE);
  		}
  	}
  	
}

public static void hideDash(){
LinearLayout[] dashArray = {dash1,dash2,dash3,dash4};
  	
  	for(LinearLayout i:dashArray){
  		if(i != null){
  			i.setVisibility(View.GONE);
  		}
  	}
}

public File saveCustomCover(){
	Bitmap bitMap = BitmapFactory.decodeResource(getResources(),R.drawable.cover);

	String fileName ="cover.jpeg";
	
	String mPath = null;
	
	File f = null;

	try {
	                                 
	FileOutputStream out1=openFileOutput(fileName, Context.MODE_PRIVATE);

	bitMap.compress(Bitmap.CompressFormat.JPEG, 100, out1);

	out1.flush();

	out1.close();

	f=getFileStreamPath(fileName);

	mPath=f.getAbsolutePath();

	       } catch (FileNotFoundException e1) {
	                                  // TODO Auto-generated catch block
	                                  e1.printStackTrace();
	                           } catch (IOException e) {
	                                  // TODO Auto-generated catch block
	                                  e.printStackTrace();
	                           }
	return f;

}
 
public void setDefaultChild(){
	flipperAreaTwo.setDisplayedChild(1);
	WidgetHandler.songNameView.setVisibility(View.VISIBLE);
	
}

public void convertNinePatchToBitmap(){
	Drawable iconDrawable = this.currentActivityContext.getResources().getDrawable(R.drawable.trial);
	
    Bitmap src = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
	 
    Canvas localCanvas = new Canvas(src);
    iconDrawable.setBounds(0, 0, localCanvas.getWidth(), localCanvas.getHeight());
    iconDrawable.draw(localCanvas);
    
    //return new BitmapDrawable(src);
}

@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		
		Toast t = new Toast(this.currentActivityContext).makeText(this.currentActivityContext, "Low Memory Error !!", Toast.LENGTH_LONG);
		t.show();
		
	}

public static void callGC(){
	try{
		Log.d("Signal to call GC .........", ".....................");
	}
	finally{
		System.gc();
	}
}

public void listenToTelephony(){
	  try{
		     StateListener phoneStateListener = new StateListener();
		     TelephonyManager telephonyManager =(TelephonyManager)this.currentActivityContext.getSystemService(TELEPHONY_SERVICE);
		     telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
		     }catch(Exception e){
		    	 System.out.println(e);
		     }
}

public void initiatePopupWindow(Activity activity) {
	try {
		
		final Dialog dialog = new Dialog(activity);
		dialog.getWindow();
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.license_dialog);
		
		ImageView btnClosDialog = (ImageView) dialog.findViewById(R.id.btnCloseDialog);
		
		arrayTV[0] = (TextView) dialog.findViewById(R.id.File_Title);
		arrayTV[1] = (TextView) dialog.findViewById(R.id.File_One);
		arrayTV[2] = (TextView) dialog.findViewById(R.id.File_Second);
		arrayTV[3] = (TextView) dialog.findViewById(R.id.File_Third);
		arrayTV[4] = (TextView) dialog.findViewById(R.id.File_Fourth);
		arrayTV[5] = (TextView) dialog.findViewById(R.id.File_Fifth);
		arrayTV[6] = (TextView) dialog.findViewById(R.id.File_Sixth);
		arrayTV[7] = (TextView) dialog.findViewById(R.id.File_Seventh);
		arrayTV[8] = (TextView) dialog.findViewById(R.id.File_Eighth);
		arrayTV[9] = (TextView) dialog.findViewById(R.id.File_Ninth);
		
		
		arrayTV[10] = (TextView) dialog.findViewById(R.id.License_One);
		arrayTV[11] = (TextView) dialog.findViewById(R.id.License_Second);
		arrayTV[12] = (TextView) dialog.findViewById(R.id.License_Third);
		arrayTV[13] = (TextView) dialog.findViewById(R.id.License_Fourth);
		arrayTV[14] = (TextView) dialog.findViewById(R.id.License_Fifth);
		arrayTV[15] = (TextView) dialog.findViewById(R.id.License_Sixth);
		arrayTV[16] = (TextView) dialog.findViewById(R.id.License_Seventh);
		arrayTV[17] = (TextView) dialog.findViewById(R.id.License_Eighth);
		arrayTV[18] = (TextView) dialog.findViewById(R.id.License_Ninth);
		
		arrayTV[17].setText(Html.fromHtml(htmlText1));
		arrayTV[18].setText(Html.fromHtml(htmlText2));	
		
		for(TextView tv:arrayTV){
			tv.setTypeface(songNameFont);
		}
		
		btnClosDialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog.cancel();
				
			}
		});
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
		
		
	} 
	catch (Exception e) {
		e.printStackTrace();
	}
}


private void crossfade(final View mLoadingView, View mContentView) {

    // Set the content view to 0% opacity but visible, so that it is visible
    // (but fully transparent) during the animation.
    mContentView.setAlpha(0f);
    mContentView.setVisibility(View.VISIBLE);

    // Animate the content view to 100% opacity, and clear any animation
    // listener set on the view.
    mContentView.animate()
            .alpha(1f)
            .setDuration(1000)
            .setListener(null);

    // Animate the loading view to 0% opacity. After the animation ends,
    // set its visibility to GONE as an optimization step (it won't
    // participate in layout passes, etc.)
    mLoadingView.animate()
            .alpha(0f)
            .setDuration(1000)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingView.setVisibility(View.GONE);
                }
            });
}

public void CopyRAWtoSDCard(int id, String path) throws IOException {
    InputStream in = getResources().openRawResource(id);
    FileOutputStream out = new FileOutputStream(path);
    byte[] buff = new byte[1024];
    int read = 0;
    try {
        while ((read = in.read(buff)) > 0) {
            out.write(buff, 0, read);
        }
    } finally {
        in.close();
        out.close();
    }
}

}
