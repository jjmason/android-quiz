package com.jjm.android.quiz.model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
	
	private List<Category> categories = new ArrayList<Category>();

	public Quiz(String assetFilename) {
		
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "Quiz [categories=" + categories + "]";
	}
	
}
