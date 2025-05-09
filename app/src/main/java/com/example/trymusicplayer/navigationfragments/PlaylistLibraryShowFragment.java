package com.example.trymusicplayer.navigationfragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trymusicplayer.R;
import com.example.trymusicplayer.database.PlaylistDBHelper;
import com.example.trymusicplayer.musicmediaplayer.MusicPlayerActivity;
import com.example.trymusicplayer.musicmediaplayer.MusicRecyclerViewAdapter;
import com.example.trymusicplayer.playlist.Playlist;
import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

import java.util.List;

public class PlaylistLibraryShowFragment extends Fragment {

    private ImageButton backBtn;
    private TextView myPlaylist;
    private PlaylistDBHelper playlistDBHelper;
    private ImageButton playPlaylist, shufflePlaylist;
    private SongQueue songQueue;
    private RecyclerView recyclerView;
    private TextView emptyPlaylistText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_library_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Initialize database helper
            playlistDBHelper = new PlaylistDBHelper(requireContext());

            // Initialize views
            recyclerView = view.findViewById(R.id.song_recyclerview);
            myPlaylist = view.findViewById(R.id.playlist);
            emptyPlaylistText = view.findViewById(R.id.show_no_songs);
            playPlaylist = view.findViewById(R.id.play_playlist);
            shufflePlaylist = view.findViewById(R.id.shuffle_playlist);
            backBtn = view.findViewById(R.id.arrow_back);

            // Set up back button
            backBtn.setOnClickListener(v -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });

            // Get playlist title from arguments
            Bundle args = getArguments();
            if (args != null) {
                String playlistTitle = args.getString("playlistTitle");
                if (playlistTitle != null) {
                    myPlaylist.setText(playlistTitle);
                    loadPlaylistSongs(playlistTitle);
                } else {
                    showEmptyPlaylist();
                }
            } else {
                showEmptyPlaylist();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading playlist", Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        }
    }

    private void loadPlaylistSongs(String playlistTitle) {
        try {
            Playlist playlist = playlistDBHelper.getPlaylistByTitle(playlistTitle);
            
            if (playlist != null) {
                List<Song> songs = playlistDBHelper.getSongsInPlaylist(playlist.getId());
                
                if (songs != null && !songs.isEmpty()) {
                    songQueue = new SongQueue();
                    songQueue.setSongs(songs);
                    
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyPlaylistText.setVisibility(View.GONE);
                    
                    MusicRecyclerViewAdapter adapter = new MusicRecyclerViewAdapter(requireContext(), songQueue);
                    adapter.setInPlaylist(true, playlistTitle);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    
                    setupPlaylistButtons();
                } else {
                    showEmptyPlaylist();
                }
            } else {
                showEmptyPlaylist();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading songs", Toast.LENGTH_SHORT).show();
            showEmptyPlaylist();
        }
    }

    private void setupPlaylistButtons() {
        if (playPlaylist != null && shufflePlaylist != null) {
            playPlaylist.setOnClickListener(v -> {
                if (songQueue != null && songQueue.getSize() > 0) {
                    try {
                        Intent intent = new Intent(requireContext(), MusicPlayerActivity.class);
                        intent.putExtra("LIST", songQueue);
                        intent.putExtra("CURRENT_POSITION", 0);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error playing playlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Playlist is empty", Toast.LENGTH_SHORT).show();
                }
            });

            shufflePlaylist.setOnClickListener(v -> {
                if (songQueue != null && songQueue.getSize() > 0) {
                    try {
                        songQueue.shuffle();
                        Intent intent = new Intent(requireContext(), MusicPlayerActivity.class);
                        intent.putExtra("LIST", songQueue);
                        intent.putExtra("CURRENT_POSITION", 0);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error shuffling playlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Playlist is empty", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEmptyPlaylist() {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyPlaylistText != null) {
            emptyPlaylistText.setVisibility(View.VISIBLE);
        }
        if (playPlaylist != null) {
            playPlaylist.setEnabled(false);
        }
        if (shufflePlaylist != null) {
            shufflePlaylist.setEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (playlistDBHelper != null) {
            playlistDBHelper.close();
        }
    }
}