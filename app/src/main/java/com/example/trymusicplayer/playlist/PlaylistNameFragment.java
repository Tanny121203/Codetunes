package com.example.trymusicplayer.playlist;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.trymusicplayer.R;
import com.example.trymusicplayer.database.PlaylistDBHelper;
import com.example.trymusicplayer.navigationfragments.PlaylistLibraryShowFragment;
import com.example.trymusicplayer.songs.Song;


import java.util.List;

public class PlaylistNameFragment extends Fragment {

    private EditText playlistNameEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_playlist_name_acivity, container, false);

        playlistNameEditText = view.findViewById(R.id.playlist_name_input);
        Button saveButton = view.findViewById(R.id.create_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = playlistNameEditText.getText().toString();
                savePlaylist(playlistName);
            }
        });
        return view;
    }

    private void savePlaylist(String playlistName) {
        PlaylistDBHelper playlistDBHelper = new PlaylistDBHelper(getContext());
        SQLiteDatabase db = playlistDBHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("playlist_name", playlistName);
            long playlistId = db.insert("playlists", null, values);

            // Check if playlist was added successfully
            if (playlistId == -1) {
                throw new Exception("Failed to insert playlist");
            }

            db.setTransactionSuccessful();
            String addedPlaylistName = String.valueOf(playlistDBHelper.getPlaylistNameById(Long.parseLong(String.valueOf(playlistId))));
            Toast.makeText(getContext(), "Playlist " + addedPlaylistName + " created successfully!", Toast.LENGTH_LONG).show();

            getParentFragmentManager().popBackStack();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to add playlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}