package com.jjm.android.quiz.model;

import java.util.List;

import android.content.Context;

public class Quiz {
	private final Context context;
	private final String fileName;
	
	private List<Category> categories;

	public Quiz(Context context){
		this(context, "categories.xml");
	}
	
	public Quiz(Context context, String assetFilename) {
		this.context = context;
		this.fileName = assetFilename;
	}

	public void loadCategories(){
		if(categories == null){
			categories = QuizXml.loadCategories(context, fileName);
		}
	}
	
	public List<Category> getCategories() {
		loadCategories();
		return categories;
	}
	

	@Override
	public String toString() {
		return "Quiz [categories=" + categories + "]";
	}
	
}
