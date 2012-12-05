package com.jjm.android.quiz.model;

import java.util.ArrayList;

import jm.util.SimpleSaxParser;
import jm.util.SimpleSaxParser.EndTagHandler;
import jm.util.SimpleSaxParser.StartTagHandler;
import jm.util.XmlException;

import org.xml.sax.Attributes;

import android.content.Context;

public class XmlLoader2 {
	private static class QuestionInfo {
		 String text;
		 ArrayList<String> choices = new ArrayList<String>();
		 String audio;
		 String image;
		 int answer = -1;
	}
	
	private static class CategoryInfo {
		long id;
		String title;
		String text;
		String icon;
		int mode;
		int layout;
	}
	
	private QuestionInfo mQuestion;
	private CategoryInfo mCategory;
	
	private SimpleSaxParser mParser = new SimpleSaxParser();
	private DataSource mDataSource;
	
	public XmlLoader2(Context context){
		initHandlers();
		mDataSource = new DataSource(context);
	}
	
	private void initHandlers(){
		mParser.setStartTagHandler("question", new StartTagHandler() {
			@Override
			public void handleStartTag(SimpleSaxParser parser, String name,
					Attributes attrs) throws XmlException {
				if(mQuestion != null){
					throw new XmlException("nested question tags?");
				}
				mQuestion = new QuestionInfo();
			}
		});
		
		mParser.setEndTagHandler("question", new EndTagHandler() {
			@Override
			public void handleEndTag(SimpleSaxParser parser, String name)
					throws XmlException {
				validateQuestion();
				mDataSource.addQuestion(mCategory.id, mQuestion.text,
						mQuestion.image, 
						mQuestion.audio, 
						mQuestion.choices, 
						mQuestion.answer);
			}
		});
		
		mParser.setStartTagHandler("category", new StartTagHandler() {
			
			@Override
			public void handleStartTag(SimpleSaxParser parser, String name,
					Attributes attrs) throws XmlException {
				// TODO Auto-generated method stub
				
			}
		});
		
		mParser.setEndTagHandler("category", new EndTagHandler() {
			
			@Override
			public void handleEndTag(SimpleSaxParser parser, String name)
					throws XmlException {
				// TODO Auto-generated method stub
				
			}
		});
		
		mParser.setEndTagHandler("answer", new EndTagHandler() {
			
			@Override
			public void handleEndTag(SimpleSaxParser parser, String name)
					throws XmlException {
				// TODO set answer
			}
		});
		
		// TODO other properties
	}
	
	private void validateQuestion() throws XmlException {
		if(mQuestion.text == null && mQuestion.audio == null && mQuestion.image == null){
			throw new XmlException("question must have at least one of text, audio or image children!");
		}
		if(mQuestion.choices.size() == 0){
			throw new XmlException("question must have at least one choice");
		}
		if(mQuestion.answer >= mQuestion.choices.size() || mQuestion.answer < 0)
			throw new XmlException("answer index out of bounds");
	}
}
