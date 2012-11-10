package com.jjm.android.quiz.util;

/**
 * Argument check helpers
 */
public final class Arguments {
	private Arguments(){}
	
	public static <T> T checkNotNull(T obj, String name){
		return checkThat(obj != null, obj, "%s must not be null", name);
	}
	
	public static <T> T checkNotNull(T obj){
		return checkNotNull(obj, "argument");
	}
	
	public static <T> T checkThat(boolean condition, T argument, String message, Object...messageArgs){
		return checkThat(condition, argument, createThrowable(message, messageArgs));
	}
	
	public static <T> T checkThat(boolean condition, T argument, RuntimeException err){
		if(!condition) 
			throw err;
		return argument;
	}
	
	private static RuntimeException createThrowable(String format, Object...args){
		if(format == null){
			format = "Illegal Argument";
			args = new Object[0];
		}
		String message = String.format(format, args);
		return new IllegalArgumentException(message);
	}
}
