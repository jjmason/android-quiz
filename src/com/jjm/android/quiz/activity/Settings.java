package com.jjm.android.quiz.activity;
import static com.jjm.android.quiz.util.Util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import roboguice.activity.RoboPreferenceActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.jjm.android.quiz.R;
import com.jjm.android.quiz.util.Util;

public class Settings extends RoboPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		findPreference(getText(R.string.key_export))
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						exportXML();
						return true;
					}
				});
	}

	
	private void exportXML() {
		File sharedPrefs = getSharedPrefsFile(this, "config.xml");
		if (!sharedPrefs.exists()) {
			Util.errorAlert(this, "file does not exist " + sharedPrefs.getAbsolutePath());
			return;
		}
		String text = null;
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
					new FileInputStream(sharedPrefs)));
			StringBuilder buffer = new StringBuilder();
			char[] ch = new char[4096];
			while(true){
				int amount = reader.read(ch);
				if(amount < 0)
					break;
				buffer.append(ch, 0 ,amount);
			}
			reader.close();
			text = buffer.toString();
		} catch (IOException e) {
			Log.e("exportXML",e.getMessage(),e);
			Util.errorAlert(this, "error reading xml:"+e);
			return;
		}
		Intent i = new Intent(Intent.ACTION_SEND)
			.putExtra(Intent.EXTRA_TEXT, text)
			.setType("application/xml");
		startActivity(Intent.createChooser(i, "Export XML"));
	}


}
