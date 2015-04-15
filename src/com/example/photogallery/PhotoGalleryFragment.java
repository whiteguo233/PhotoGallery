package com.example.photogallery;

import java.util.ArrayList;

import android.R.anim;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoGalleryFragment extends Fragment {
	private static final String TAG="PhotoGalleryFragment";
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDownloader<ImageView> mThumbnailDownloader;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		new FetchItemsTask().execute(1);
		mThumbnailDownloader = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailDownloader.setListener(new ThumbnailDownloader.Listener<ImageView>() {

			@Override
			public void onThumbnailDownloader(ImageView token, Bitmap thumbnail) {
				// TODO Auto-generated method stub
				if (isVisible()) {
					token.setImageBitmap(thumbnail);
				}
			}
			
		});
		mThumbnailDownloader.start();
		mThumbnailDownloader.getLooper();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_photo_gallery, container,false);
		mGridView = (GridView) view.findViewById(R.id.gridView);
		setupAdapter();
		return view;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mThumbnailDownloader.quit();
	}

	void setupAdapter()
	{
		if (getActivity() == null || mGridView == null) {
			return;
		}
		if (mItems !=null) {
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
		}else {
			mGridView.setAdapter(null);
		}
	}
	private class FetchItemsTask extends AsyncTask<Integer, Void, ArrayList<GalleryItem>>{

		@Override
		protected ArrayList<GalleryItem> doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			return new FlickrFetchr().fetchItems(params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<GalleryItem> result) {
			// TODO Auto-generated method stub
			mItems=result;
			setupAdapter();
		}
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mThumbnailDownloader.clearQueue();
	}
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem>
	{

		public GalleryItemAdapter(ArrayList<GalleryItem> items) {
			super(getActivity(),0, items);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView==null) {
				convertView=getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent,false);
			}
			ImageView imageView=(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
			imageView.setImageResource(R.drawable.brian_up_close);
			GalleryItem item = getItem(position);
			mThumbnailDownloader.queueThumbnail(imageView, item.getUrl());
			if (position == getCount()-1) {
				new FetchItemsTask().execute(2);
			}
			return convertView;
			}

	}
	
}
