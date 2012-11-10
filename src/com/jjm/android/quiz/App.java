package com.jjm.android.quiz;

import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jjm.android.quiz.util.AssetCache;
import com.jjm.android.quiz.util.HtmlCache;

@Singleton
public class App {

	
	
	@Inject
	private Application mContext;

	private AssetCache mAssetCache;
	
	private HtmlCache mHtmlCache;
	
	private Config mConfig;
	
	public AssetCache getAssetCache(){
		if(mAssetCache == null){
			mAssetCache = new AssetCache(mContext);
		}
		return mAssetCache;
	}
	 
	public HtmlCache getHtmlCache(){
		if(mHtmlCache == null){
			mHtmlCache = new HtmlCache();
		}
		return mHtmlCache;
	}
	
	public Config getConfig(){
		if(mConfig == null){
			mConfig = new Config(mContext);
		}
		return mConfig;
	}
	
	public boolean isAudioAvailable(){
		return false; //TODO
	}

}
