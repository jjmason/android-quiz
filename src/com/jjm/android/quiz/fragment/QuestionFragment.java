package com.jjm.android.quiz.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.R;
import com.jjm.android.quiz.model.Question;

public abstract class QuestionFragment extends RoboFragment {
	/**
	 * Must be implemented by the parent activity.
	 */
	public interface QuestionListener {
		void onNextQuestion(); 
		void onQuestionAnswered(int answer);
	}
 
	public static int NO_ANSWER = -1;
	public static final String QUESTION_KEY = "question";
	public static final String ANSWER_KEY = "answer";
	public static final String ANSWERED_KEY = "answered";
	public static final String LAST_QUESTION_KEY = "lastQuestion";

	@Inject
	protected App mApp;

	@InjectView(R.id.questionText)
	protected TextView mQuestionText;

	@InjectView(R.id.questionImage)
	protected ImageView mQuestionImage;
	
	@InjectView(R.id.next)
	protected Button mNextButton;

	protected Question mQuestion;
	protected boolean mLastQuestion; 
	protected int mAnswer = NO_ANSWER;

	protected boolean mAnswered = false;
	
	/** implemented to provide layouts for subclasses */
	protected abstract int getLayoutResourceId();

	protected QuestionListener getQuestionListener() {
		return (QuestionListener) getActivity();
	} 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// don't do anything here because injection hasn't happened yet
		return inflater.inflate(getLayoutResourceId(), container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mQuestion = getArguments().getParcelable(QUESTION_KEY);
		mLastQuestion = getArguments().getBoolean(LAST_QUESTION_KEY, false);
		if (savedInstanceState != null) {
			mAnswer = savedInstanceState.getInt(ANSWER_KEY, NO_ANSWER);
			mAnswered = savedInstanceState.getBoolean(ANSWERED_KEY);
		} else {
			mAnswer = NO_ANSWER;
			mAnswered = false;
		}

		mQuestionText.setText(mApp.getHtmlCache().getHtml(mQuestion.getText()));
		mQuestionText.setTextSize(mApp.getConfig().fontSize());
		
		Drawable d = mApp.getAssetCache().getDrawable(mQuestion.getImage());
		if(d != null){
			mQuestionImage.setVisibility(View.VISIBLE);
			mQuestionImage.setImageDrawable(d);
		}else{
			mQuestionImage.setVisibility(View.GONE);
		}
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getQuestionListener().onNextQuestion();
			}
		});
		mNextButton.setText(mLastQuestion ? "Finish Quiz" : "Next Question");
		
		
		onAnswerChanged();
	}
	
	protected boolean isNextQuestionButtonEnabled(){
		return mAnswer != NO_ANSWER;
	}
	
	protected void onAnswerChanged(){
		mNextButton.setEnabled(isNextQuestionButtonEnabled());
		if(mAnswer != NO_ANSWER && !mAnswered){
			mAnswered = true;
			getQuestionListener().onQuestionAnswered(mAnswer);
		}
	}
	
	public void setAnswer(int answer){
		if(answer != mAnswer){
			mAnswer = answer;
			onAnswerChanged();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ANSWER_KEY, mAnswer);
		outState.putBoolean(ANSWERED_KEY, mAnswered);
	}
	
	public static Bundle newArguments(Question question, boolean lastQuestion){
		Bundle b = new Bundle();
		b.putParcelable(QUESTION_KEY, question);
		b.putBoolean(LAST_QUESTION_KEY, lastQuestion);
		return b;
	}
}
