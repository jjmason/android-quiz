package com.jjm.android.quiz.model;

import java.util.Arrays;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jjm.android.quiz.model.DataSource.QuestionColumns;
import com.jjm.android.quiz.util.Util;

public class Question implements QuestionColumns, Parcelable {
	private long mId;
	private String mText;
	private String mImage;
	private String mAudio;
	private String[] mChoices;
	private int mAnswer;

	/**
	 * The text of the question
	 * 
	 * @return the text
	 */
	public String getText() {
		return mText;
	}

	public String getImage() {
		return mImage;
	}

	public String getAudio() {
		return mAudio;
	}

	/**
	 * Choices to present to the user.
	 * 
	 * @return the choices
	 */
	public String[] getChoices() {
		return mChoices;
	}

	/**
	 * The index of the correct answer in {@link #getChoices()}.
	 * 
	 * @return the index
	 */
	public int getAnswer() {
		return mAnswer;
	}

	public long getId() {
		return mId;
	}

	/**
	 * Create a question from the database
	 */
	public Question(Cursor c) {
		mText = c.getString(TEXT_INDEX);
		mImage = c.getString(IMAGE_INDEX);
		mAudio = c.getString(AUDIO_INDEX);
		mChoices = (String[]) Util.deserialize(c.getBlob(CHOICES_INDEX));
		mAnswer = c.getInt(ANSWER_INDEX);
		mId = c.getLong(_ID_INDEX);
	}

	@Override
	public String toString() {
		return "Question [mText=" + mText + ", mImage=" + mImage + ", mAudio="
				+ mAudio + ", mChoices=" + Arrays.toString(mChoices)
				+ ", mAnswer=" + mAnswer + "]";
	}

	/*
	 * Parcelable implementation
	 */
	public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
		public Question createFromParcel(Parcel source) {
			return new Question(source);
		}

		public Question[] newArray(int size) {
			return new Question[size];
		}
	};

	private Question(Parcel parcel) {
		mId = parcel.readLong();
		mText = parcel.readString();
		mImage = parcel.readString();
		mAudio = parcel.readString();
		mChoices = parcel.createStringArray();
		mAnswer = parcel.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mText);
		dest.writeString(mImage);
		dest.writeString(mAudio);
		dest.writeStringArray(mChoices);
		dest.writeInt(mAnswer);
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
