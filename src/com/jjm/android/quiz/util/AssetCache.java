package com.jjm.android.quiz.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class AssetCache {
	private final Context mContext;
	private HashMap<String, Drawable> mDrawables =
			new HashMap<String, Drawable>();
	
	public AssetCache(Context context){
		mContext = context;
	}
	
	public Drawable getDrawable(String path){
		if(path == null)
			return null;
		
		Drawable drawable = mDrawables.get(path);
		if(drawable == null){
			drawable = loadDrawable(path);
			if(drawable != null){
				mDrawables.put(path, drawable);
			}
		}
		return drawable;
	}
	
	private Drawable loadDrawable(String path){
		try{
			InputStream in = mContext.getAssets().open(path);
			return Drawable.createFromStream(in, path);
		}catch(IOException e){
			error("unable to load drawable from " + path + ": " + e);
			return null;
		}
	}
	
	private void error(String error){
		
	}
}
