package com.jjm.android.quiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Preconditions;

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
	
	public boolean getShowIcons() {
		return getBoolean(R.string.key_showIcons, false);
	}

	public boolean getShowHighScores() {
		return getBoolean(R.string.key_showHighScores, false);
	}

	public boolean getAllowSkipQuestions() {
		return getBoolean(R.string.key_allowSkipQuestions, false);
	}

	public int getQuizLength() {
		return getInt(R.string.key_quizLength, 10);
	}

	public boolean getSoundEnabled() {
		return getBoolean(R.string.key_soundEnabled, true);
	} 
	public int getQuestionFontSize() {
		return getInt(R.string.key_questionFontSize, 14);
	}

	public boolean getAutoNextQuestion() {
		return getBoolean(R.string.key_autoNextQuestion, false);
	}

	public long getAutoNextQuestionDelay() {
		return getLong(R.string.key_autoNextQuestionDelay, 1500);
	}

	public int getQuizMode() {
		return getInt(R.string.key_mode, MODE_VARIABLE);
	}

	public int getNavigationStyle() {
		return getInt(R.string.key_navigation, NAVIGATION_BOTH);
	}
	
	public boolean hasNavigationStyle(int style){
		return 0 != (getNavigationStyle() & style);
	}

	public int getQuestionLayout() {
		return getInt(R.string.key_questionLayout, LAYOUT_FIXED_BUTTONS);
	} 

	public int getQuestionAnimationType(){
		return getInt(R.string.key_questionAnimationType, ANIMATION_SLIDE);
	}
	
	public int getFlashcardAnimationType(){
		return getInt(R.string.key_flashcardAnimationType, ANIMATION_FLIP);
	}
	 
	private SharedPreferences mSharedPreferences; 
	private final Context mContext;
 
	@Inject
	public Config(Context context) {
		mContext = Preconditions.checkNotNull(context);
	}

	/*
	 * Generic accessors
	 */ 

	private String getKey(int keyResId){
		return mContext.getString(keyResId);
	}
	
	private String getString(int key, String defValue){
		return getSharedPreferences().getString(getKey(key), defValue);
	}
	
	private boolean getBoolean(int key, boolean defValue) {
		return getSharedPreferences().getBoolean(getKey(key), defValue);
	}

	private int getInt(int key, int defValue) {
		String sval = getString(key, null);
		if(sval == null){
			return defValue;
		}
		try{
			return Integer.valueOf(sval);
		}catch(NumberFormatException e){
			Log.wtf("Config", "invalid int preference:" + sval);
			return defValue;
		}
	}

	
	private long getLong(int key, long defValue) {
		String sval = getString(key, null);
		if(sval == null){
			return defValue;
		}
		try{
			return Long.valueOf(sval);
		}catch(NumberFormatException e){
			Log.wtf("Config", "invalid int preference:" + sval);
			return defValue;
		}
	}

	private SharedPreferences getSharedPreferences() {
		if (mSharedPreferences == null) {
			mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		}
		return mSharedPreferences;
	}
}
