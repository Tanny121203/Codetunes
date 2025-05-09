package com.example.trymusicplayer.songs;

import java.io.Serializable;

public final class Song implements Serializable {
    private final String path;
    private final String title;
    private final String artist;
    private final String album;
    private final String duration;

    /**
     * Constructs a new Song object with the given data.
     *
     * @param path     the file path of the song
     * @param title    the title of the song
     * @param artist   the artist of the song
     * @param album    the album of the song
     * @param duration the duration of the song
     */
    public Song(String path, String title, String artist, String album, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }


    // Getters only (Makes the songs immutable to ensure that the songs will not be modified after creation)


    /**
     * Returns the file path of the song.
     *
     * @return the file path of the song
     */
    public String getPath() { return path; }


    /**
     * Returns the title of the song.
     *
     * @return the title of the song
     */
    public String getTitle() { return title; }


    /**
     * Returns the artist of the song.
     *
     * @return the artist of the song
     */
    public String getArtist() { return artist; }


    /**
     * Returns the album of the song.
     *
     * @return the album of the song
     */
    public String getAlbum() { return album; }


    /**
     * Returns the duration of the song.
     *
     * @return the duration of the song
     */
    public String getDuration() { return duration; }
}
