package com.example.create_a_scribe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class VoiceRecordActivity extends AppCompatActivity {

    // This is an activity used simply to host and start the voice recorder activity.
    // This is the activity seen in the android manifest and that is referenced from the LyricPadActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_record_main);
    }

}
