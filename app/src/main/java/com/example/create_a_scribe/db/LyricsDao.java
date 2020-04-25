package com.example.create_a_scribe.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.create_a_scribe.model.Lyric;

import java.util.List;

/**
 * Lyrics Data Object used to help access the lyrics
 */
@Dao
public interface LyricsDao {
    /**
     * Insert and save lyric to Database
     *
     * @param lyric
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLyric(Lyric lyric);

    /**
     * Delete lyric
     *
     * @param lyric that will be deleted
     */
    @Delete
    void deleteLyric(Lyric... lyric);

    /**
     * Update lyric
     *
     * @param lyric the lyric that will be updated
     */
    @Update
    void updateLyric(Lyric lyric);

    /**
     * List All Lyrics From Database
     *
     * @return list of Lyrics
     */
    @Query("SELECT * FROM lyrics")
    List<Lyric> getLyrics();

    /**
     * @param lyricId lyric id
     * @return Lyric
     */
    @Query("SELECT * FROM lyrics WHERE id = :lyricId")
    Lyric getLyricById(int lyricId);

    /**
     * Delete Lyric by Id from DataBase
     *
     * @param lyricId
     */
    @Query("DELETE FROM lyrics WHERE id = :lyricId")
    void deleteLyricById(int lyricId);

}