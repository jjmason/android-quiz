package com.jjm.android.quiz.activity;

import java.util.ArrayList;
import java.util.Random;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.R;
import com.jjm.android.quiz.model.Question;
import com.jjm.android.quiz.view.QuizButton;

public class QuizActivity extends RoboActivity {
	private static final int MAX_BUTTONS = 5;

	public static final String CATEGORY_INDEX_EXTRA = "categoryIndex";
	public static final String QUIZ_LENGTH_EXTRA = "quizLength";
	
	// Delay before going to the next question
	private static final long DELAY_MILLIS = 1000;

	@Inject
	private App app;

	@InjectView(R.id.buttonHolder)
	private ViewGroup buttonHolder;
	@InjectView(R.id.questionTextView)
	private TextView questionTextView;
	private ArrayList<QuizButton> quizButtons = new ArrayList<QuizButton>();

	private ArrayList<Question> questions;
	private int currentQuestion;
	private int score;
	private Handler handler = new Handler();
	private int categoryIndex;
	private int quizLength;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		categoryIndex = getIntent().getIntExtra(CATEGORY_INDEX_EXTRA, 0);
		quizLength = getIntent().getIntExtra(QUIZ_LENGTH_EXTRA,
				app.getQuizLength());

		currentQuestion = 0;

		LayoutInflater inflater = getLayoutInflater();
		for (int i = 0; i < MAX_BUTTONS; ++i) {
			QuizButton btn = (QuizButton) inflater.inflate(
					R.layout.quiz_button, null);
			quizButtons.add(btn);
		}

		pickQuestions();
		updateView();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void pickQuestions() {
		Object[] all = app.getQuiz().getCategories()
				.get(categoryIndex).getQuestions().toArray();
		// Shuffle them
		for (int i = 0; i < all.length; i++) {
			int j = new Random().nextInt(all.length);
			Question tmp = (Question)all[i];
			all[i] = all[j];
			all[j] = tmp;
		}
		// Take the first N
		quizLength = Math.min(quizLength, all.length);
		questions = new ArrayList<Question>(quizLength);
		for (int i = 0; i < quizLength; i++) {
			questions.add((Question)all[i]);
		}
	}

	private void updateView() {
		Question q = questions.get(currentQuestion);
		questionTextView.setText(q.getText());
		buttonHolder.removeAllViews();
		
		for(int i=0;i<q.getChoices().size() && i < MAX_BUTTONS;i++){
			QuizButton qb = quizButtons.get(i);
			qb.setText(q.getChoices().get(i));
			qb.setShowingAnswer(false);
			if(i == q.getAnswer()){
				qb.setCorrect(true);
				qb.setOnClickListener(onCorrectButtonClickListener);
			}else{
				qb.setCorrect(false);
				qb.setOnClickListener(onIncorrectButtonClickListener);
			} 
			buttonHolder.addView(qb,getQuizButtonLayoutParams());
		}
	}
	
	private LayoutParams getQuizButtonLayoutParams(){
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		p.setMargins(36, 4, 36, 4);
		return p;
	}
	
	private void onButtonClick(boolean correct){
		if(correct)
			score++;
		app.playSound(correct);
		for(QuizButton qb : quizButtons){
			qb.setShowingAnswer(true);
		}
		handler.postDelayed(new Runnable() {
			
			public void run() {
				currentQuestion ++;
				if(currentQuestion >= questions.size()){
					startActivity(new Intent(QuizActivity.this,  CompleteActivity.class)
							.putExtra(CompleteActivity.CORRECT_EXTRA, score)
							.putExtra(CompleteActivity.TOTAL_EXTRA, questions.size())
							.putExtra(CompleteActivity.CATEGORY_INDEX_EXTRA, categoryIndex));
					finish();
				}else{
					updateView();
				}
			}
		}, DELAY_MILLIS);
	}
	
	private final OnClickListener onCorrectButtonClickListener = new OnClickListener() {
		public void onClick(View v) {
			onButtonClick(true);
		}
	};
	
	private final OnClickListener onIncorrectButtonClickListener = new OnClickListener() {
		public void onClick(View v) {
			onButtonClick(false);
		}
	};
}
