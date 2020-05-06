package com.example.create_a_scribe;

import android.annotation.SuppressLint;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.create_a_scribe.adapters.LyricsAdapter;
import com.example.create_a_scribe.callbacks.LyricEventListener;
import com.example.create_a_scribe.callbacks.MainLyricActionModeCallback;
import com.example.create_a_scribe.db.LyricsDB;
import com.example.create_a_scribe.db.LyricsDao;
import com.example.create_a_scribe.model.Lyric;
import com.example.create_a_scribe.utils.NoteUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.create_a_scribe.EditLyricActivity.LYRIC_EXTRA_Key;
import static com.example.create_a_scribe.EditLyricActivity.MEDIA_EXTRA_Key;

// This class is the main activity that makes the lyricPad work
// This is used as a hub holding the navigation menu which contain buttons that point to the other activities.
// The LyricPad code is also housed here. The loading, creating, and deleting functionality is below
// as well as the click functionality and what occurs when you hold your finger on the lyrics
public class LyricPadActivity extends AppCompatActivity implements LyricEventListener, Drawer.OnDrawerItemClickListener {
    private static final String TAG = "LyricPadActivity";
    private RecyclerView recyclerView;
    private AlertDialog.Builder currAlertDialog;
    private AlertDialog urlDialog;
    private EditText myMusicUrl;
    private ArrayList<Lyric> lyrics;
    private LyricsAdapter adapter;
    private LyricsDao dao;
    private Bundle extras;
    private Intent myFileIntent;
    private String myUriString;
    private Uri myUri;
    private MainLyricActionModeCallback actionModeCallback;
    private int checkedCount = 0;
    private FloatingActionButton fab;
    private SharedPreferences settings;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    private int theme;

