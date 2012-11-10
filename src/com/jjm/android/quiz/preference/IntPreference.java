package com.jjm.android.quiz.preference;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;

public class IntPreference extends EditTextPreference {

	public IntPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		configureEditText();
	}

	public IntPreference(Context context, AttributeSet attrs) {
		super(context, attrs); 
		configureEditText();
	}

	private void configureEditText(){
		getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}
	
	@Override
	protected String getPersistedString(String defaultReturnValue) {
		return String.valueOf(getPersistedInt(-1));
	}
	
	@Override
	protected boolean persistString(String value) {
		persistInt(Integer.valueOf(value));
		return true;
	}
}
