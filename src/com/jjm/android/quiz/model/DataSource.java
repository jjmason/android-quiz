package com.jjm.android.quiz.model;

import java.io.File;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.inject.Inject;
import com.jjm.android.quiz.util.Util;

/**
 * Provides access to the database.
 */
public class DataSource {
	// increasing this value will force the database to be reloaded.  Decreasing 
	// it will cause an error.
	public static final int DB_VERSION =5;

	
	public interface CategoryColumns extends BaseColumns {
		public static final String TABLE_NAME = "category";

		public static final String TITLE_COLUMN = "title";
		public static final String TEXT_COLUMN = "text";
		public static final String ICON_COLUMN = "icon";
		public static final String HIGHSCORE_COLUMN = "highscore";
		public static final String MODE_COLUMN = "mode";

		public static final String[] COLUMNS = { _ID, TITLE_COLUMN,
				TEXT_COLUMN, ICON_COLUMN, HIGHSCORE_COLUMN, MODE_COLUMN };

		public static final int _ID_INDEX = 0;
		public static final int TITLE_INDEX = 1;
		public static final int TEXT_INDEX = 2;
		public static final int ICON_INDEX = 3;
		public static final int HIGHSCORE_INDEX = 4;
		public static final int MODE_INDEX = 5;

		public static final String CREATE_SQL = 
				"CREATE TABLE " + TABLE_NAME + "(" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ TITLE_COLUMN + " TEXT," 
				+ TEXT_COLUMN + " TEXT,"
				+ ICON_COLUMN + " TEXT," 
				+ HIGHSCORE_COLUMN + " REAL,"
				+ MODE_COLUMN + " INTEGER)";
		
	}

	public interface QuestionColumns extends BaseColumns {
		public static final String TABLE_NAME = "question";

		public static final String CATEGORY_ID_COLUMN = "category_id";
		public static final String TEXT_COLUMN = "text";
		public static final String AUDIO_COLUMN = "audio";
		public static final String IMAGE_COLUMN = "image";
		public static final String CHOICES_COLUMN = "choices";
		public static final String ANSWER_COLUMN = "answer";

		public static final String[] COLUMNS = { _ID, CATEGORY_ID_COLUMN,
				TEXT_COLUMN, AUDIO_COLUMN, IMAGE_COLUMN, CHOICES_COLUMN,
				ANSWER_COLUMN };

		public static final int _ID_INDEX = 0;
		public static final int CATEGORY_ID_INDEX = 1;
		public static final int TEXT_INDEX = 2;
		public static final int AUDIO_INDEX = 3;
		public static final int IMAGE_INDEX = 4;
		public static final int CHOICES_INDEX = 5;
		public static final int ANSWER_INDEX = 6;

		public static final String CREATE_SQL = 
				"CREATE TABLE " + TABLE_NAME + "(" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CATEGORY_ID_COLUMN + " INTEGER," 
				+ TEXT_COLUMN + " TEXT,"
				+ AUDIO_COLUMN + " TEXT," 
				+ IMAGE_COLUMN + " TEXT,"
				+ CHOICES_COLUMN + " BLOB," 
				+ ANSWER_COLUMN + " INTEGER)"; 
	}

	private OpenHelper openHelper;
	private Context context;
	
	private static class OpenHelper extends SQLiteOpenHelper {
		public OpenHelper(Context context) {
			super(context, "quiz.db", null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CategoryColumns.CREATE_SQL);
			db.execSQL(QuestionColumns.CREATE_SQL);
			db.execSQL("CREATE TABLE metadata (apkLastModified INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE " + CategoryColumns.TABLE_NAME);
			db.execSQL("DROP TABLE " + QuestionColumns.TABLE_NAME);
			db.execSQL("DROP TABLE metadata");
			onCreate(db);
		}
	}

	@Inject
	public DataSource(Context context) {
		this.context = context;
		openHelper = new OpenHelper(context); 
	}

	public void reset(){
		openHelper.onUpgrade(openHelper.getWritableDatabase(), 0, 1);
		updateApkLastModified();
	}
	
	public long getRealApkLastModified(){
		return new File(context.getPackageCodePath()).lastModified();
	}
	
	public boolean isDatabaseCreated() { 
		return getRealApkLastModified() <= getApkLastModified();
	}
 
	public long getApkLastModified(){
		Cursor c = read().query("metadata");
		if(c!=null && c.moveToFirst()){
			return c.getLong(c.getColumnIndex("apkLastModified"));
		}
		return 0;
	}
	
	public void updateApkLastModified(){
		setApkLastModified(getRealApkLastModified());
	}
	
	public void setApkLastModified(long lastModified){
		write().delete("metadata", "1");
		
		ContentValues cv = new ContentValues();
		cv.put("apkLastModified", lastModified);
		
		write().insert("metadata", cv);
	}
	
	public void updateCategory(long id, String title, String text, String icon, int mode){
		ContentValues cv = new ContentValues();
		cv.put(CategoryColumns.TITLE_COLUMN, title);
		cv.put(CategoryColumns.TEXT_COLUMN, text);
		cv.put(CategoryColumns.ICON_COLUMN, icon);
		cv.put(CategoryColumns.MODE_COLUMN, mode);
		write().update(CategoryColumns.TABLE_NAME, "_id=" + id, cv);
	}
	
