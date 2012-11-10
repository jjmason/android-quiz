package com.jjm.android.quiz.activity;

import roboguice.activity.RoboActivity;
import com.jjm.android.quiz.R;
import android.os.Bundle;

public class About extends RoboActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

}
