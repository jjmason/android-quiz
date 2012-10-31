package com.jjm.android.quiz.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jjm.android.quiz.R;

public class CompleteActivity extends RoboActivity {
	public static final String CORRECT_EXTRA = "correct";
	public static final String TOTAL_EXTRA = "total";
	public static final String CATEGORY_INDEX_EXTRA = "categoryIndex";
	
	@InjectView(R.id.goHomeButton)
	private Button goHomeButton;
	
	@InjectView(R.id.tryAgainButton)
	private Button tryAgainButton;
	
	@InjectView(R.id.scoreTextView)
	private TextView scoreTextView;
	
	@InjectExtra(CORRECT_EXTRA)
	private int correct;
	
	@InjectExtra(TOTAL_EXTRA)
	private int total;
	
	@InjectExtra(CATEGORY_INDEX_EXTRA)
	private int categoryIndex;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        
        scoreTextView.setText(String.format("%d / %d", correct, total));
        
        tryAgainButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(CompleteActivity.this, QuizActivity.class)
						.putExtra(QuizActivity.CATEGORY_INDEX_EXTRA, categoryIndex));
				finish();
			}
		});
        
        goHomeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
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
}
