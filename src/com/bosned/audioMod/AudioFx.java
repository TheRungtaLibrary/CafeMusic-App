package com.bosned.audioMod;

import com.cafe.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;
import android.support.v4.content.Loader;

public class AudioFx extends Activity{

	private MediaPlayer mp;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
    private Virtualizer mVirtualizer;

    LinearLayout equalizerlayout;
    LinearLayout equalizerlayout1;
    RelativeLayout visualizerLayout;
    private VisualizerView mVisualizerView;
    public Activity currentActivityContext;
    SeekBar barVirtualizer;
    Typeface textFont;

    public AudioFx(Activity activity, MediaPlayer player, LinearLayout param_equalizerlayout, RelativeLayout visualizerLayout1) {

//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    	
    	this.currentActivityContext = activity;
        equalizerlayout = param_equalizerlayout;
        equalizerlayout1 = (LinearLayout) this.currentActivityContext.findViewById(R.id.viewEqualizer1);
        visualizerLayout = visualizerLayout1;
        barVirtualizer = (SeekBar) this.currentActivityContext.findViewById(R.id.VirtualizerBar);
        
        textFont = Typeface.createFromAsset(this.currentActivityContext.getAssets(), "neuropol-x-free.regular.ttf");
        
        Log.d("equalizerlayout ..................... : ","..."+equalizerlayout);
        //equalizerlayout.setOrientation(LinearLayout.VERTICAL);

 //       activity.setContentView(equalizerlayout);

        // Create the MediaPlayer
        mp = player;
              
        setupEqualizerFxAndUI(this.currentActivityContext);
        setupVirtualizer(this.currentActivityContext);
        setupVisualizerFxAndUI(this.currentActivityContext);

        // Make sure the visualizer is enabled only when you actually want to receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);

        // When the stream ends, we don't need to collect any more data. We don't do this in
        // setupVisualizerFxAndUI because we likely want to have more, non-Visualizer related code
        // in this callback.
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
            	//mp.setLooping(true);
                mVisualizer.setEnabled(true);
            }
        });

        //mp.start();
        
//        presetSpinner.setOnItemSelectedListener(this);
    }
    

    private void setupEqualizerFxAndUI(Activity activity) {
        // Create the Equalizer object (an AudioEffect subclass) and attach it to our media player,
        // with a default priority (0).
        mEqualizer = new Equalizer(0, mp.getAudioSessionId());
        mEqualizer.setEnabled(true);

        TextView eqTextView = new TextView(activity);
        eqTextView.setText("Equalizer:");
        //equalizerlayout.addView(eqTextView);

        short bands = mEqualizer.getNumberOfBands();

        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;
            Log.d("bands: "," ......................................... "+i);
            TextView freqTextView = new TextView(activity);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setText((mEqualizer.getCenterFreq(band) / 1000) + " Hz");
            //freqTextView.setTypeface(textFont);
            
            if(i == 0){
            	equalizerlayout1.addView(freqTextView);
            }
            else{
            	equalizerlayout.addView(freqTextView);
            }

            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            /*LinearLayout.LayoutParams layoutRow = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutRow.setMargins(0, 100, 0, 0);
            row.setLayoutParams(layoutRow);*/

            TextView minDbTextView = new TextView(activity);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setGravity(Gravity.RIGHT);
            minDbTextView.setText((minEQLevel / 100) + " dB");
            //minDbTextView.setTypeface(textFont);

            TextView maxDbTextView = new TextView(activity);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setGravity(Gravity.LEFT);
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            //maxDbTextView.setTypeface(textFont);
            

            /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);*/
            
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    100,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            
            layoutParams.weight = 1;
            
            SeekBar bar = new SeekBar(activity);
            bar.setProgressDrawable(activity.getResources().getDrawable(R.drawable.styled_progress));
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(band));
            bar.setThumb(activity.getResources().getDrawable(R.drawable.orange_pointer));
            bar.setThumbOffset(0);
            //bar.setRotation(270);

            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);
            
            if(i == 0){
            	equalizerlayout1.addView(row);
            }
            else{
            	equalizerlayout.addView(row);
            }
        }
                
    }

    @SuppressWarnings("deprecation")
	private void setupVisualizerFxAndUI(Activity activity) {
        // Create a VisualizerView (defined below), which will render the simplified audio
        // wave form to a Canvas.
        mVisualizerView = new VisualizerView(activity);
        /*mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int)(VISUALIZER_HEIGHT_DIP * activity.getResources().getDisplayMetrics().density)*3));*/
        
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        //mVisualizerView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        
        visualizerLayout.addView(mVisualizerView);
        Log.d("visualizerLayout ..................... : ","..."+visualizerLayout);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mp.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
       mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                    int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) { 
            	mVisualizerView.updateVisualizer(bytes);
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }
    
    private void setupVirtualizer(Activity activity){
    	mVirtualizer = new Virtualizer(0, mp.getAudioSessionId());
    	mVirtualizer.setEnabled(true);
    	barVirtualizer.setMax(1000);
    	barVirtualizer.setProgressDrawable(activity.getResources().getDrawable(R.drawable.styled_progress));
    	barVirtualizer.setThumb(activity.getResources().getDrawable(R.drawable.orange_pointer));
    	barVirtualizer.setThumbOffset(0);
    	barVirtualizer.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub				
				Log.d("mVirtualizer.setStrength initial !!!!!", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+mVirtualizer.getRoundedStrength());
				mVirtualizer.setStrength((short)progress);
				Log.d("mVirtualizer.setStrength final!!!!!", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+mVirtualizer.getRoundedStrength());
			}
		});
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing() && mp != null) {
            mVisualizer.release();
            mEqualizer.release();
            mp.release();
            mp = null;
        }
    }
    
    public View getAudioModView(){
    	Log.d("Parent ..................... : ","..."+equalizerlayout.getParent());
    	return equalizerlayout;
    }

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		
		String presetValue = String.valueOf(parent.getSelectedItem());
		changeEqualizerPreset(presetValue);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void changeEqualizerPreset(String presetName){
		
	}
}

/**
 * A simple class that draws waveform data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
 */