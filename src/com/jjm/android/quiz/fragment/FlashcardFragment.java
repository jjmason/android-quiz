package com.jjm.android.quiz.fragment;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jjm.android.quiz.R;
import com.jjm.android.quiz.model.Question;
import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class FlashcardFragment extends QuestionFragment {
	private static final int ANSWER_CHILD_INDEX = 1;
	private static final int QUESTION_CHILD_INDEX = 0;
	
	private static final FlipDirection QUESTION_TO_ANSWER_DIR = FlipDirection.LEFT_RIGHT;
	private static final FlipDirection ANSWER_TO_QUESTION_DIR = FlipDirection.RIGHT_LEFT;

	private static final String SHOWING_ANSWER_KEY = "showingAnswer";
	
	@InjectView(R.id.answerText)
	private TextView mAnswerText;

	@InjectView(R.id.flipper)
	private ViewFlipper mViewFlipper;

	@InjectView(R.id.buttons)
	private View mButtons;

	@InjectView(R.id.show)
	private Button mShowButton;

	private boolean mShowingAnswer;

	@Override
	protected int getLayoutResourceId() {
		return R.layout.flashcard_fragment;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAnswerText.setText(mApp.getHtmlCache().getHtml(
				mQuestion.getChoices()[mQuestion.getAnswer()]));
		mAnswerText.setTextSize(mApp.getConfig().fontSize());
		mShowButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleShowingAnswer();
			}
		});
		if(savedInstanceState != null){
			mShowingAnswer = savedInstanceState.getBoolean(SHOWING_ANSWER_KEY);
		}
		if(mShowingAnswer){
			mViewFlipper.setDisplayedChild(ANSWER_CHILD_INDEX);
		}else{
			mViewFlipper.setDisplayedChild(QUESTION_CHILD_INDEX);
		}
	}

	private void toggleShowingAnswer() {
		FlipDirection direction = mShowingAnswer ? ANSWER_TO_QUESTION_DIR
				: QUESTION_TO_ANSWER_DIR;
		AnimationFactory.flipTransition(mViewFlipper, direction);
		mShowButton.setText(mShowingAnswer ? R.string.show_question
				: R.string.show_answer);
		mShowingAnswer = !mShowingAnswer;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SHOWING_ANSWER_KEY, mShowingAnswer);
	}
	
	@Override
	protected boolean isNextQuestionButtonEnabled() {
		return true;
	}
	
	public static FlashcardFragment newInstance(Question question, boolean isLastQuestion){
		FlashcardFragment fragment = new FlashcardFragment();
		fragment.setArguments(newArguments(question, isLastQuestion));
		return fragment;
	}
}
