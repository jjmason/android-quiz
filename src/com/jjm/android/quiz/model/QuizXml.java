package com.jjm.android.quiz.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

public class QuizXml {
	private static final String TAG = "QuizXml";

	public static List<Category> loadCategories(Context context,
			String assetFileName) {
		try {
			InputStream in = context.getAssets().open(assetFileName);
			CategoriesHandler handler = new CategoriesHandler(context);
			Xml.parse(in, Encoding.UTF_8, handler);
			return handler.getCategories();
		} catch (Exception e) {
			// TODO: Just report it?
			throw new RuntimeException(e);
		}
	}

	public static List<Question> loadQuestions(Context context, 
			String assetFileName){
		try{
			InputStream in = context.getAssets().open(assetFileName);
			QuestionsHandler handler = new QuestionsHandler();
			Xml.parse(in, Encoding.UTF_8, handler);
			return handler.getQuestions();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	// ///////////////////////////
	// Handler for character data
	// ////////////////////////////
	private static class CharsHandler extends DefaultHandler {
		private StringBuilder buffer = new StringBuilder();

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			buffer.append(ch, start, length);
		}

		protected String getBuffer() {
			String s = buffer.toString().trim();
			buffer.delete(0, buffer.length());
			return s;
		}
	}

	// /////////////////////////
	// Handler for categories
	// /////////////////////////
	private static class CategoriesHandler extends CharsHandler {

		private final Context context;
		private ArrayList<Category> categories = new ArrayList<Category>();
		private Category current;

		public CategoriesHandler(Context context) {
			this.context = context;
			current = new Category(context);
		}

		public List<Category> getCategories() {
			return categories;
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if ("category".equals(localName)) {
				if (!current.isValid()) {
					Log.wtf(TAG, "category " + current
							+ " is not valid, skipping it!");
				} else {
					categories.add(current);
				}
				current = new Category(context);
			} else if ("name".equals(localName)) {
				current.setName(getBuffer());
			} else if ("description".equals(localName)) {
				current.setDescription(getBuffer());
			} else if ("icon".equals(localName)) {
				current.setIconFileName(getBuffer());
			} else if ("file".equals(localName)) {
				current.setFileName(getBuffer());
			}
		}
	}
	
	////////////////////////////
	// Handler for questions
	///////////////////////////
	private static class QuestionsHandler extends CharsHandler {
		private List<Question> questions = 
				new ArrayList<Question>();
		private Question current = new Question();
		public List<Question> getQuestions(){
			return questions;
		}
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if("question".equals(localName)){
				if(!current.isValid()){
					Log.wtf(TAG, "question " + current + 
							" is not valid; skipping it!");
				}else{
					questions.add(current);
				}
				current = new Question();
			}else if("text".equals(localName)){
				current.setText(getBuffer());
			}else if("answer".equals(localName)){
				String as = getBuffer();
				try{
					current.setAnswer(Integer.valueOf(as));
				}catch(NumberFormatException e){
					Log.wtf(TAG, "Question " + current + 
							" had invalid answer format '" + 
							as + "'");
					// This will keep it from being included.
					current.setAnswer(-1);
				}
			}else if("choice".equals(localName)){
				current.getChoices().add(getBuffer());
			}
		}
	}
}
