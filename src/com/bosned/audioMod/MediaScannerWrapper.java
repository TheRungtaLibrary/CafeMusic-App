package com.bosned.audioMod;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class MediaScannerWrapper implements MediaScannerConnectionClient {

    private MediaScannerConnection mConnection;
    private String mPath;
    private String mMimeType;

    public MediaScannerWrapper(Context ctx, String filePath, String mime){
        mPath = filePath;
        mMimeType = mime;
        mConnection = new MediaScannerConnection(ctx, this);
    }

    public void scan(){
        mConnection.connect();
    }

   @Override
   public void onMediaScannerConnected() {
       mConnection.scanFile(mPath, mMimeType);
       Log.d(getClass().getName(), "Media file scanned: "+mPath);
   }

@Override
public void onScanCompleted(String path, Uri uri) {
	// TODO Auto-generated method stub
	
}

}
