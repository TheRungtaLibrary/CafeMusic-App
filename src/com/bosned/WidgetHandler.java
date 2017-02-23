package com.bosned;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bosned.audioMod.EditAudioTags;
import com.bosned.songInfo.CircularSeekBar;
import com.bosned.songInfo.SongDurationHandler;
import com.cafe.R;
import com.lockscreen.LockScreenAppActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class WidgetHandler extends Activity implements OnSeekBarChangeListener
{
  Drawable[] arrayDrawable;
  Activity currentActivity;
  String currentAlbumId;
  public static String currentAlbum;
  Handler handler = new Handler();
  public static MediaPlayer mp;
  static ArrayList<Integer> playedSongsIndex = new ArrayList();
  ImageView songArtView;
  String songArtist;
  public static TextView songArtistView;
  public static TextView songDurationView;
  String songName;
  Typeface artistNameFont;
  public static TextView songNameView;
  public static CircularSeekBar barSongDuration; 
  public static Handler barHandler = new Handler();
  public static Handler tagDataHandler = new Handler();
  private static SongDurationHandler SongDurationHandlerObject = new SongDurationHandler();
  public static String currentSongPath;
  public static EditAudioTags editAudioTags;
  public Drawable backUpDrawable = null;
  
  public WidgetHandler(MediaPlayer paramMediaPlayer, Activity paramActivity, Cursor paramCursor)
  {
    mp = paramMediaPlayer;
    currentActivity = paramActivity;
    songNameView = ((TextView)this.currentActivity.findViewById(2131230732));
    songArtistView = ((TextView)this.currentActivity.findViewById(R.id.ArtistNameView));
    songDurationView = ((TextView)this.currentActivity.findViewById(2131230734));
    songArtView = ((ImageView)this.currentActivity.findViewById(2131230728));
    barSongDuration = (CircularSeekBar) this.currentActivity.findViewById(R.id.SongDurationBar);
    //Log.d("Player Now : ", "..." + this.mp.getCurrentPosition() + " " + this.mp.getDuration());
    //artistNameFont = Typeface.createFromAsset(this.currentActivity.getAssets(), "green avocado.ttf");
    artistNameFont = Typeface.createFromAsset(this.currentActivity.getAssets(), "neuropol-x-free.regular.ttf");    
    songArtistView.setTypeface(artistNameFont);
    songDurationView.setTypeface(artistNameFont);
    songNameView.setTypeface(artistNameFont);
    songArtistView.setSelected(true);
    songNameView.setSelected(true);
    //barSongDuration.setThumb(null);   
    editAudioTags = new EditAudioTags(this.currentActivity);
    
    backUpDrawable = this.currentActivity.getResources().getDrawable(R.drawable.trial);
    
    handler.postDelayed(currentTimeUpdate, 1);
	barHandler.postDelayed(barUpdateTime, 1);
	tagDataHandler.postDelayed(updateTagData, 1);
	
	//barSongDuration.setOnSeekBarChangeListener(this);
  }

  private Runnable currentTimeUpdate = new Runnable(){

	@Override
	public void run() {
		// TODO Auto-generated method stub

		SongDurationHandlerObject.calculateSongDuration();

		handler.postDelayed(this, 1);
	}
	  
  };
  
  public void updateSongDurationBar(){
	  Log.d("updateSongDurationBar ..................","........... called !!!");
		barHandler.postDelayed(barUpdateTime, 1);
	}
  
  public static Runnable barUpdateTime = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub		
			SongDurationHandler.calculateProgressPercentage();
			barHandler.postDelayed(this, 1);
		}};						
		
  public static Runnable updateTagData = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			editAudioTags.getDataFromTags(WidgetHandler.currentSongPath);					
			
			tagDataHandler.postDelayed(this, 1);
		}};		
		
  public Drawable doGreyscale(Drawable paramDrawable) {
	    
	  Bitmap src = null;
	  try{
		  src = Bitmap.createBitmap(paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
		  Canvas localCanvas = new Canvas(src);
		    paramDrawable.setBounds(0, 0, localCanvas.getWidth(), localCanvas.getHeight());
		    paramDrawable.draw(localCanvas);
		 
		    Paint paint = new Paint();
		    float[] mat = new float[]{
		            0.3f, 0.59f, 0.11f, 0, 0, 
		            0.3f, 0.59f, 0.11f, 0, 0, 
		            0.3f, 0.59f, 0.11f, 0, 0, 
		            0, 0, 0, 1, 0,};
	        ColorMatrixColorFilter f = new ColorMatrixColorFilter(mat);
	        paint.setColorFilter(f);
	        localCanvas.drawBitmap(src, 0, 0, paint);
		 
		    // return final image
		    
	  }
	  catch(Exception e){
		  
		   src = BitmapFactory.decodeResource(this.currentActivity.getResources(), R.drawable.trial);
	  }
	    
	  return new BitmapDrawable(src);
	    
	}
  
  public void animateImageTransition(Drawable paramDrawable1, Drawable paramDrawable2, boolean isBackUp)
  {
    this.arrayDrawable[0] = paramDrawable1;
    this.arrayDrawable[1] = paramDrawable2;
    
    if(!isBackUp){
    	this.arrayDrawable[1] = doGreyscale(this.arrayDrawable[1]);   	    	
    }
    
    TransitionDrawable localTransitionDrawable = new TransitionDrawable(this.arrayDrawable);    
    localTransitionDrawable.setCrossFadeEnabled(true);
    this.songArtView.setBackgroundDrawable(localTransitionDrawable);    
    localTransitionDrawable.startTransition(3000);
    Log.d("faded Into ........  =  ", ""+this.songArtView.getBackground());
  }

  public static String getArt(String paramString, Activity paramActivity)
  {
    String str = "";
    ContentResolver localContentResolver = paramActivity.getContentResolver();
    Uri localUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, Long.parseLong(paramString));
    String[] arrayOfString = new String[1];
    arrayOfString[0] = "album_art";
    Cursor localCursor = localContentResolver.query(localUri, arrayOfString, null, null, null);
    if (localCursor.moveToFirst())
    {
      str = localCursor.getString(0);
      Log.d("Inside second query ...... Album Art", ".............." + str);
    }
    if (str == null)
      str = "";
    return str;
  }

  public void playSong(MediaPlayer paramMediaPlayer, Cursor paramCursor, int paramInt, boolean paramBoolean)
  {	
	 
//	if(HomeScreen.firstItemClicked !=0){
	  
		    paramCursor.moveToPosition(paramInt);	
		    
		    HomeScreen.currentSongPosition = paramInt;
		    
		    WidgetHandler.mp.reset();
		    try
		    {
		      if(paramCursor.getString(paramCursor.getColumnIndex("_data")) != null){
		      Log.d("the ITEM INDEX TO BE PLAYED is : ", ""+paramCursor.getString(paramCursor.getColumnIndex("_data")));
		      WidgetHandler.mp.setDataSource(paramCursor.getString(paramCursor.getColumnIndex("_data")));
		      Log.d("playedSongIndex is : ", ""+this.playedSongsIndex);
		      Log.d("the CURRENT POSITION OF PLAYER is : ", ""+WidgetHandler.mp.getCurrentPosition());
		      WidgetHandler.mp.prepare();
		      Log.d("HomeScreen.firstItemClicked is : ", ""+HomeScreen.firstItemClicked);
		      barSongDuration.setProgress(0);
		      barSongDuration.setMax(100);
		      if(HomeScreen.firstItemClicked != 0){
		    	  Log.d("WidgetHandler.mp.start() : ", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    	  WidgetHandler.mp.start();
					// Updating progress bar
		    	  //updateSongDurationBar();
		      }
		      else{
		    	  HomeScreen.firstItemClicked++;
		    	  
		    	  Log.d("viewDash called : ", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		    	  HomeScreen.viewDash();
		    	  
		      }
		      this.songName = paramCursor.getString(paramCursor.getColumnIndex("title"));
		      this.songArtist = paramCursor.getString(paramCursor.getColumnIndex("artist"));
		      this.currentAlbumId = paramCursor.getString(paramCursor.getColumnIndex("album_id"));
		      this.currentAlbum = paramCursor.getString(paramCursor.getColumnIndex("album"));
		      currentSongPath = paramCursor.getString(paramCursor.getColumnIndex("_data"));
		      Log.d("songName : ", this.songName);
		      Log.d("songArtist : ", this.songArtist);
		      Log.d("currentAlbumId : ", this.currentAlbumId);
		      Log.d("currentActivity : ", ""+this.currentActivity);
		      switchArt(this.currentAlbumId, this.songArtist, currentSongPath, this.currentActivity);
		      this.songNameView.setText(this.songName);
		      this.songArtistView.setText(this.songArtist);
		      
		      if(paramBoolean)
		        this.playedSongsIndex.add(Integer.valueOf(paramInt));
		      Log.d("playedSongIndex is : ", ""+this.playedSongsIndex);
		      Log.d("the CURRENT POSITION OF PLAYER is : ", ""+WidgetHandler.mp.getCurrentPosition());
		      Log.d("Song path Is : ", paramCursor.getString(paramCursor.getColumnIndex("_data")));		            			
		    }
		    }
		    catch (IllegalArgumentException localIllegalArgumentException)
		    {
		      while (true)
		        localIllegalArgumentException.printStackTrace();
		    }
		    catch (SecurityException localSecurityException)
		    {
		      while (true)
		        localSecurityException.printStackTrace();
		    }
		    catch (IllegalStateException localIllegalStateException)
		    {
		      while (true)
		        localIllegalStateException.printStackTrace();
		    }
		    catch (IOException localIOException)
		    {
		      while (true)
		        localIOException.printStackTrace();
		    }
  	//}
  }

  public void switchArt(String albumID, String songArtist, String currentSongPath, Activity activity)
  {
	  Drawable currentDrawable =  songArtView.getBackground();
	    
	        
	    Log.d("backUpDrawable ........  =  ",""+backUpDrawable);
	    if (currentDrawable != null) {
	     arrayDrawable = new Drawable[2];
			if(albumID != ""){
				String albumArt = getArt(albumID, activity);
				Log.d("albumArt ........  =  ",""+albumArt);
				if(albumArt != null && albumArt != ""){	    
			  	  
			  	  Drawable nextImage = Drawable.createFromPath(albumArt);
			  	  Log.d("nextImage ........  =  ",""+nextImage);
			  	  
			  	  animateImageTransition(currentDrawable, nextImage, false); 	
			    }
				else{
					animateImageTransition(currentDrawable, backUpDrawable, true);
			    }
			}
		    else{
		    	animateImageTransition(currentDrawable, backUpDrawable, true);
		    }
	    }
	    else{
	    	animateImageTransition(currentDrawable, backUpDrawable, true);
	    }
	    Log.d("changedDrawable ........  =  ",""+songArtView.getBackground());
  }

@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	// TODO Auto-generated method stub
	
}

@Override
public void onStartTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	
}

@Override
public void onStopTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	barHandler.removeCallbacks(barUpdateTime);
	int totalDuration = mp.getDuration();
	int currentPosition = SongDurationHandler.progressToTimer(seekBar.getProgress(), totalDuration);
	Log.d("Inside onStopTrackingTouch ........  =  ",""+currentPosition);
	// forward or backward to certain seconds
	mp.seekTo(currentPosition);
	
	// update timer progress again
	updateSongDurationBar();
}

}

/* Location:           D:\GaanaBajao-com.bosned-1-v1.0\dex2jar-0.0.9.9\classes_dex2jar.jar
 * Qualified Name:     com.bosned.WidgetHandler
 * JD-Core Version:    0.6.2
 */