package com.jjm.android.quiz.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.App.OnLoadListener;
import com.jjm.android.quiz.R;

public class HomeActivity extends RoboActivity {
	@SuppressWarnings("unused")
    private static final String TAG = "HomeActivity";
    
    @Inject 
    private App app;
    
    @InjectView(R.id.startQuizButton)
    private Button startQuizButton;
    
    @InjectView(R.id.progressBar)
    private ProgressBar progressBar;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        if(!app.isLoaded()){
        	startQuizButton.setVisibility(View.GONE);
        	progressBar.setVisibility(View.VISIBLE);
        	app.addOnLoadListener(new OnLoadListener() {
				public void onLoad() {
					startQuizButton.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			});
        }else{
        	startQuizButton.setVisibility(View.VISIBLE);
        	progressBar.setVisibility(View.GONE);
        }
        startQuizButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onStartQuizClick();
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
    
    private void onStartQuizClick(){
    	startActivity(new Intent(this, CategoriesActivity.class));
    }
}
