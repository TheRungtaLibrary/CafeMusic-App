package com.bosned.songInfo;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.bosned.WidgetHandler;
import com.bosned.songInfo.CircularSeekBar.OnCircularSeekBarChangeListener;

public class CircularSeekBarListener implements OnCircularSeekBarChangeListener{
	
	MediaPlayer mp;
	private Handler handler = new Handler();
	
	public CircularSeekBarListener(MediaPlayer player){
		mp = player;
	}

	@Override
	public void onProgressChanged(CircularSeekBar circularSeekBar,
			int progress, boolean fromUser) {
		// TODO Auto-generated method stub
/*		Log.d("progress : ", "............"+progress);
		PopulateSongInfoArea test =  new PopulateSongInfoArea();
		SongDurationHandler SongDurationHandlerObject = new SongDurationHandler();
		int totalTimeOfCurrentSong = mp.getDuration();
		int seekEndTime = SongDurationHandlerObject.progressToTimer(progress, totalTimeOfCurrentSong );
		mp.seekTo(seekEndTime);*/
	}

}
