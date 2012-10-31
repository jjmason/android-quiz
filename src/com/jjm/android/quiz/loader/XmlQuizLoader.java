package com.jjm.android.quiz.loader;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jjm.android.quiz.model.Category;
import com.jjm.android.quiz.model.Question;
import com.jjm.android.quiz.model.Quiz;

import android.util.Xml;
import android.util.Xml.Encoding;

public class XmlQuizLoader extends QuizLoader {
	private Quiz quiz;
	private Category category;
	private Question question;
	private StringBuilder buffer = new StringBuilder();
	private String getBuffer(){
		String s = buffer.toString().trim();
		buffer.delete(0, buffer.length());
		return s;
	}
	private class Handler extends DefaultHandler {
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			buffer.append(ch,start,length);
		}
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(localName.equals("category")){
				quiz.getCategories().add(category);
			}else if(localName.equals("question")){
				category.getQuestions().add(question);
			}else if(localName.equals("name")){
				category.setName(getBuffer());
			}else if(localName.equals("text")){
				question.setText(getBuffer());
			}else if(localName.equals("choice")){
				question.getChoices().add(getBuffer());
			}else if(localName.equals("answer")){
				question.setAnswer(Integer.valueOf(getBuffer()));
			}else{
				getBuffer();
			}
		}
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if(localName.equals("quiz")){
				quiz = new Quiz();
			}else if(localName.equals("category")){
				category = new Category();
			}else if(localName.equals("question")){
				question = new Question();
			}
		}
	}

	@Override
	protected Quiz internalLoadQuiz(InputStream input) throws Exception {
		Xml.parse(input, Encoding.UTF_8, new Handler());
		return quiz;
	}
	
	
}
