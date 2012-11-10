package com.jjm.android.quiz.activity;

import roboguice.activity.RoboListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.jjm.android.quiz.App;
import com.jjm.android.quiz.Config;
import com.jjm.android.quiz.R;
import com.jjm.android.quiz.model.Category;
import com.jjm.android.quiz.model.DataSource;
import com.jjm.android.quiz.model.XmlLoader;
import com.jjm.android.quiz.model.XmlLoader.XmlLoaderListener;

public class Categories extends RoboListActivity {
    private static final String TAG = Categories.class.getSimpleName();
    
	private DataSource mDataSource = new DataSource(this);
	
	@Inject 
	private App mApp;
	
	@Inject 
	private Config mConfig;
	
	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		Log.d(TAG, "onCreatePanelMenu 0x" + Integer.toHexString(featureId) + ", ?=" + 
				(featureId == Window.FEATURE_OPTIONS_PANEL));
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            boolean show = onCreateOptionsMenu(menu);
            Log.d(TAG, "called onCreateOptionsMenu=>"+show); 
            return show;
        }
        return false;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories); 
        if(!mDataSource.isDatabaseCreated()){
        	final ProgressBar progressBar = 
        			(ProgressBar)findViewById(R.id.progressBar);
        	progressBar.setIndeterminate(true);
        	final XmlLoader xmlLoader = new XmlLoader(this);
        	xmlLoader.setXmlLoaderListener(new XmlLoaderListener() {
				
				@Override
				public void onProgress(int current, int max) {
					if(max >= current){
						progressBar.setIndeterminate(false);
						if(max != progressBar.getMax()){
							progressBar.setMax(max);
						}
						progressBar.setProgress(current);
					}else{
						progressBar.setIndeterminate(true);
					}
				}
				
				@Override
				public boolean onError(String message, Throwable error) {
					throw new RuntimeException(message, error); // TODO
				}
				
				@Override
				public void onComplete() {
					initListAdapter();
				}
			});
        	
        	xmlLoader.load("categories.xml");
        }else{
        	initListAdapter();
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
		initListAdapter(); // so that we get the updated high scores
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
			startActivity(new Intent(this, About.class));
			break;
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	protected int getMode(long categoryId){
		if(mConfig.getQuizMode() == Config.MODE_VARIABLE){
			return mDataSource.getCategoryMode(categoryId);
		}else{
			return mConfig.getQuizMode();
		}
			
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		startActivity(new Intent(this, Quiz.class)
				.putExtra(Quiz.CATEGORY_ID_EXTRA, id)
				.putExtra(Quiz.MODE_EXTRA, getMode(id)));
	}
    
	private void initListAdapter(){
		setListAdapter(new Adapter(mDataSource.queryCategories()));
	}
	
	private CharSequence formatScore(float score){
		return ((int)(score * 100)) + "%";
	}
	
	private class Adapter extends CursorAdapter {
		public Adapter(Cursor cursor){
			super(Categories.this, cursor, false);
		}
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Category c = new Category(cursor);
			
			if(!mConfig.getShowIcons()){
				view.findViewById(R.id.iconFrame).setVisibility(View.GONE);
			}else{
				Drawable icon = mApp.getAssetCache().getDrawable(c.getIcon());
				if(icon != null){
					((ImageView)view.findViewById(R.id.icon)).setImageDrawable(icon);
				}
			}
			
			((TextView) view.findViewById(R.id.title)).setText(c.getTitle());
			
			TextView text = (TextView)view.findViewById(R.id.text);
			if(c.getText() != null){
				text.setText(mApp.getHtmlCache().getHtml(c.getText()));
			}else{
				text.setVisibility(View.GONE);
			}
			
			TextView info = (TextView) view.findViewById(R.id.info);
			if(c.hasHighScore() && mConfig.getShowHighScores()){
				info.setText(formatScore(c.getHighScore()));
			}else {
				info.setVisibility(View.GONE);
			}
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup root) {
			return getLayoutInflater().inflate(R.layout.category_list_item, root, false);
		}
		
	}
}
