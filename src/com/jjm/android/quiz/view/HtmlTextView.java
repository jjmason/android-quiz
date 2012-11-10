package com.jjm.android.quiz.view;

import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class HtmlTextView extends TextView {
	// we remember the last value passed to set text
	// to avoid multiple calls to Html.fromHtml
	private final HashMap<String, Spanned> mHtmlCache = 
			new HashMap<String, Spanned>();
	
	public HtmlTextView(Context context) {
		super(context); 
	}

	public HtmlTextView(Context context, AttributeSet attrs) {
		super(context, attrs); 
	}

	public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		if(text instanceof String){
			String textString = (String) text;
			Spanned cached = mHtmlCache.get(textString);
			if(cached == null){
				cached = Html.fromHtml(textString, mImageGetter, null);
				mHtmlCache.put(textString, cached);
			}
			setLinksClickable(true);
			text = cached;
		}
		super.setText(text, type);
	}
	
	private final ImageGetter mImageGetter = new ImageGetter() {
		
		@Override
		public Drawable getDrawable(String source) {
			try{
				InputStream in = getContext().getAssets().open(source);
				Drawable d = Drawable.createFromStream(in, source);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}catch(Exception e){
				Log.e("HtmlTextView$ImageGetter",
						"error loading image: "+source,e);
				return null;
			}
		}
	}; 
}
