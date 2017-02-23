package com.bosned.audioMod;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosned.HomeScreen;
import com.cafe.R;
import com.bosned.WidgetHandler;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.bosned.audioMod.RecMicToMp3;

public class AudioRecord extends Activity {

   private MediaPlayer myPlayer;
   private TextView startBtn;
   private TextView playBtn;
   Activity currentActivity;
   Typeface textFont;
   int hasRecorded = 0;
   AnimationDrawable recAnimation;
   ImageView img1;
   ImageView img2;
   
	private EditText EditTitle;
	private EditText EditArtist;
	private EditText EditAlbum;
	
	private TextView TitleTV;
	private TextView ArtistTV;
	private TextView AlbumTV;
	
	LayoutInflater inflater;
	View view;
	
	int anonymousNameCounter = 1;
	boolean isAnonymous = false;
	
	AlertDialog.Builder builder;
   
   RecMicToMp3 mRecMicToMp3 = new RecMicToMp3(
			Environment.getExternalStorageDirectory().
  		  getAbsolutePath()  + "/MusicCafe.mp3", 8000);
   
   
   
   public AudioRecord(Activity activity, final File mPath) {
	   this.currentActivity = activity;
      // store it to sd card
	   

	   
     // outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CafeMusic.mp3";
      
      textFont = Typeface.createFromAsset(this.currentActivity.getAssets(), "neuropol-x-free.regular.ttf");
      
      img1 = (ImageView) this.currentActivity.findViewById(R.id.img1);
      img2 = (ImageView) this.currentActivity.findViewById(R.id.img2);
      
      recAnimation=(AnimationDrawable) img2.getDrawable();
      Log.d("recAnimation .................."," ............... "+recAnimation);
      startBtn = (TextView)this.currentActivity.findViewById(R.id.button1);
      startBtn.setTypeface(textFont);
      startBtn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			stopPlay();
			mRecMicToMp3.start();
			//start(v);
			hasRecorded = 1;
			Log.d("Clicked .................."," !!!!!!!!!!!!!!");		
			img1.setVisibility(View.GONE);
			img2.setVisibility(View.VISIBLE);
			recAnimation.start();
		}
      });
 
      playBtn = (TextView)this.currentActivity.findViewById(R.id.button2);
      playBtn.setTypeface(textFont);
      playBtn.setOnClickListener(new OnClickListener() {
  		
  		@SuppressWarnings("deprecation")
		@Override
  		public void onClick(View v) {
  			// TODO Auto-generated method stub
  			if(hasRecorded == 1){ 		
  				//stop(v);
  				mRecMicToMp3.stop();
  				recAnimation.stop();
  				img2.setVisibility(View.GONE);
  				img1.setVisibility(View.VISIBLE);
				play(v);				
				saveDialog(mPath);
				hasRecorded = 2;
  			}
  			else if(hasRecorded == 2){
  				play(v);
  			}
  			else{
  				Toast toast = Toast.makeText(currentActivity.getApplication().getApplicationContext(), "Record something to play !!", Toast.LENGTH_SHORT);
				toast.show();
  			}
  		}
      });
               
    }
  
   public void play(View view) {
	   try{
		   myPlayer = new MediaPlayer();
		   myPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()  + "/MusicCafe.mp3");
		   myPlayer.prepare();
		   myPlayer.start();
		   
	   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   
   public void stopPlay() {
	   try {
	       if (myPlayer != null) {
	    	   myPlayer.stop();
	           myPlayer.release();
	           //myPlayer = null;

	       }
	   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   
   private void saveDialog(final File mPath){
	    builder = new AlertDialog.Builder(currentActivity); 
	    
        /*final EditText input = new EditText(currentActivity);
        input.setBackgroundColor(currentActivity.getResources().getColor(R.color.Black));
        input.setTextColor(currentActivity.getResources().getColor(R.color.white));
		input.setTypeface(textFont);
		input.setHint("Give it a name !!!");
		input.setHintTextColor(currentActivity.getResources().getColor(R.color.white));*/
		
	    inflater = this.currentActivity.getLayoutInflater();
		view = inflater.inflate(R.layout.dialog, null);
				
		EditTitle = (EditText) view.findViewById(R.id.editTitle);
		EditArtist = (EditText) view.findViewById(R.id.editArtist);
		EditAlbum = (EditText) view.findViewById(R.id.editAlbum);
		TitleTV = (TextView) view.findViewById(R.id.titleTV);
		ArtistTV = (TextView) view.findViewById(R.id.artistTV);
		AlbumTV = (TextView) view.findViewById(R.id.albumTV);
		
		EditTitle.setTypeface(textFont);
		EditArtist.setTypeface(textFont);
		EditAlbum.setTypeface(textFont);
		
		TitleTV.setTypeface(textFont);
		ArtistTV.setTypeface(textFont);
		AlbumTV.setTypeface(textFont);

		
	    
	        builder.setView(view) 
	               .setCancelable(false)
	               .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	
	                	
						try {
							String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
							String selectedFilePath = new File(sdcard,"MusicCafe.mp3").getAbsolutePath();
		                	File file = new File(selectedFilePath);
							boolean deleted = file.getCanonicalFile().delete();
							if(deleted){
								stopPlay();
								dialog.cancel();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                	                    
	                 }
	               })
	               .setPositiveButton("Yup", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                    //do some thing	               	
	
						try {
							   String fileName = EditTitle.getText().toString();
							   String artistName = EditArtist.getText().toString();
							   String albumName = EditAlbum.getText().toString();
							   
							   for(String elem : new String[]{fileName, artistName, albumName}){
								   if(elem.equalsIgnoreCase("")){
									   elem = "unknown"+anonymousNameCounter;
									   isAnonymous = true;
								   }
							   }
							   
							   if(isAnonymous){
								   anonymousNameCounter++;
							   }
							   
							   String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
							   Mp3File mp3file = new Mp3File(new File(sdcard,"MusicCafe.mp3").getAbsolutePath());
							   
							   ID3v2 id3v2Tag;
							   if (mp3file.hasId3v2Tag()) {
							     id3v2Tag = mp3file.getId3v2Tag();
							   } else {
							     // mp3 does not have an ID3v2 tag, let's create one..
							     id3v2Tag = new ID3v24Tag();
							     mp3file.setId3v2Tag(id3v2Tag);
							   }
							   
							   id3v2Tag.setAlbumImage(readFile(mPath), getMimeType(mPath.getAbsolutePath()));
							   //id3v2Tag.setAlbumImage(readFile(mPath), "image/jpeg");
							   
							   id3v2Tag.setTitle(fileName);
							   id3v2Tag.setArtist(artistName);							   
							   id3v2Tag.setAlbum(albumName);
							   
							   mp3file.save(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName + ".mp3");

							   
							   new MediaScannerWrapper(currentActivity, Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName + ".mp3", "audio/*").scan();
							   Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
							   intent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName + ".mp3")));
							   currentActivity.sendBroadcast(intent);
							   
							  
							   
							   
							   //updateArt(mPath, fileName, "TUSHAR", currentActivity);
							   
							   /*new MediaScannerWrapper(currentActivity, Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName, "audio/*").scan();
							   currentActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + PathsHandler.getInstance().getRecordingsDirectory())));*/
							   
							   Toast toast = Toast.makeText(currentActivity.getApplication().getApplicationContext(), "Your song's been saved as "+fileName+" !!!", Toast.LENGTH_SHORT);
							   toast.show();
							   
						} catch (NotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedTagException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvalidDataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                	
	                }
	            });

	        AlertDialog alert = builder.create();
	        alert.show();

	        Button btn2 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
	        Button btn1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
	        btn1.setTypeface(textFont);
	        btn2.setTypeface(textFont);
	               
	}
   
   public static byte[] readFile (File file) throws IOException {
	    // Open file
	    RandomAccessFile f = new RandomAccessFile(file, "r");

	    try {
	        // Get and check length
	        long longlength = f.length();
	        int length = (int) longlength;
	        if (length != longlength) throw new IOException("File size >= 2 GB");

	        // Read file and return data
	        byte[] data = new byte[length];
	        f.readFully(data);
	        return data;
	    }
	    finally {
	        f.close();
	    }
	}
  
   public static String getMimeType(String url)
   {
       String type = null;
       String extension = MimeTypeMap.getFileExtensionFromUrl(url);
       if (extension != null) {
           MimeTypeMap mime = MimeTypeMap.getSingleton();
           type = mime.getMimeTypeFromExtension(extension.toLowerCase());
       }
       return type;
   }

}
