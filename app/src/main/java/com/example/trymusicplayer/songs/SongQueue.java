package com.example.trymusicplayer.songs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SongQueue implements Serializable {

    private List<Song> originalSongs;
    private List<Song> shuffledSongs;
    private boolean isShuffled;
    private boolean isRepeat;
    private Song repeatSong;

    public SongQueue() {
        this.originalSongs = new ArrayList<>();
        this.shuffledSongs = new ArrayList<>();
        this.isShuffled = false;
        this.isRepeat = false;
        this.repeatSong = null;
    }

    public void setSongs(List<Song> songs) {
        if (songs == null) {
            this.originalSongs = new ArrayList<>();
        } else {
            this.originalSongs = new ArrayList<>(songs);
            // Sort songs alphabetically by title
            Collections.sort(this.originalSongs, (s1, s2) -> {
                if (s1 == null || s2 == null) return 0;
                String title1 = s1.getTitle() != null ? s1.getTitle() : "";
                String title2 = s2.getTitle() != null ? s2.getTitle() : "";
                return title1.compareToIgnoreCase(title2);
            });
        }
        this.shuffledSongs.clear();
        this.isShuffled = false;
        this.isRepeat = false;
        this.repeatSong = null;
    }

    public void enqueue(Song song) {
        if (song != null) {
            originalSongs.add(song);
            if (isShuffled) {
                shuffledSongs.add(song);
            }
        }
    }

    public Song dequeue() {
        if (isEmpty()) {
            return null;
        }
        Song song;
        if (isShuffled) {
            if (!shuffledSongs.isEmpty()) {
                song = shuffledSongs.remove(0);
                originalSongs.remove(song);
            } else {
                song = originalSongs.remove(0);
            }
        } else {
            song = originalSongs.remove(0);
        }
        return song;
    }

    public void shuffle() {
        if (!isShuffled) {
            shuffledSongs = new ArrayList<>(originalSongs);
            Collections.shuffle(shuffledSongs);
            isShuffled = true;
        } else {
            // If already shuffled, create a new shuffle
            Collections.shuffle(shuffledSongs);
        }
    }

    public void repeat(Song song) {
        if (!isRepeat) {
            isRepeat = true;
            repeatSong = song;
        } else {
            isRepeat = false;
            repeatSong = null;
        }
    }

    public boolean isEmpty() {
        return originalSongs.isEmpty();
    }

    public Song get(int position) {
        if (position < 0) return null;
        
        if (isShuffled) {
            if (position < shuffledSongs.size()) {
                return shuffledSongs.get(position);
            }
        } else {
            if (position < originalSongs.size()) {
                return originalSongs.get(position);
            }
        }
        return null;
    }

    public int getSize() {
        if (isShuffled) {
            return shuffledSongs.size();
        } else {
            return originalSongs.size();
        }
    }

    public void resetShuffle() {
        isShuffled = false;
        shuffledSongs.clear();
    }

    public boolean isShuffled() {
        return isShuffled;
    }

    public void resetRepeat() {
        isRepeat = false;
        repeatSong = null;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public int getOriginalIndex(Song song) {
        if (song == null) return -1;
        return originalSongs.indexOf(song);
    }

    public void setAsTop(int position) {
        if (position < 0 || position >= originalSongs.size()) {
            return;
        }
        Song song = originalSongs.remove(position);
        originalSongs.add(0, song);
        if (isShuffled) {
            shuffledSongs.remove(song);
            shuffledSongs.add(0, song);
        }
    }

    public void setCurrentSongAsTop(Song song) {
        if (song == null) return;
        int position = originalSongs.indexOf(song);
        if (position != -1) {
            setAsTop(position);
        }
    }

    public Song getRepeatSong() {
        return repeatSong;
    }

    public List<Song> getOriginalSongs() {
        return new ArrayList<>(originalSongs);
    }

    public List<Song> getCurrentSongs() {
        if (isShuffled) {
            return new ArrayList<>(shuffledSongs);
        } else {
            return new ArrayList<>(originalSongs);
        }
    }
}
