package com.jjm.android.quiz;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressLint("WorldReadableFiles")
@Singleton
public class Config {  
	
	public static final int MODE_MULTIPLE_CHOICE = 0;
	public static final int MODE_FLASHCARD = 1;
	public static final int MODE_VARIABLE = 2;

	public static final int NAVIGATION_BUTTONS = 0x01;
	public static final int NAVIGATION_SWIPE = 0x02;
	public static final int NAVIGATION_BOTH = NAVIGATION_BUTTONS
			| NAVIGATION_SWIPE;

	public static final int LAYOUT_FIXED_BUTTONS = 1;
	public static final int LAYOUT_SCROLL_BUTTONS = 2; 

	public static final int ANIMATION_NONE = 0;
	public static final int ANIMATION_SLIDE= 1;
	public static final int ANIMATION_FLIP = 2;

	private final Context mContext;
	
	@Inject 
	public Config(Context context){
		mContext = context;
		setDefaultPrefs();
	}
	
	private void setDefaultPrefs(){
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		long prefsTime = prefs.getLong("__mtime", 0);
		long apkTime = new File(mContext.getPackageCodePath()).lastModified();
		if(apkTime <= prefsTime){
			return;
		}
		
		Editor editor = prefs.edit();
		String key = getString(R.string.key_soundEnabled);
		editor.putBoolean(key, getBool(R.bool.config_defaultSoundEnabled));
		
		key = getString(R.string.key_quizLength);
		editor.putString(key, Integer.toString(getInt(R.integer.config_defaultNumQuestions)));
		
		key = getString(R.string.key_autoNextQuestion);
		editor.putBoolean(key, getBool(R.bool.config_autoNext));
		
		editor.putLong("__mtime", apkTime);
		
		editor.commit();
	}
	
	public String questionsFile(){
		return getString(R.string.config_questionsFile);
	}

	
	
	public boolean highScores() {
		return getBool(R.bool.config_highScores);
	}

	public boolean flashcardMode(){
		return getBool(R.bool.config_flashcardMode);
	}
	
	public boolean fixedLayout(){
		return getBool(R.bool.config_fixedLayout);
	}
	
	public boolean autoNext(){
		return getBool(R.bool.config_autoNext);
	}
	
	public int autoNextDelay(){
		return getInt(R.integer.config_autoNextDelay);
	}

	public int fontSize(){
		return getInt(R.integer.config_fontSize);
	}
	
	public boolean soundEnabled(){
		return getPrefs().getBoolean(getString(R.string.key_soundEnabled), true);
	}
	
	public int numQuestions(){
		String str = getPrefs().getString(getString(R.string.key_quizLength), "15");
		return Integer.valueOf(str);
	}
	
	private String getString(int resId){
		return mContext.getString(resId);
	}
	
	private boolean getBool(int resId){
		return mContext.getResources().getBoolean(resId);
	}
	
	private int getInt(int resId){
		return mContext.getResources().getInteger(resId);
	}
	
	private SharedPreferences getPrefs(){
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
}
