package com.jjm.android.quiz.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jjm.android.quiz.model.DataSource.CategoryColumns;

public class Category implements CategoryColumns,Parcelable {
	private long mId;
	private String mTitle;
	private String mText;
	private String mIcon;
	private float mHighScore;
	private int mMode;
	
	public Category(Cursor c){
		mId = c.getLong(_ID_INDEX);
		mTitle = c.getString(TITLE_INDEX);
		mText = c.getString(TEXT_INDEX);
		mIcon = c.getString(ICON_INDEX);
		mMode = c.getInt(MODE_INDEX);
		mHighScore = c.getFloat(HIGHSCORE_INDEX);
	}

	public long getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getText() {
		return mText;
	}

	public String getIcon() {
		return mIcon;
	}

	public int getMode(){
		return mMode;
	}
	
	public float getHighScore(){
		return mHighScore;
	}
	
	public boolean hasHighScore(){
		return mHighScore >= 0;
	}
	
	/*
	 * Parcelable implementation
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mTitle);
		dest.writeString(mText);
		dest.writeString(mIcon);
		dest.writeFloat(mHighScore);
		dest.writeInt(mMode);
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	private Category(Parcel source){
		mId = source.readLong();
		mTitle = source.readString();
		mText = source.readString();
		mIcon = source.readString();
		mHighScore = source.readFloat();
		mMode = source.readInt();
	}
	
	public static final Parcelable.Creator<Category> 
		CREATOR = new Creator<Category>() {
			
			@Override
			public Category[] newArray(int size) {
				return new Category[size];
			}
			
			@Override
			public Category createFromParcel(Parcel source) {
				return new Category(source);
			}
		};
	
	@Override
	public String toString() {
		return "Category [mId=" + mId + ", mTitle=" + mTitle + ", mText="
				+ mText + ", mIcon=" + mIcon + ",mMode=" + mMode + "]";
	}
	
}
