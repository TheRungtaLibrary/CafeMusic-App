package com.bosned.audioMod;

import java.util.Random;

import com.cafe.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class VisualizerView extends View {
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private int mDivisions = 2;
    Boolean mTop = false;
    float colorCounter = 0;
    Random randomColor = new Random();

    private Paint mForePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBytes = null;
        
        mForePaint.setStrokeWidth(10);
        mForePaint.setShadowLayer(50, 0, 0, getResources().getColor(R.color.Orange));
        mForePaint.setAntiAlias(true);
        mForePaint.setDither(true);
        //mForePaint.setColor(Color.argb(240, 255, 255, 255));
        mForePaint.setColor(Color.argb(240, 255, 155, 0));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

       @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        cycleColor();
        
        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());
        
        for (int i = 0; i < mBytes.length / mDivisions; i++) {
        	
        	if(i%2 == 0){
        	
            mPoints[i *4] = i * 4 * mDivisions;
            mPoints[i * 4 + 2] = i * 4 * mDivisions;
            byte rfk = mBytes[mDivisions * i];
            byte ifk = mBytes[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (30 * Math.log10(magnitude));

            if(mTop)
            {
              mPoints[i * 4 + 1] = 0;
              mPoints[i * 4 + 3] = (dbValue * 2 - 10);
            }
            else
            {
              mPoints[i * 4 + 1] = mRect.height();
              mPoints[i * 4 + 3] = mRect.height() - (dbValue * 4 - 10);
            }
            
        }

          }

        canvas.drawLines(mPoints, mForePaint);
        
    }
       
       private void cycleColor()
       {   	 
    	 
/*    	 int r = randomColor.nextInt(255);  
    	 int g = randomColor.nextInt(255);
    	 int b = randomColor.nextInt(255);
         mForePaint.setColor(Color.argb(240, r, g, b));*/
    	   
	    /*int r = (int)Math.floor(128*(Math.sin(colorCounter) + 1));
	    int g = (int)Math.floor(128*(Math.sin(colorCounter + 2) + 1));
	    int b = (int)Math.floor(128*(Math.sin(colorCounter + 4) + 1));
	    mForePaint.setColor(Color.argb(240, r, g, b));
	    colorCounter += 0.02;
*/
       }
	
}
