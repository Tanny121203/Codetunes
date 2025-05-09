package com.example.trymusicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.trymusicplayer.playlist.Playlist;
import com.example.trymusicplayer.songs.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Musicaofficial.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PLAYLISTS = "CREATE TABLE playlists (" +
            "playlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "playlist_name TEXT);";

    private static final String TABLE_SONGS = "CREATE TABLE songs (" +
            "song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "song_title TEXT," +
            "artist TEXT," +
            "album TEXT," +
            "duration TEXT," +
            "path TEXT," +
            "playlist_id INTEGER," +
            "FOREIGN KEY(playlist_id) REFERENCES playlists(playlist_id));";

    public PlaylistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_PLAYLISTS);
        db.execSQL(TABLE_SONGS);
        createFavoritePlaylist(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS songs");
        db.execSQL("DROP TABLE IF EXISTS playlists");
        onCreate(db);
    }

    public void addPlaylist(String playlistName, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("playlist_name", playlistName);
        db.insert("playlists", null, values);
    }

    public void addSongToPlaylist(String songTitle, String artist, String album, String duration, String path, int playlistId, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("song_title", songTitle);
        values.put("artist", artist);
        values.put("album", album);
        values.put("duration", duration);
        values.put("path", path);
        values.put("playlist_id", playlistId);
        db.insert("songs", null, values);
    }

    public void updatePlaylistName(int playlistId, String newName, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("playlist_name", newName);
        db.update("playlists", values, "playlist_id = ?", new String[]{String.valueOf(playlistId)});
    }

    public void deletePlaylist(int playlistId, SQLiteDatabase db) {
        db.delete("playlists", "playlist_id = ?", new String[]{String.valueOf(playlistId)});
        db.delete("songs", "playlist_id = ?", new String[]{String.valueOf(playlistId)}); // Also delete all songs in the playlist
    }

    // Method to query a playlist by its name
//    public int getPlaylistIdByName(String playlistName) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query("playlists", new String[]{"playlist_id"}, "playlist_name = ?", new String[]{playlistName}, null, null, null);
//        int playlistId = -1; // Default to -1 if not found
//        if (cursor.moveToFirst()) {
//            playlistId = cursor.getInt(cursor.getColumnIndex("playlist_id"));
//        }
//        cursor.close();
//        return playlistId;
//    }

    public String getPlaylistNameById(long playlistId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("playlists", new String[]{"playlist_name"}, "playlist_id = ?", new String[]{String.valueOf(playlistId)}, null, null, null);
        String playlistName = null;
        if (cursor.moveToFirst()) {
            playlistName = cursor.getString(cursor.getColumnIndex("playlist_name"));
        }
        cursor.close();
        return playlistName;
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("playlists", new String[]{"playlist_id", "playlist_name"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("playlist_id"));
                String name = cursor.getString(cursor.getColumnIndex("playlist_name"));
                playlists.add(new Playlist(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return playlists;
    }

    public Playlist getPlaylistByTitle(String playlistTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("playlists", new String[]{"playlist_id", "playlist_name"}, "playlist_name = ?",
                new String[]{playlistTitle}, null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("playlist_id"));
            String name = cursor.getString(cursor.getColumnIndex("playlist_name"));
            cursor.close();
            return new Playlist(id, name);
        } else {
            cursor.close();
            return null;
        }
    }


    public List<Song> getSongsInPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();

        // Query the database for songs
        String selectQuery = "SELECT song_title, artist, album, duration, path FROM songs WHERE playlist_id = " + playlistId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("song_title"));
                String artist = cursor.getString(cursor.getColumnIndex("artist"));
                String album = cursor.getString(cursor.getColumnIndex("album"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                String path = cursor.getString(cursor.getColumnIndex("path"));

                Song song = new Song(path, title, artist, album, duration);
                songs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return songs;
    }

    public void createFavoritePlaylist(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("playlist_name", "Favorite");
        db.insert("playlists", null, values);
    }

    public void removeSongFromPlaylist(String songTitle, String artist, int playlistId, SQLiteDatabase db) {
        db.delete("songs", "song_title = ? AND artist = ? AND playlist_id = ?",
                new String[]{songTitle, artist, String.valueOf(playlistId)});
    }

}