    @Override
    //This method runs when the activity itself is created. So this is the best method to initialize variables and also the method that points you to the proper activity and sets what layout you will be viewing
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_pad);//sets the layout to the view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupNavigation(savedInstanceState, toolbar);
        // init recyclerView
        recyclerView = findViewById(R.id.lyrics_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // init fab Button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 13/05/2018  add new note
                onAddNewLyric();
            }
        });

        dao = LyricsDB.getInstance(this).lyricsDao();
    }

    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar) {

        // Navigation menu items
        final List<IDrawerItem> iDrawerItems = new ArrayList<>();

        PrimaryDrawerItem drawerNotes = new PrimaryDrawerItem();
        PrimaryDrawerItem drawerArt = new PrimaryDrawerItem();
        PrimaryDrawerItem drawerLyrics = new PrimaryDrawerItem();


        iDrawerItems.add(new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_home_black_24dp));
        iDrawerItems.add(drawerNotes.withName("NotePad").withIcon(R.drawable.ic_note_black_24dp));
        iDrawerItems.add(drawerArt.withName("DrawPad").withIcon(R.drawable.baseline_brush_black_24));
        iDrawerItems.add(drawerLyrics.withName("LyricPad").withIcon(R.drawable.baseline_music_note_black_24));
        drawerNotes.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if(position == 2)
                {
                    Intent intent=new Intent(LyricPadActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        drawerArt.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if(position == 3)
                {
                    Intent intent=new Intent(LyricPadActivity.this,DrawPadActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });


        // sticky DrawItems ; footer menu items
        List<IDrawerItem> stickyItems = new ArrayList<>();
        SwitchDrawerItem switchDrawerItem = new SwitchDrawerItem()
                .withName("Dark Theme")
                .withChecked(theme == R.style.AppTheme_Dark)
                .withIcon(R.drawable.ic_dark_theme)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        // TODO: 02/10/2018 change to dark theme and save it to settings
                        if (isChecked) {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme_Dark).apply();
                        } else {
                            settings.edit().putInt(THEME_Key, R.style.AppTheme).apply();
                        }

                        // this lines means we want to close the app and open it again to change theme
                        TaskStackBuilder.create(LyricPadActivity.this)
                                .addNextIntent(new Intent(LyricPadActivity.this, LyricPadActivity.class))
                                .addNextIntent(getIntent()).startActivities();
                    }
                });

        PrimaryDrawerItem signInDrawerItem = new PrimaryDrawerItem();
        stickyItems.add(new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings_black_24dp));
        stickyItems.add(switchDrawerItem);
        switchDrawerItem.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                return false;
            }
        });

        // navigation menu header
        AccountHeader header = new AccountHeaderBuilder().withActivity(this)
                .addProfiles(new ProfileDrawerItem()
                        .withEmail("pladd365@gmail.com")
                        .withName("UIQ")
                        .withIcon(R.mipmap.ic_launcher_round))
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .withSelectionListEnabledForSingleProfile(false) // we need just one profile
                .build();

        // Navigation drawer
        new DrawerBuilder()
                .withActivity(this) // activity main
                .withToolbar(toolbar) // toolbar
                .withSavedInstance(savedInstanceState) // saveInstance of activity
                .withDrawerItems(iDrawerItems) // menu items
                .withTranslucentNavigationBar(true)
                .withStickyDrawerItems(stickyItems) // footer items
                .withAccountHeader(header) // header of navigation
                .withOnDrawerItemClickListener(this) // listener for menu items click
                .build();

    }

    private void loadLyrics() {
        this.lyrics = new ArrayList<>();
        List<Lyric> list = dao.getLyrics();// get All lyrics from DataBase
        this.lyrics.addAll(list);// adds all the lyrics to a list
        this.adapter = new LyricsAdapter(this, this.lyrics);//sends a copy of the current lyrics to the adapter.
        // set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();
        // add swipe helper to recyclerView

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);//adds swipe deleting functionality
    }

    /**
     * when no lyrics show msg in main_layout
     */
    private void showEmptyView() {
        if (lyrics.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_lyrics_view).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_lyrics_view).setVisibility(View.GONE);
        }
    }

    /**
     * Start EditNoteActivity.class for Create New Note
     */
    private void onAddNewLyric() {

        startActivity(new Intent(this, EditLyricActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lyric_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {

            case R.id.voice_record:{
                Intent intent=new Intent(LyricPadActivity.this, VoiceRecordActivity.class);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    //when clicking an old note, it loads the current lyrics
    protected void onResume() {
        super.onResume();
        loadLyrics();
    }

    @Override
    //when an old note was clicked, it sends you to the EditNoteActivity so you can then edit the note
    public void onLyricClick(Lyric lyric) {
        // TODO: 22/07/2018  lyric clicked : edit lyric
        Intent edit = new Intent(this, EditLyricActivity.class);
        edit.putExtra(LYRIC_EXTRA_Key, lyric.getId());
        startActivity(edit);

    }

    @Override
    public void onLyricLongClick(Lyric lyric) {
        // TODO: 22/07/2018 lyric long clicked : delete , share ..
        lyric.setChecked(true);
        checkedCount = 1;
        adapter.setMultiCheckMode(true);

        // set new listener to adapter intend off MainActivity listener that we have implement
        adapter.setListener(new LyricEventListener() {
            @Override
            public void onLyricClick(Lyric lyric) {
                lyric.setChecked(!lyric.isChecked()); // inverse selected
                if (lyric.isChecked())
                    checkedCount++;
                else checkedCount--;

                if (checkedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (checkedCount == 0) {
                    //  finish multi select mode wen checked count =0
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCount(checkedCount + "/" + lyrics.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLyricLongClick(Lyric lyric) {

            }
        });

        actionModeCallback = new MainLyricActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_lyrics)
                    onDeleteMultiLyrics();
                else if (menuItem.getItemId() == R.id.action_share_lyric)
                    onShareLyric();

                actionMode.finish();
                return false;
            }

        };

        // start action mode
        startActionMode(actionModeCallback);
        // hide fab button
        fab.setVisibility(View.GONE);
        actionModeCallback.setCount(checkedCount + "/" + lyrics.size());
    }

    private void onShareLyric() {
        // TODO: 22/07/2018  we need share just one Lyric not multiple

        Lyric lyric = adapter.getCheckedLyrics().get(0);
        // TODO: 22/07/2018 do your logic here to share lyric on social or something else
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String songText = lyric.getLyricTitle() + "\n by: "+ lyric.getLyricAuthor() + " \n" + lyric.getLyricContent() +
                "\n\n Created on : " + NoteUtils.dateFromLong(lyric.getLyricDate()) + "\n  With :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, songText);
        startActivity(share);


    }

    private void onDeleteMultiLyrics() {
        // TODO: 22/07/2018 delete multi lyrics

        List<Lyric> checkedLyrics = adapter.getCheckedLyrics();
        if (checkedLyrics.size() != 0) {
            for (Lyric lyric : checkedLyrics) {
                dao.deleteLyric(lyric);
            }
            // refresh Lyrics
            loadLyrics();
            Toast.makeText(this, checkedLyrics.size() + " Lyric(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No Lyric(s) selected", Toast.LENGTH_SHORT).show();

        //adapter.setMultiCheckMode(false);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false); // uncheck the lyrics
        adapter.setListener(this); // set back the old listener
        fab.setVisibility(View.VISIBLE);
    }

    // swipe to right or to left te delete
    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // TODO: 28/09/2018 delete note when swipe

                    if (lyrics != null) {
                        // get swiped lyric
                        Lyric swipedLyric = lyrics.get(viewHolder.getAdapterPosition());
                        if (swipedLyric != null) {
                            swipeToDelete(swipedLyric, viewHolder);

                        }

                    }
                }
            });

    private void swipeToDelete(final Lyric swipedLyric, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(LyricPadActivity.this)
                .setMessage("Delete Note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018 delete note
                        dao.deleteLyric(swipedLyric);
                        lyrics.remove(swipedLyric);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018  Undo swipe and restore swipedNote
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                    }
                })
                .setCancelable(false)
                .create().show();

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        return false;
    }
}