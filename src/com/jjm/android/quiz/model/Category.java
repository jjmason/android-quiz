package com.jjm.android.quiz.model;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Category {
	private static final String TAG = "Category";

	private final Context context;
	
	private String name;
	private String fileName;
	private String iconFileName;
	private Drawable icon;
	private String description;
	private List<Question> questions;

	/**
	 * Get the questions, loading them if necessary.
	 * @return the questions
	 */
	public List<Question> getQuestions() {
		if(questions == null){
			loadQuestions();
		}
		return questions;
	}
	
	/** 
	 * The name to be displayed to the user.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * A short description of the category
	 * @return the description or null if none was provided
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * An icon representing this category
	 * @return the icon or null if none was provided
	 */
	public Drawable getIcon(){
		if(icon == null){
			loadIcon();
		}
		return icon;
	}
	
	/**
	 * <p>Load the icon from the disk if necessary.</p>
	 * <p>May be called on a background thread.</p>
	 */
	public void loadIcon(){
		if(icon == null && iconFileName != null){
			try {
				icon = Drawable.createFromStream(
						context.getAssets().open(iconFileName), iconFileName);
			} catch (IOException e) {
				Log.e(TAG, "Unable to read icon from " + iconFileName, e);
			}
			
		}
	}
	
	/**
	 * <p>Load the questions from the disk if necessary.</p>
	 * <p>May be called from a background thread.</p>
	 */
	public void loadQuestions(){
		if(questions == null && isValid()){
			questions = QuizXml.loadQuestions(context, fileName);
		}
	}
	
	/**
	 * Have all the required fields been set?
	 * @return whether I am valid
	 */
	public boolean isValid(){
		return fileName != null && name != null;
	}
	
	//////////////////////////////
	// Package private constructors
	//////////////////////////////
	Category(Context ctx) {
		context = ctx;
	}
	
	/////////////////////////////
	// Package private setters 
	/////////////////////////////
	void setName(String name) {
		this.name = name;
	}
	void setFileName(String fileName) {
		this.fileName = fileName;
	}
	void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}
	void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Category [name=" + name + ", description=" + description
				+ ", questions=" + questions + "]";
	} 
	
}
