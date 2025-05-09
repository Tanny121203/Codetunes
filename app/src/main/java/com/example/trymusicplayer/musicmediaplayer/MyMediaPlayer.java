package com.example.trymusicplayer.musicmediaplayer;

import android.media.MediaPlayer;

import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

import java.io.IOException;

public class MyMediaPlayer {
    static MediaPlayer instance;
    public static int currentIndex = -1;


    public static MediaPlayer getInstance() {
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }

}
