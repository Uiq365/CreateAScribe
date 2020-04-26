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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.create_a_scribe.db.LyricsDB;
import com.example.create_a_scribe.db.LyricsDao;
import com.example.create_a_scribe.model.Lyric;

import java.io.File;
import java.util.Date;

public class EditLyricActivity extends AppCompatActivity {
    private ImageView imagePlayPause;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private Uri myUri;
    private TextView pathView;
    private MediaPlayer mediaPlayer;
    private Intent myFileIntent;
    private Handler handler = new Handler();
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

        imagePlayPause = findViewById(R.id.imagePlayPause);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        pathView = findViewById(R.id.pathView);
        mediaPlayer = new MediaPlayer();

        playerSeekBar.setMax(100);

        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setImageResource(R.drawable.ic_play);
                }else{
                    mediaPlayer.start();
                    imagePlayPause.setImageResource(R.drawable.ic_pause);
                    upDateSeekBar();
                }
            }
        });
        prepareMediaPlayer();
    }

    private void prepareMediaPlayer(){
        try {
            //mediaPlayer.setDataSource(getApplicationContext(), myUri);//plays music based on a Uri, internal address for a file
            mediaPlayer = MediaPlayer.create(this, R.raw.jesus_walks);
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
            Toast.makeText(this, "Playing your Beat!!", Toast.LENGTH_SHORT).show();
        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            upDateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void upDateSeekBar(){
        if(mediaPlayer.isPlaying()){
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    // a method used to convert the milliSeconds passed in as a parameter
    // into a user friendly way by constructing them as readable strings
    private String milliSecondsToTimer(long milliSeconds){
        String timerString = "";
        String secondsString;

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0) {
            timerString = hours + ":";
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;

        return timerString;
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
            case R.id.pickMusicFile: {
                onDirectoryOpen();
                //prepareMediaPlayer();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDirectoryOpen(){
        myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("*/*");
        startActivityForResult(myFileIntent, 10);

    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){

            case 10:

                if(resultCode == RESULT_OK){
                    String path = data.getData().getPath();
                    pathView.setText(Uri.parse(path).toString());
                    myUri = Uri.fromFile(new File(path));
                }
                break;
        }
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