package com.example.create_a_scribe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.create_a_scribe.db.LyricsDB;
import com.example.create_a_scribe.db.LyricsDao;
import com.example.create_a_scribe.model.Lyric;

import java.util.Date;

public class EditLyricActivity extends AppCompatActivity {
    private EditText lyricTitle;
    private EditText lyricAuthor;
    private EditText lyricContent;
    private LyricsDao dao;
    private Lyric temp;
    public static final String LYRIC_EXTRA_Key = "lyric_id";

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
        if (id == R.id.save_lyric)
            onSaveLyric();
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
            author = "by: Unknown";

        if (!title.isEmpty()) {
            long date = new Date().getTime(); // get  system time
            // if  exist update else create new
            if (temp == null) {
                temp = new Lyric(title, author, content, date);
                dao.insertLyric(temp); // create new note and inserted to database
            } else {
                temp.setLyricTitle(title);
                temp.setLyricAuthor(author);
                temp.setLyricContent(content);
                temp.setLyricDate(date);
                dao.updateLyric(temp); // change text and date and update note on database
            }
            }

            finish(); // return to the MainActivity
        }

    }