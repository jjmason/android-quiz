package com.jjm.android.quiz.model;

import java.util.ArrayList;
import java.util.List;

public class Question {
	private String text;
	private List<String> choices = 
			new ArrayList<String>();
	
	private int answer;
	
	/**
	 * The text of the question
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Choices to present to the user.
	 * @return the choices
	 */
	public List<String> getChoices() {
		return choices;
	}
	
	/**
	 * The index of the correct answer in {@link #getChoices()}.
	 * @return the index
	 */
	public int getAnswer() {
		return answer;
	}
	
	/**
	 * Whether this question is valid
	 * @return the validity in question
	 */
	public boolean isValid(){
		return answer >= 0 && answer < choices.size() 
				&& !choices.isEmpty() && text != null;
	}
	
	////////////////////////////
	// Package private setters
	////////////////////////////
	void setAnswer(int answer) {
		this.answer = answer;
	}
	
	void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "Question [text=" + text + ", choices=" + choices + ", answer="
				+ answer + "]";
	}
	
}
