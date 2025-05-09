package com.example.trymusicplayer.playlist;

import java.io.Serializable;

public class Playlist implements Serializable {
    private int id;
    private String name;

    // Constructor, getters, and setters
    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
