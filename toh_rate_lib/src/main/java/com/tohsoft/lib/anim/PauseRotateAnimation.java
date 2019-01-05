package com.tohsoft.lib.anim;

import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

public class PauseRotateAnimation extends RotateAnimation {	
	 private long mElapsedAtPause=0;
     private boolean mPaused=false;
	

     public PauseRotateAnimation(float fromDegrees, float toDegrees, int pivotXType, float pivotXValue,
             int pivotYType, float pivotYValue) {
       super(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
     }

	
	
	 @Override
	    public boolean getTransformation(long currentTime, Transformation outTransformation) { 
	        if(mPaused && mElapsedAtPause==0) {
	            mElapsedAtPause=currentTime-getStartTime();
	        }
	        if(mPaused)
	            setStartTime(currentTime-mElapsedAtPause);
	        return super.getTransformation(currentTime, outTransformation);
	    }

	    public void pause() {
	        mElapsedAtPause=0;
	        mPaused=true;
	    }

	    public void resume() {
	        mPaused=false;
	    }
}
