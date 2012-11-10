package com.jjm.android.quiz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.jjm.android.quiz.R;

/**
 * This button has two extra pieces of drawable state: correct: is this button
 * the one for the correct answer? showingAnswer: are we showing the answer?
 */
public class QuizButton extends Button {
	private static final int[] STATE_SHOWING_ANSWER = { R.attr.state_showing_answer };
	private static final int[] STATE_CORRECT = { R.attr.state_correct };

	private boolean mCorrect;
	private boolean mShowingAnswer;

	public QuizButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.QuizButton, defStyle, 0);
		mCorrect = a.getBoolean(R.styleable.QuizButton_correct, true);
		mShowingAnswer = a.getBoolean(R.styleable.QuizButton_showing_answer, false);
	}

	public QuizButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.style.button);
	}

	public QuizButton(Context context) {
		this(context, null);
	}

	public boolean isCorrect() {
		return mCorrect;
	}

	public void setCorrect(boolean correct) {
		if (correct != this.mCorrect) {
			this.mCorrect = correct;
			refreshDrawableState();
		}
	}

	public boolean isShowingAnswer() {
		return mShowingAnswer;
	}

	public void setShowingAnswer(boolean showingAnswer) {
		if (showingAnswer != this.mShowingAnswer) {
			this.mShowingAnswer = showingAnswer;
			refreshDrawableState();
		}
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
		if (isCorrect()) {
			mergeDrawableStates(drawableState, STATE_CORRECT);
		}
		if (isShowingAnswer()) {
			mergeDrawableStates(drawableState, STATE_SHOWING_ANSWER);
		}
		return drawableState;
	}

}
