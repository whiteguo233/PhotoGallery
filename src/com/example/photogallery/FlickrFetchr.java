package com.example.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.net.Uri;
import android.util.Log;

public class FlickrFetchr {
	public static final String TAG="FlickrFetchr";
	private static final String BAIDUURL="http://image.baidu.com/channel/listjson";
	private static final String PN="1";
	private static final String RN="100";
	private static final String TAG1="Ã÷ÐÇ";
	private static final String TAG2="¸ßÔ²Ô²";
	private static final String IE="utf8";
	byte[] getUrlBytes (String urlSpec) throws IOException{
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer,0,bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally
		{
			connection.disconnect();
		}
	}
	
	public String getUrl(String urlSpec) throws IOException
	{
		return new String(getUrlBytes(urlSpec));
	}
	void parseItems(ArrayList<GalleryItem> items,String jsonString) throws Exception
	{
		try {
			JSONArray jsonObjs = new JSONObject(jsonString).getJSONArray("data");
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));
				String abs = jsonObj.getString("abs");
				String id = jsonObj.getString("id");
				String smallUrl=jsonObj.getString("thumbnail_url");
				GalleryItem item=new GalleryItem();
				item.setId(id);
				item.setUrl(smallUrl);
				item.setCaption(abs);
				items.add(item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, "load json error", e);
		}
	}
	public ArrayList<GalleryItem> fetchItems(Integer pn){
		ArrayList<GalleryItem> items= new ArrayList<GalleryItem>();
		try {
			String url=Uri.parse(BAIDUURL).buildUpon().appendQueryParameter("pn", String.valueOf(pn)).appendQueryParameter("rn", RN).appendQueryParameter("tag1", TAG1).appendQueryParameter("tag2", TAG2).appendQueryParameter("ie", IE).build().toString();
			String jsonString=getUrl(url);
			parseItems(items, jsonString);
			Log.i(TAG, jsonString);
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, "fetch fail",e);
		}
		return items;
	}
}
