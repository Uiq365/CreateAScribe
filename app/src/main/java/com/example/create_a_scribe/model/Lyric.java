package com.example.create_a_scribe.model;

import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// This class is used to structure the information received from the lyrics to also make it easier for the db to store them

@Entity(tableName = "lyrics")
public class Lyric {
    @PrimaryKey(autoGenerate = true)
    private int id; // default value
    @ColumnInfo(name="title")
    private String  lyricTitle;
    @ColumnInfo(name="author")
    private String lyricAuthor;
    @ColumnInfo(name = "content")
    private String lyricContent;
    @ColumnInfo(name = "date")
    private long lyricDate;

    @Ignore // we don't want to store this value on database so ignore it
    private boolean checked = false;

    public Lyric() {
    }

    // this methods holds and sets the lyricContent, lyricAuthor, and lyricDate of each lyric that is created.
    public Lyric(String lyricTitle, String lyricAuthor, String lyricContent, long lyricDate) {
        this.lyricContent = lyricContent;
        this.lyricTitle= lyricTitle;
        this.lyricAuthor = lyricAuthor;
        this.lyricDate = lyricDate;
    }

    // getter and setter methods for the lyrics and different lyric fields
    public String getLyricContent() {
        return lyricContent;
    }

    public void setLyricContent(String lyricContent) {
        this.lyricContent = lyricContent;
    }

    public String getLyricTitle() {
        return lyricTitle;
    }

    public void setLyricTitle(String lyricTitle) {
        this.lyricTitle = lyricTitle;
    }

    public String getLyricAuthor() {
        return lyricAuthor;
    }

    public void setLyricAuthor(String lyricAuthor) {
        this.lyricAuthor = lyricAuthor;
    }

    public long getLyricDate() {
        return lyricDate;
    }

    public void setLyricDate(long lyricDate) {
        this.lyricDate = lyricDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return  "Song{" +
                lyricTitle + "by: " + lyricAuthor + "id=" + id +
                ", lyricDate=" + lyricDate +
                '}';
    }
}