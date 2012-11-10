package com.jjm.android.quiz.activity;

import java.util.Random;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.Config;
import com.jjm.android.quiz.R;
import com.jjm.android.quiz.fragment.FlashcardFragment;
import com.jjm.android.quiz.fragment.MultipleChoiceFragment;
import com.jjm.android.quiz.fragment.QuestionFragment.QuestionListener;
import com.jjm.android.quiz.model.DataSource;
import com.jjm.android.quiz.model.Question;
import com.jjm.android.quiz.util.Task;

public class Quiz extends RoboFragmentActivity implements QuestionListener {
 
	public static final String CATEGORY_ID_EXTRA = "categoryId";
	public static final String MODE_EXTRA = "mode";
	private static final String QUESTION_INDEX_KEY = "questionIndex";
	private static final String SEED_KEY = "seed";
	private static final String NUM_ANSWERED_KEY = "numAnswered";
	private static final String NUM_CORRECT_KEY = "numCorrect";
 
	@Inject
	private Config mConfig;
	
	@Inject 
	private App mApp;
	
	@Inject 
	private DataSource mDataSource;
	
	@InjectView(R.id.progress)
	private TextView mProgressTextView;
	
	@InjectView(R.id.fragmentContainer)
	private ViewGroup mFragmentContainer;
	
	@InjectView(R.id.soundWarning)
	private View mSoundWarning;
	
	@InjectView(R.id.score)
	private TextView mScoreTextView;
	
	@InjectExtra(CATEGORY_ID_EXTRA)
	private long mCategoryId;
	
	@InjectExtra(MODE_EXTRA)
	private int mMode;
	
	
	private Question[] mQuestions; 
	private int mQuestionIndex;
	private long mSeed;
	private int mNumAnswered;
	private int mNumCorrect;
	private Task mNextQuestionTask;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
		
		if(savedInstanceState != null){
			mQuestionIndex = savedInstanceState.getInt(QUESTION_INDEX_KEY);
			mSeed = savedInstanceState.getLong(SEED_KEY);
			mNumAnswered = savedInstanceState.getInt(NUM_ANSWERED_KEY);
			mNumCorrect = savedInstanceState.getInt(NUM_CORRECT_KEY);
		}else{
			mSeed = System.currentTimeMillis(); 
		}
		
		if(mMode == Config.MODE_FLASHCARD){
			mScoreTextView.setVisibility(View.GONE);
		}else{
			mScoreTextView.setVisibility(View.VISIBLE);
		}
		
		selectQuestions();
		updateProgressAndScore();
		showCurrentQuestion();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(QUESTION_INDEX_KEY, mQuestionIndex);
		outState.putLong(SEED_KEY, mSeed);
	}
	
	private void selectQuestions(){
		int maxQuestions  = mConfig.getQuizLength();
		Cursor cursor = mDataSource.queryQuestions(mCategoryId);
		cursor.moveToFirst();
		int totalQuestions = cursor.getCount();
		if(maxQuestions == 0 || maxQuestions > totalQuestions){
			maxQuestions = totalQuestions;
		} 

		// This is from Knuth 3.4.2, algorithm S. It selects
		// each question with equal probability, and surprisingly,
		// the cursor never runs off the end.
		int t = 0; // record index
		int m = 0; // records selected so far
		Random r = new Random(mSeed);
		mQuestions = new Question[maxQuestions];
		for (; m < maxQuestions; t++, cursor.moveToNext()) {
			if (r.nextFloat() * (totalQuestions - t) < maxQuestions - m) {
				mQuestions[m++] = new Question(cursor);
			} 
		}
		// Give the questions a shuffle
		for (int i = 0; i < maxQuestions; i++) {
			int j = r.nextInt(maxQuestions);
			Question tmp = mQuestions[i];
			mQuestions[i] = mQuestions[j];
			mQuestions[j] = tmp;
		}

	}

	private void updateProgressAndScore(){
		mScoreTextView.setText(getScoreText());
		mProgressTextView.setText(getProgressText());
	}

	private String getProgressText(){
		return String.format("Question %d of %d",
				mQuestionIndex + 1,
				mQuestions.length);
	}
	
	private String getScoreText(){
		if(mNumAnswered == 0)
			return null;
		int pct = (int)(100.0f *  mNumCorrect / mNumAnswered);
		return String.format("%d of %d correct (%d%%)", 
				mNumCorrect, mNumAnswered, pct);
	}
	
	private CharSequence getCompleteDialogTitle(){
		return "TODO";
	}
	
	private CharSequence getCompleteDialogMessage(){
		return "Score: " + 
				mNumAnswered + " of " + mNumCorrect;
	}
	
	private void showCurrentQuestion(){
		Fragment fragment = createFragment();
		FragmentTransaction transaction = 
				getSupportFragmentManager().beginTransaction();
		if(mFragmentContainer.getChildCount() != 0){
			transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
				.replace(R.id.fragmentContainer, fragment);
		}else{
			transaction.add(R.id.fragmentContainer, fragment);
		}
		transaction.commit();
	}
	
	private Fragment createFragment(){
		Question question = mQuestions[mQuestionIndex];
		boolean last = mQuestionIndex + 1 == mQuestions.length;
		Fragment fragment = mMode == Config.MODE_FLASHCARD ?
				FlashcardFragment.newInstance(question, last)
				: MultipleChoiceFragment.newInstance(question, last);
		return fragment;
	}

	@Override
	public void onNextQuestion() {
		if(mQuestionIndex  + 1 >= mQuestions.length){
			Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					finish();
				}
			}, 2000);
		}else{
			mQuestionIndex ++;
			showCurrentQuestion();
		}
		updateProgressAndScore();
	}

	@Override
	public void onQuestionAnswered(int answer) {
		mNumAnswered ++;
		boolean correct = mQuestions[mQuestionIndex].getAnswer() == answer;
		if(correct){
			mNumCorrect ++;
		}
		// TODO Sound
		// TODO Auto next
		
	}
	
}
