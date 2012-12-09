package com.jjm.android.quiz.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * Facade class to play sounds using a {@link MediaPlayer} (assets) or
 * {@link SoundPool} (resources)
 */
public class SoundPoolAssistant {
	private static final String TAG = "SoundPoolAssistant";

	/**
	 * Passed to ...
	 */
	public static enum VolumeMode {
		/**
		 * Use the system volume settings.
		 */
		SYSTEM,
		/**
		 * Use the volume passed to {@link SoundPoolAssistant#setVolume(float)}.
		 */
		FIXED,
		/**
		 * Don't play any sounds.
		 */
		MUTE;
	};

	private final SoundPool mSoundPool;
	private final Context mContext;
	private VolumeMode mVolumeMode = VolumeMode.SYSTEM;
	private float mVolume;

	private final Map<String, Integer> mPathToSoundId = new LinkedHashMap<String, Integer>();
	private final SparseIntArray mResIdToSoundId = new SparseIntArray();

	public void setVolumeMode(VolumeMode mode) {
		mVolumeMode = mode;
	}

	public void setVolume(float volume) {
		if (volume < 0 || volume > 1)
			throw new IllegalArgumentException("volume must be >= 0 and <= 1");
		mVolume = volume;
	}

	public SoundPoolAssistant(Context context, int maxStreams, int streamType) {
		mContext = context;
		mSoundPool = new SoundPool(maxStreams, streamType, 0);
	}

	/**
	 * 
	 * @param assetFileName
	 * @param reference
	 */
	public void load(String path) throws IOException {
		AssetFileDescriptor afd = mContext.getAssets().openFd(path);
		mPathToSoundId.put(path, mSoundPool.load(afd, 0));
	}

	/**
	 * 
	 * @param resourceId
	 * @param reference
	 */
	public void load(int resourceId) {
		mResIdToSoundId.put(resourceId,
				mSoundPool.load(mContext, resourceId, 0));
	}

	public void play(int resId) {
		Integer soundId = mResIdToSoundId.get(resId);
		if (soundId == null) {
			Log.w(TAG, "sound not loaded for resource id " + resId);
		}else{
			playSoundId(soundId);
		}
	}

	public void play(String path) {
		Integer soundId = mPathToSoundId.get(path);
		if(soundId == null){
			Log.w(TAG, "sound not loaded for path " + path);
		}else{
			playSoundId(soundId);
		}
	}
	
	public boolean isSoundEnabled(){
		return getActualVolume() > 0;
	}
	
	private void playSoundId(int soundId) {
		float vol = getActualVolume();
		if( vol != 0 ){
			mSoundPool.play(soundId, vol, vol, 0, 0, 1);
		}
	}
	
	private float getActualVolume(){
		if(mVolumeMode == VolumeMode.MUTE)
			return 0;
		if(mVolumeMode == VolumeMode.FIXED && mVolume >= 0 && mVolume <= 1)
			return mVolume;
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		return (float) am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) /
				(float) am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
}
