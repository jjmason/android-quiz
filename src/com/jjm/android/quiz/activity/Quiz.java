package com.jjm.android.quiz.activity;

import java.io.IOException;
import java.util.Random;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.Config;
import com.jjm.android.quiz.R;
import com.jjm.android.quiz.fragment.FlashcardFragment;
import com.jjm.android.quiz.fragment.MultipleChoiceFragment;
import com.jjm.android.quiz.fragment.QuestionFragment.QuestionListener;
import com.jjm.android.quiz.model.DataSource;
import com.jjm.android.quiz.model.Question;
import com.jjm.android.quiz.util.SoundPoolAssistant;
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

	@InjectView(R.id.messages)
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
	private SoundPoolAssistant mSoundPool;
	private boolean mHighScore;
	private float mScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);

		if (savedInstanceState != null) {
			mQuestionIndex = savedInstanceState.getInt(QUESTION_INDEX_KEY);
			mSeed = savedInstanceState.getLong(SEED_KEY);
			mNumAnswered = savedInstanceState.getInt(NUM_ANSWERED_KEY);
			mNumCorrect = savedInstanceState.getInt(NUM_CORRECT_KEY);
		} else {
			mSeed = System.currentTimeMillis();
		}

		if (mMode == Config.MODE_FLASHCARD) {
			mScoreTextView.setVisibility(View.GONE);
		} else {
			mScoreTextView.setVisibility(View.VISIBLE);
		}

		selectQuestions();
		loadAudio();
		updateProgress();
		updateScore();
		showCurrentQuestion();
	}

	@Override
	protected void onDestroy() { 
		super.onDestroy();
		// make sure we don't try to show the next question after we're dead
		if(mNextQuestionTask != null){
			mNextQuestionTask.cancel();
		}
	}
	
	private void loadAudio() {
		mSoundPool = new SoundPoolAssistant(this, 1, AudioManager.STREAM_MUSIC);
		mSoundPool.load(R.raw.correct);
		mSoundPool.load(R.raw.incorrect);
		for (Question q : mQuestions) {
			if (q.getAudio() != null) {
				try {
					mSoundPool.load(q.getAudio());
				} catch (IOException e) {
					Log.e("Quiz", "error loading sound " + q.getAudio());
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(QUESTION_INDEX_KEY, mQuestionIndex);
		outState.putLong(SEED_KEY, mSeed);
	}

	private void selectQuestions() {
		int maxQuestions = mConfig.numQuestions();
		Cursor cursor = mDataSource.queryQuestions(mCategoryId);
		cursor.moveToFirst();
		int totalQuestions = cursor.getCount();
		if (maxQuestions == 0 || maxQuestions > totalQuestions) {
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

	private void updateProgress() {
		mProgressTextView.setText(getProgressText());
	}

	private void updateScore() {
		mScoreTextView.setText(getScoreText());
	}

	private String getProgressText() {
		return String.format("Question %d of %d", mQuestionIndex + 1,
				mQuestions.length);
	}

	private String getScoreText() {
		if (mNumAnswered == 0)
			return null;
		int pct = (int) (100.0f * mNumCorrect / mNumAnswered);
		return String.format("%d of %d correct (%d%%)", mNumCorrect,
				mNumAnswered, pct);
	}

	private CharSequence getCompleteDialogTitle() {
		if (mConfig.highScores() && mHighScore) {
			return getText(R.string.complete_high_score);
		}
		return getText(R.string.complete);
	}

	private CharSequence getCompleteDialogMessage() {
		int pct = (int) (100 * mScore);
		return getResources().getString(R.string.complete_score_format,
				mNumCorrect, mQuestions.length, pct);
	}

	private void showCurrentQuestion() {
		Fragment fragment = createFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mFragmentContainer.getChildCount() != 0) {
			transaction.setCustomAnimations(R.anim.slide_in_left,
					R.anim.slide_out_left).replace(R.id.fragmentContainer,
					fragment);
		} else {
			transaction.add(R.id.fragmentContainer, fragment);
		}
		transaction.commit();
		mSoundWarning.setVisibility(View.GONE);
		final Question q = mQuestions[mQuestionIndex];
		if (q.getAudio() != null) {
			if (mSoundPool.isSoundEnabled() && mConfig.soundEnabled()) {

				// use a little delay to let the question appear
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mSoundPool.play(q.getAudio());
					}
				}, 500);
			
			}else{
				mSoundWarning.setVisibility(View.VISIBLE);
			}
			
		}
	}

	private Fragment createFragment() {
		Question question = mQuestions[mQuestionIndex];
		boolean last = mQuestionIndex + 1 == mQuestions.length;
		Fragment fragment = mMode == Config.MODE_FLASHCARD ? FlashcardFragment
				.newInstance(question, last) : MultipleChoiceFragment
				.newInstance(question, last);
		return fragment;
	}

	@Override
	public void onNextQuestion() {
		cancelNextQuestionTask();
		if (mQuestionIndex + 1 >= mQuestions.length) {
			finishQuiz();
		} else {
			mQuestionIndex++;
			showCurrentQuestion();
		}
		updateProgress();
	}

	private void finishQuiz() {
		// in flashcard mode we just go home
		if (mMode == Config.MODE_FLASHCARD) {
			finish();
			return;
		}
		mScore = (float) mNumCorrect / (float) mQuestions.length;
		mHighScore = mDataSource.saveScore(mCategoryId, mScore);

		// in normal mode we show the user her score
		new AlertDialog.Builder(this)
				.setTitle(getCompleteDialogTitle())
				.setMessage(getCompleteDialogMessage())
				.setCancelable(false)
				.setPositiveButton(R.string.try_again,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// start a new quiz activity, then finish
								startActivity(new Intent(Quiz.this, Quiz.class)
										.putExtra(CATEGORY_ID_EXTRA,
												mCategoryId).putExtra(
												MODE_EXTRA, mMode));
								finish();
							}
						})
				.setNegativeButton(R.string.home,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// just finish
								finish();
							}
						})
				.create()
				.show();
			
	}

	private void cancelNextQuestionTask() {
		if (mNextQuestionTask != null) {
			mNextQuestionTask.cancel();
			mNextQuestionTask = null;
		}
	}

	@Override
	public void onQuestionAnswered(int answer) {
		mNumAnswered++;
		boolean correct = mQuestions[mQuestionIndex].getAnswer() == answer;
		if (correct) {
			mNumCorrect++;
		}
		mScoreTextView.setText(getScoreText());
		if (mConfig.soundEnabled()) {
			mSoundPool.play(correct ? R.raw.correct : R.raw.incorrect);
		}
		if (mConfig.autoNext()) {
			cancelNextQuestionTask();
			mHandler.postDelayed(mNextQuestionTask = new Task(new Runnable() {
				@Override
				public void run() {
					onNextQuestion();
				}
			}), mConfig.autoNextDelay());
		}
	}

}
