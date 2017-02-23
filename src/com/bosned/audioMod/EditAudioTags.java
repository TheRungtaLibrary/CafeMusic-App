package com.bosned.audioMod;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosned.HomeScreen;
import com.cafe.R;
import com.bosned.WidgetHandler;

public class EditAudioTags {

	String currentSongPath = null;
	String title = null;
	String artist = null;
	String album = null;
	Activity currentActivity;
	Typeface textFont;
	private EditText EditTitle;
	private EditText EditArtist;
	private EditText EditAlbum;
	private TextView TitleTV;
	private TextView ArtistTV;
	private TextView AlbumTV;
	AlertDialog.Builder builder;
	LayoutInflater inflater;
	View view;
	AlertDialog alert;
	
	public EditAudioTags(Activity activity){
			
			this.currentActivity = activity;
			textFont = Typeface.createFromAsset(this.currentActivity.getAssets(), "neuropol-x-free.regular.ttf");
		
			builder = new AlertDialog.Builder(currentActivity); 
			
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
            	 setNewTagValues(currentSongPath);
             }
         });
			
	     alert = builder.create();
	}
	
	public void getDataFromTags(String	mPath) {
		currentSongPath = mPath;
		try {
			
				title = (String) WidgetHandler.songNameView.getText();
		     	artist = (String) WidgetHandler.songArtistView.getText();
		     	album = WidgetHandler.currentAlbum;
	   
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   		   
	}
	
	public void setEditDialog(){
	
		EditTitle.setText(title);
		EditArtist.setText(artist);
		EditAlbum.setText(album);

	        alert.show();

	        Button btn2 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
	        Button btn1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
	        btn1.setTypeface(textFont);
	        btn2.setTypeface(textFont);
	}
	
	public void setNewTagValues(String currentSongPath){

		try {				  
				   
				   AudioFile f = AudioFileIO.read(new File(currentSongPath));
				   Tag tag = f.getTagOrCreateAndSetDefault();
				   
				   Log.d("tag........  =  ", ""+tag);
				   Log.d("EditTitle........  =  ", ""+EditTitle);
				   Log.d("EditTitle.getText().toString()........  =  ", ""+EditTitle.getText().toString());
				   
				   tag.setField(FieldKey.TITLE, EditTitle.getText().toString());
				   tag.setField(FieldKey.ARTIST, EditArtist.getText().toString());
				   tag.setField(FieldKey.ALBUM, EditAlbum.getText().toString());
				   AudioFileIO.write(f);
				   
				   new MediaScannerWrapper(currentActivity, currentSongPath, "audio/*").scan();
				   Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				   intent.setData(Uri.fromFile(new File(currentSongPath)));
				   currentActivity.sendBroadcast(intent);
				   
				   WidgetHandler.songNameView.setText(EditTitle.getText().toString());
				   WidgetHandler.songArtistView.setText(EditArtist.getText().toString());

				   Toast songUpdatedMsgToast = Toast.makeText(currentActivity, "The Song is updated !!", Toast.LENGTH_SHORT);
				   songUpdatedMsgToast.show();
				   
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CannotReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CannotWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    
	}
	
}
