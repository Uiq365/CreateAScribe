package com.example.create_a_scribe.callbacks;

import com.example.create_a_scribe.model.Lyric;

public interface LyricEventListener {
    /**
     * call when lyric clicked.
     *
     * @param lyric: lyric item
     */
    void onLyricClick(Lyric lyric);

    /**
     * call when long Click on lyric.
     *
     * @param lyric : item
     */
    void onLyricLongClick(Lyric lyric);
}