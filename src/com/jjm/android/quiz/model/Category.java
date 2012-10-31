package com.jjm.android.quiz.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private String name;
	private String description;
	private List<Question> questions = 
			new ArrayList<Question>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	@Override
	public String toString() {
		return "Category [name=" + name + ", description=" + description
				+ ", questions=" + questions + "]";
	} 
	
}
