package com.example.photogallery;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class ThumbnailDownloader<Token> extends HandlerThread{
	private static final String TAG="ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;
	Handler mHandler;
	Map<Token, String> requestMap=Collections.synchronizedMap(new HashMap<Token,String>());
	Handler mResponseHandler;
	Listener<Token> mListener;
	public interface Listener<Token>{
		void onThumbnailDownloader(Token token,Bitmap thumbnail);
	}
	public void setListener(Listener<Token> listener)
	{
		mListener=listener;
	}
 	public ThumbnailDownloader (Handler responseHandler)
	{
		super(TAG);
		mResponseHandler = responseHandler;
	}
	
	@Override
	protected void onLooperPrepared() {
		// TODO Auto-generated method stub
		super.onLooperPrepared();
		mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == MESSAGE_DOWNLOAD) {
					Token token = (Token)msg.obj;
					handleRequest(token);
					
				}
			}
			
		};
	}

	public void queueThumbnail(Token token,String url)
	{
		Log.i(TAG, url);
		requestMap.put(token, url);
		mHandler.obtainMessage(MESSAGE_DOWNLOAD,token).sendToTarget();
	}
	public void clearQueue()
	{
		mHandler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}
	
	private void handleRequest(final Token token)
	{
		try {
			final String url=requestMap.get(token);
			if (url == null) {
				return ;
			}
			byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
			final Bitmap bitmap= BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			mResponseHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (requestMap.get(token)!=url) {
						
					}
					requestMap.remove(token);
					mListener.onThumbnailDownloader(token, bitmap);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "error image",e);
		}
	}
}
