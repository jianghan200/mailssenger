package com.mailssenger.slidinglayer;

import android.app.Activity;
import android.os.Bundle;

import com.mailssenger.R;
import com.mailssenger.slidinglayer.SlidingLayer.OnInteractListener;


public class SlidingLayerDemo extends Activity implements OnInteractListener {



	private SlidingLayer mSlidingLayer;


	public SlidingLayer getSlidingLayer() {
		return mSlidingLayer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSlidingLayer = (SlidingLayer)findViewById(R.id.right_sliding_layer);
		
		mSlidingLayer.setOnInteractListener(this);

		if (mSlidingLayer != null && mSlidingLayer.isOpened()) {
			mSlidingLayer.closeLayer(true);
		}
		
	}
	
	@Override
	public void onOpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		// mSlidingLayer.removeAllViews();
	}

	@Override
	public void onOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClosed() {
		// TODO Auto-generated method stub
		mSlidingLayer.removeAllViews();
	}

}
