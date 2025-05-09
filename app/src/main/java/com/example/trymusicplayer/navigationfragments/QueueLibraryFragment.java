package com.example.trymusicplayer.navigationfragments;

import static com.example.trymusicplayer.MainActivity.songsList;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trymusicplayer.R;
import com.example.trymusicplayer.musicmediaplayer.MusicRecyclerViewAdapter;
import com.example.trymusicplayer.musicmediaplayer.MyMediaPlayer;
import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

public class QueueLibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyQueueText;
    private SongQueue queue;
    private MusicRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queue_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            recyclerView = view.findViewById(R.id.song_recyclerview);
            emptyQueueText = view.findViewById(R.id.empty_queue_text);

            if (songsList != null && songsList.getSize() > 0) {
                queue = songsList;
                recyclerView.setVisibility(View.VISIBLE);
                emptyQueueText.setVisibility(View.GONE);

                adapter = new MusicRecyclerViewAdapter(requireContext(), queue);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            } else {
                showEmptyQueue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading queue", Toast.LENGTH_SHORT).show();
            showEmptyQueue();
        }
    }

    private void showEmptyQueue() {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (emptyQueueText != null) {
            emptyQueueText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateQueue();
    }

    private void updateQueue() {
        try {
            if (songsList != null && songsList.getSize() > 0) {
                queue = songsList;
                if (recyclerView != null) {
                    if (adapter == null) {
                        adapter = new MusicRecyclerViewAdapter(requireContext(), queue);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    if (emptyQueueText != null) {
                        emptyQueueText.setVisibility(View.GONE);
                    }
                }
            } else {
                showEmptyQueue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error updating queue", Toast.LENGTH_SHORT).show();
            showEmptyQueue();
        }
    }
}