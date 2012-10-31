package com.jjm.android.quiz.activity;

import roboguice.activity.RoboActivity;
import android.os.Bundle;

import com.jjm.android.quiz.R;

public class AboutActivity extends RoboActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

}
