package com.example.create_a_scribe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.create_a_scribe.db.LyricsDB;
import com.example.create_a_scribe.db.LyricsDao;
import com.example.create_a_scribe.model.Lyric;

import java.io.File;
import java.util.Date;

//this is the activity that brings you to the edit lyric screen.
// it also allows for the saving of lyrics when the button id is detected.
public class EditLyricActivity extends AppCompatActivity {
    private ImageView imagePlayPause;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private String myUrl;
    private TextView pathView;
    private MediaPlayer mediaPlayer;
    private String myStringUri;
    private Handler handler = new Handler();
    private EditText lyricTitle;
    private EditText lyricAuthor;
    private EditText lyricContent;
    private LyricsDao dao;
    private Lyric temp;
    public static final String LYRIC_EXTRA_Key = "lyric_id";
    public static final String MEDIA_EXTRA_Key = "song_path";


    //on create the activity sets up the theme based on the shared preferences and sets the view and the inputLyric button
    //it also gets the lyrics from the database and if there is any extra passed by the intent it will populate the lyric edit screen with the previous information
    //it sets the different textViews for the different field inputs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set theme
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt(MainActivity.THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lyric);
        Toolbar toolbar = findViewById(R.id.edit_lyric_activity_toolbar);
        setSupportActionBar(toolbar);

        lyricTitle = findViewById(R.id.lyric_title);
        lyricAuthor = findViewById(R.id.lyric_author);
        lyricContent = findViewById(R.id.lyric_content);
        dao = LyricsDB.getInstance(this).lyricsDao();
        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(LYRIC_EXTRA_Key, 0);
            temp = dao.getLyricById(id);
            lyricTitle.setText(temp.getLyricTitle());
            lyricAuthor.setText(temp.getLyricAuthor());
            lyricContent.setText(temp.getLyricContent());
        } else lyricTitle.setFocusable(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_lyric_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()){

            case R.id.save_lyric: {
                onSaveLyric();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void onSaveLyric() {
        // TODO: 20/06/2018 Save Lyric
        String title = lyricTitle.getText().toString();
        String author = lyricAuthor.getText().toString();
        String content = lyricContent.getText().toString();

        if(title.isEmpty()) {
            title = content;
        }
        if(author.isEmpty())
            author = "Unknown";

        if (!title.isEmpty()) {
            long date = new Date().getTime(); // get  system time
            // if  exist update else create new
            if (temp == null) {
                temp = new Lyric(title, author, content, date);
                dao.insertLyric(temp); // create new lyric and inserted to database
            } else {
                temp.setLyricTitle(title);
                temp.setLyricAuthor(author);
                temp.setLyricContent(content);
                temp.setLyricDate(date);
                dao.updateLyric(temp); // change title, author, lyric content and date and updates the lyric on a database
            }
            }

            finish(); // return to the MainActivity
        }

    }