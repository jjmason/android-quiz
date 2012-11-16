package com.jjm.android.quiz.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * This class receives notifications when an object is
 * garbage collected.  The primary limitation is that it
 * must be "bumped" periodically to learn of the reference's
 * demise.  
 */
public class ReferenceObserver<R> {
	public interface Callback {
		public void referenceCollected();
	}
	
	private class Entry extends WeakReference<R>{
		public boolean cancelled;
		public Callback callback;
		
		public Entry(R ref, Callback cb){
			super(ref, mReferenceQueue);
			callback = cb;
		}
	}
	
	// poll this queue to pick up dead refs
	private ReferenceQueue<R> mReferenceQueue = 
			new ReferenceQueue<R>();
	
	// we keep a weak map from observed objects to 
	// callback holders so that we can use the observed
	// object to cancel callbacks.
	private WeakHashMap<R, Entry> mMap = 
			new WeakHashMap<R,Entry>();
	
	public void observe(R ref, Callback callback){
		bump();
		mMap.put(ref, new Entry(ref, callback));
	}
	
	public void unobserve(R ref){
		bump();
		mMap.remove(ref);
	}
	
	public void bump(){
		// force map to do it's own "bump"
		mMap.entrySet();
		Entry e;
		while((e = (Entry)mReferenceQueue.poll()) != null){
			mMap.remove(e.get());
			e.callback.referenceCollected();
		}
	}
}
