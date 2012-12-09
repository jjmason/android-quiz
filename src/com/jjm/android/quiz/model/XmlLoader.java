package com.jjm.android.quiz.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Xml;

public class XmlLoader {
	public interface XmlLoadedListener {
		void onXmlLoaded();
	}

	private static class CategoryInfo {
		public long id;
		public String title;
		public String text;
		@SuppressWarnings("unused")
		public String icon;
		@SuppressWarnings("unused")
		public int mode;
		@SuppressWarnings("unused")
		public int layout;
	}

	private static class QuestionInfo {
		public String text;
		public String audio;
		public String image;
		public ArrayList<String> choices = new ArrayList<String>();
		public int answer;
	} 

	@SuppressWarnings("unused")
	private static final String TAG = "QuizXml";
	private final Context context;
	private final DataSource dataSource;
	
	public XmlLoader(Context context) {
		this.context = context;
		this.dataSource = new DataSource(context);
	}

	
	public void load(String fileName) throws IOException, SAXException {
		dataSource.reset();
		InputStream in = context.getAssets().open(fileName);
		Xml.parse(in, Xml.Encoding.UTF_8, new XmlHandler()); 
	}
	
	private class XmlHandler extends DefaultHandler {

		
		private StringBuilder chars = new StringBuilder();
		private CategoryInfo category;
		private QuestionInfo question;
		private int questionCount;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// don't catch whitespace between tags
			chars.delete(0, chars.length());
			
			if ("category".equals(localName)) {
				category = new CategoryInfo();
				category.id = dataSource.addCategory();
			} else if ("question".equals(localName)) {
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

			if ("category".equals(localName)) { 
				finishCategory();
				category = null;
			} else if ("question".equals(localName)) {
				finishQuestion(); 
			} else if ("title".equals(localName)) {
				category.title = getChars();
			} else if ("text".equals(localName)) {
				if (question != null) {
					question.text = getChars();
				} else {
					category.text = getChars();
				}
			} else if ("image".equals(localName)) {
				question.image = getChars();
			} else if ("audio".equals(localName)) {
				question.audio = getChars();
			} else if ("choice".equals(localName)) {
				question.choices.add(getChars());
			} else if ("answer".equals(localName)) {
				question.answer = parseAnswer();
			}
		}

		private int parseAnswer() throws SAXException {
			String ch = getChars();
			try{
				return Integer.valueOf(ch);
			}catch(NumberFormatException e){
				throw throwException("invalid answer index " + ch);
			} 
		}
		
		private void finishQuestion() throws SAXException {
			if (question.choices.size() == 0)
				throwException("must have at least one choice");
			if (question.answer < 0
					|| question.answer >= question.choices.size())
				throwException("invalid answer index");
			if (question.text == null && question.image == null
					&& question.audio == null)
				throwException("question must contain at least one of text, image, and audio tags");
			dataSource.addQuestion(category.id, question.text, question.image,
					question.audio, question.choices, question.answer);
			question = null;
			questionCount ++;
		}

		private void finishCategory() throws SAXException {
			if (category.title == null)
				throwException("missing category title");
			dataSource.updateCategory(category.id, category.title,
					category.text, "", 0);
			category = null;
		}

		private SAXException throwException(String message) throws SAXException {
			throw new SAXException("error while parsing question "
					+ questionCount + " in category "
					+ (category == null ? "??" : category.id) + ": " + message);
		}

		private String getChars() {
			String ch = chars.toString().trim();
			chars.delete(0, chars.length());
			return ch;
		}
	}

}
