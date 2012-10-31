package com.jjm.android.quiz.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.jjm.android.quiz.R;

/**
 * This button has two extra pieces of drawable state:
 * correct: is this button the one for the correct answer?
 * showingAnswer: are we showing the answer?
 */
public class QuizButton extends Button {
	private static final int[] STATE_SHOWING_ANSWER = { R.attr.state_showing_answer };
	private static final int[] STATE_CORRECT = { R.attr.state_correct };

	private boolean correct;
	private boolean showingAnswer;
	
	public QuizButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
	}

	public QuizButton(Context context, AttributeSet attrs) {
		super(context, attrs); 
	}

	public QuizButton(Context context) {
		super(context); 
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		if(correct != this.correct){
			this.correct = correct;
			refreshDrawableState();
		}
	}

	public boolean isShowingAnswer() {
		return showingAnswer;
	}

	public void setShowingAnswer(boolean showingAnswer) {
		if(showingAnswer != this.showingAnswer){
			this.showingAnswer = showingAnswer;
			refreshDrawableState();
		}
	}
	
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
		if(isCorrect()){
			mergeDrawableStates(drawableState, STATE_CORRECT);
		}
		if(isShowingAnswer()){
			mergeDrawableStates(drawableState, STATE_SHOWING_ANSWER);
		}
		return drawableState;
	}
	

}
