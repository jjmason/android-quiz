package com.jjm.android.quiz.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jjm.android.quiz.Config;
import com.jjm.android.quiz.R;

import roboguice.util.RoboAsyncTask;
import roboguice.util.SafeAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Xml;
import android.util.Xml.Encoding;

public class XmlLoader { 
	public interface XmlLoaderListener {
		void onComplete();
		boolean onError(String message, Throwable error);
		void onProgress(int current, int max);
	}
	
	private static class CategoryInfo {
		public long id;
		public String title;
		public String text;
		public String icon;
		public int mode;
	}
	
	private static class QuestionInfo {
		public String text;
		public String audio;
		public String image;
		public ArrayList<String> choices = 
				new ArrayList<String>();
		public int answer;
	}
	
	@SuppressWarnings("unused")
	private static final String TAG = "QuizXml";
	private XmlLoaderListener listener;
	private final Context context;
	private final DataSource dataSource;   
	private String fileName;
	
	public XmlLoader(Context context){
		this.context = context;
		this.dataSource = new DataSource(context);
	}
	
	public void setXmlLoaderListener(XmlLoaderListener listener){
		this.listener = listener;
	}
	
	/**
	 * This is called from the UI thread and runs 
	 * the load operation on a background thread.
	 */
	public void load(String fileName){
		this.fileName = fileName;
		new Task().execute();
	}
	 
	
	private class XmlHandler extends DefaultHandler {
		private StringBuilder chars = new StringBuilder();
		private CategoryInfo category;
		private QuestionInfo question;
		private int maxProgress = -1;
		private int currentProgress = 0;
		private Poster poster;
		
		public XmlHandler(Poster poster){
			this.poster = poster;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			chars.delete(0, chars.length()); // don't want text lying around between elements
			if("category".equals(localName)){
				category = new CategoryInfo();
				category.id = dataSource.addCategory();
			}else if("question".equals(localName)){
				question = new QuestionInfo();
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			chars.append(ch, start, length);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if("progress".equals(localName)){
				currentProgress = 0;
				maxProgress = Integer.valueOf(getChars());
				poster.postProgress(currentProgress, maxProgress);
			}else if("category".equals(localName)){
				dataSource.updateCategory(category.id, 
						category.title, category.text, 
						category.icon, category.mode);
				category = null;
			}else if("question".equals(localName)){
				dataSource.addQuestion(category.id, question.text, question.image, 
						question.audio, question.choices, question.answer);
				question = null;
				poster.postProgress(++currentProgress, maxProgress);
			}else if("title".equals(localName)){
				category.title = getChars();
			}else if("icon".equals(localName)){
				category.icon = getChars();
			}else if("mode".equals(localName)){
				String s = getChars();
				if("flashcard".equals(s)){
					category.mode = Config.MODE_FLASHCARD;
				}else if("quiz".equals(s)){
					category.mode = Config.MODE_MULTIPLE_CHOICE;
				}else{
					throw new SAXException("invalid mode: " + s);
				}
			}else if("text".equals(localName)){
				if(question != null){
					question.text = getChars();
				}else{
					category.text = getChars();
				}
			}else if("image".equals(localName)){
				question.image = getChars();
			}else if("audio".equals(localName)){
				question.audio = getChars();
			}else if("choice".equals(localName)){
				question.choices.add(getChars());
			}else if("answer".equals(localName)){
				question.answer = Integer.valueOf(getChars());
			}
		}
		
		private String getChars(){
			String ch = chars.toString().trim();
			chars.delete(0, chars.length());
			return ch;
		}
	}
	
	private interface Poster {
		void postProgress(int current, int total);
		void postError(String message, Throwable ex);
	}
	
	private class Task extends SafeAsyncTask<Void> implements Poster { 
		
		public Task(){
			super(new Handler());
		}
		
		@Override
		public Void call() throws Exception {
			InputStream in = context.getAssets().open(fileName);
			XmlHandler handler = new XmlHandler(this);
			Xml.parse(in, Encoding.UTF_8, handler);
			return null;
		}
		
		@Override
		protected void onFinally() throws RuntimeException {
			if(listener != null)
				listener.onComplete();
		}
		
		@Override
		protected void onException(Exception e) throws RuntimeException {
			if(listener != null)
				listener.onError("Error loading xml", e);
			
		}
		
		@Override
		public void postError(final String message, final Throwable error){
			handler().post(new Runnable() {
				@Override
				public void run() {
					if(listener != null)
						listener.onError(message, error);
				}
			});
		}
		
		@Override
		public void postProgress(final int progress,final int total){
			handler().post(new Runnable() {
				@Override
				public void run() {
					if(listener != null)
						listener.onProgress(progress, total);
				}
			});
		}
	} 
}
