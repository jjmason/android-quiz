package com.jjm.android.quiz.loader;

import java.io.IOException;
import java.io.InputStream;

import com.jjm.android.quiz.model.Quiz;

import android.content.res.AssetManager;

public abstract class QuizLoader {
	protected abstract Quiz internalLoadQuiz(InputStream input) throws Exception;
	public final Quiz loadQuiz(InputStream inputStream) throws QuizLoaderException {
		try{
			return internalLoadQuiz(inputStream);
		}catch(Exception e){
			throw new QuizLoaderException(e);
		}
	}
	public final Quiz loadQuiz(AssetManager assets, String name) throws QuizLoaderException{
		try{
			InputStream in = assets.open(name);
			return loadQuiz(in);
		}catch(IOException e){
			throw new QuizLoaderException(e);
		}
	}
}
