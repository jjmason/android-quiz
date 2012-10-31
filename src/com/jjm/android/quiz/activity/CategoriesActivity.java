package com.jjm.android.quiz.activity;

import roboguice.activity.RoboListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.R;

public class CategoriesActivity extends RoboListActivity {

	@Inject
	private App app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.category_list_item, R.id.textView,
				app.getCategoryNames()));
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, QuizActivity.class);
		i.putExtra(QuizActivity.CATEGORY_INDEX_EXTRA, position);
		startActivity(i);
		finish();
	}
}
