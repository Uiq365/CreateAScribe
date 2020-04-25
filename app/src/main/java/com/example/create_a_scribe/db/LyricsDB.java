package com.example.create_a_scribe.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.create_a_scribe.model.Lyric;
import com.example.create_a_scribe.model.Note;

//This class is to manage the database that the lyrics will be saved in.
//Uses the structure from the Lyric class
@Database(entities = Lyric.class, version = 1)
public abstract class LyricsDB extends RoomDatabase {
    public abstract LyricsDao lyricsDao();

    public static final String DATABASE_NAME = "lyricsDb";
    private static LyricsDB instance;

    public static LyricsDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, LyricsDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
        }
    }