	public long addCategory( ) { 
		ContentValues cv = new ContentValues();
		cv.put(CategoryColumns.HIGHSCORE_COLUMN, -1f);
		return write().insert(CategoryColumns.TABLE_NAME, cv);
	}

	public long addQuestion(long categoryId, String text, String image,
			String audio, List<String> choices, int answer) {
		return addQuestion(categoryId, text, image, audio,
				choices.toArray(new String[choices.size()]), answer);
	}

	public long addQuestion(long categoryId, String text, String image,
			String audio, String[] choices, int answer) {
		ContentValues cv = new ContentValues();
		cv.put(QuestionColumns.CATEGORY_ID_COLUMN, categoryId);
		cv.put(QuestionColumns.TEXT_COLUMN, text);
		cv.put(QuestionColumns.IMAGE_COLUMN, image);
		cv.put(QuestionColumns.AUDIO_COLUMN, audio);
		cv.put(QuestionColumns.CHOICES_COLUMN, Util.serialize(choices));
		cv.put(QuestionColumns.ANSWER_COLUMN, answer);
		return write().insert(QuestionColumns.TABLE_NAME, cv);
	}

	public boolean saveScore(long categoryId, float highScore) {
		ContentValues cv = new ContentValues();
		cv.put(CategoryColumns.HIGHSCORE_COLUMN, highScore);

		return 0 != write().update(
				CategoryColumns.TABLE_NAME,
				CategoryColumns.HIGHSCORE_COLUMN + "<" + highScore + " AND "
						+ CategoryColumns._ID + "=" + categoryId, cv);

	}

	public Cursor queryCategories() {
		return read()
				.query(CategoryColumns.TABLE_NAME, CategoryColumns.COLUMNS);
	}
	
	public Category getCategory(long categoryId){
		Cursor c = read().query(CategoryColumns.TABLE_NAME, CategoryColumns.COLUMNS,
				CategoryColumns._ID + "=" + categoryId);
		Category category = null;
		if(c.moveToFirst()){
			category = new Category(c);
		}
		c.close();
		return category;
	}
	
	public int getCategoryMode(long categoryId){
		return getCategory(categoryId).getMode();
	}

	public Cursor queryQuestions(long categoryId){
		return read().query(QuestionColumns.TABLE_NAME,
				QuestionColumns.COLUMNS,
				QuestionColumns.CATEGORY_ID_COLUMN + "=" + categoryId);
	}
	
	public Question[] createQuiz(long categoryId, int numQuestions, long seed) {
		Cursor c = read().query(QuestionColumns.TABLE_NAME,
				QuestionColumns.COLUMNS,
				QuestionColumns.CATEGORY_ID_COLUMN + "=" + categoryId);
		int total = c.getCount();
		if (total < numQuestions)
			numQuestions = total;

		// This is from Knuth 3.4.2, algorithm S. It selects
		// each question with equal probability, and surprisingly,
		// the cursor never runs off the end. Cool, huh?
		int t = 0; // record index
		int m = 0; // records selected so far
		Random r = new Random(seed);
		Question[] qs = new Question[numQuestions];
		for (; m < numQuestions; t++, c.moveToNext()) {
			if (r.nextFloat() * (total - t) < numQuestions - m) {
				qs[m++] = new Question(c);
			}
		}
		// Give the questions a shuffle
		for (int i = 0; i < numQuestions; i++) {
			int j = r.nextInt(numQuestions);
			Question tmp = qs[i];
			qs[i] = qs[j];
			qs[j] = tmp;
		}
		return qs;
	}

	public DatabaseWrapper read() {
		return new DatabaseWrapper(openHelper.getReadableDatabase());
	}

	public DatabaseWrapper write() {
		return new DatabaseWrapper(openHelper.getWritableDatabase());
	}

	public static class DatabaseWrapper {
		public final SQLiteDatabase db;

		public DatabaseWrapper(SQLiteDatabase db) {
			this.db = db;
		}

		public Cursor query(String tableName, String[] columns,
				String selection, String[] selectionArgs, String groupBy,
				String having, String orderBy) {
			return db.query(tableName, columns, selection, selectionArgs,
					groupBy, having, orderBy);
		}

		public Cursor query(String table, String[] columns, String selection,
				String[] selectionArgs, String groupBy, String having) {
			return query(table, columns, selection, selectionArgs, groupBy,
					having, null);
		}

		public Cursor query(String table, String[] columns, String selection,
				String[] selectionArgs, String groupBy) {
			return query(table, columns, selection, selectionArgs, groupBy,
					null);
		}

		public Cursor query(String table, String[] columns, String selection,
				String[] selectionArgs) {
			return query(table, columns, selection, selectionArgs, null);
		}

		public Cursor query(String table, String[] columns, String selection) {
			return query(table, columns, selection, null);
		}

		public Cursor query(String table, String[] columns) {
			return query(table, columns, null);
		}

		public Cursor query(String table) {
			return query(table, null);
		}

		public long insert(String table, ContentValues cv) {
			return db.insert(table, "title", cv);
		}

		public int update(String table, String selection, ContentValues cv) {
			return db.update(table, cv, selection, null);
		}
		
		public int delete(String table, String selection, String[] selectionArgs){
			return db.delete(table, selection, selectionArgs);
		}
		
		public int delete(String table, String selection){
			return delete(table, selection, null);
		}
	}
}
