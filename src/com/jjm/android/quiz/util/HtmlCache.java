package com.jjm.android.quiz.util;

import java.util.HashMap;

import android.text.Html;
import android.text.Spanned;

public class HtmlCache {
	private final HashMap<String, Spanned> mCache = 
			new HashMap<String, Spanned>();
	
	public Spanned getHtml(String rawHtml){
		if(rawHtml == null)
			return null;
		
		Spanned html = mCache.get(rawHtml);
		if(html == null){
			mCache.put(rawHtml, html = Html.fromHtml(rawHtml));
		}
		return html;
	}

}
