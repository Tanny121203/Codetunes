package com.example.trymusicplayer.navigationfragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trymusicplayer.R;
import com.example.trymusicplayer.database.PlaylistDBHelper;
import com.example.trymusicplayer.playlist.Playlist;
import com.example.trymusicplayer.playlist.PlaylistRecyclerViewAdapter;

import java.util.List;


public class PlaylistLibraryFragment extends Fragment {
    private RecyclerView recyclerView;
    private PlaylistRecyclerViewAdapter adapter;
    private PlaylistDBHelper playlistDBHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist_library, container, false);
        recyclerView = view.findViewById(R.id.playlist_recyclerview);
        playlistDBHelper = new PlaylistDBHelper(getContext());

        List<Playlist> playlists = playlistDBHelper.getAllPlaylists();
        adapter = new PlaylistRecyclerViewAdapter(getContext(), playlists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}