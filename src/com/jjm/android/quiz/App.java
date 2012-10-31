package com.jjm.android.quiz;

import java.util.HashSet;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jjm.android.quiz.model.Quiz;

@Singleton
public class App {
	public interface OnLoadListener {
		void onLoad();
	}

	private final Application application;
	private int correctSoundId;
	private int incorrectSoundId;
	private SoundPool soundPool;
	private int quizLength = 20;
	private Quiz quiz;
	private String[] categoryNames;
	private boolean xmlLoaded = false;
	private boolean soundsLoaded = false;

	private Handler handler = new Handler();
	private HashSet<OnLoadListener> onLoadListeners = new HashSet<App.OnLoadListener>();

	private void checkStatus() {
		if (isLoaded()) {
			for (OnLoadListener l : onLoadListeners) {
				l.onLoad();
			}
			onLoadListeners.clear();
		}
	}

	@Inject
	private App(final Application application) {
		this.application = application;
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				soundsLoaded = true;
				checkStatus();
			}
		});
		correctSoundId = soundPool.load(application, R.raw.correct, 1);
		incorrectSoundId = soundPool.load(application, R.raw.incorrect, 1);

		new Thread(new Runnable() {
			public void run() {
				quiz = new Quiz(getApplication());
				quiz.loadCategories();
				handler.post(new Runnable() {
					public void run() {
						xmlLoaded = true;
						checkStatus();
					}
				});
			}
		}).start();
	}

	public boolean isLoaded() {
		return soundsLoaded && xmlLoaded;
	}

	public void addOnLoadListener(OnLoadListener onLoadListener) {
		if (!isLoaded()) {
			onLoadListeners.add(onLoadListener);
		}
	}

	public void playSound(boolean correct) {
		if (!PreferenceManager.getDefaultSharedPreferences(application)
				.getBoolean("sound", true)) 
			return;
		int soundId = correct ? correctSoundId : incorrectSoundId;
		AudioManager audioManager = (AudioManager) application
				.getSystemService(Context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		soundPool.play(soundId, volume, volume, 1, 0, 1f);
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public Application getApplication() {
		return application;
	}

	public String[] getCategoryNames() {
		if (categoryNames == null) {
			categoryNames = new String[getQuiz().getCategories().size()];
			for (int i = categoryNames.length - 1; i >= 0; --i) {
				categoryNames[i] = getQuiz().getCategories().get(i).getName();
			}
		}
		return categoryNames;
	}

	public int getQuizLength() {
		return quizLength;
	}
}
