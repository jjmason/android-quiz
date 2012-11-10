package com.jjm.android.quiz.util;

public class Task implements Runnable {
	private boolean mCancelled;
	private final Runnable mRunnable;
	
	public Task(Runnable runnable) {
		mRunnable = runnable;
	}

	public void cancel(){
		mCancelled = true;
	}
	
	@Override
	public void run() {
		if(!mCancelled){
			mRunnable.run();
		}
	}

}
