package com.jjm.android.quiz.model;

import java.util.ArrayList;
import java.util.List;

public class Question {
	private String text;
	private List<String> choices = 
			new ArrayList<String>();
	private int answer;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getChoices() {
		return choices;
	}
	public void setChoices(List<String> choices) {
		this.choices = choices;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	@Override
	public String toString() {
		return "Question [text=" + text + ", choices=" + choices + ", answer="
				+ answer + "]";
	}
	
}
