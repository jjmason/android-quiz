package com.jjm.android.quiz.loader;


@SuppressWarnings("serial")
public class QuizLoaderException extends Exception {

	public QuizLoaderException() { 
		super();
	}

	public QuizLoaderException(String message, Throwable cause) {
		super(message, cause); 
	}

	public QuizLoaderException(String detailMessage) {
		super(detailMessage); 
	}

	public QuizLoaderException(Throwable cause) {
		super(cause); 
	}

}
