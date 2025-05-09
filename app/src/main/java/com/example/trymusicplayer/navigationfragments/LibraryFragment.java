package com.example.trymusicplayer.navigationfragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.example.trymusicplayer.R;

public class LibraryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        ImageButton queueButton = view.findViewById(R.id.queue_button);
        ImageButton playlistButton = view.findViewById(R.id.playlist_button);

        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueueLibraryFragment queueFragment = new QueueLibraryFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_view, queueFragment).commit();
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistLibraryFragment playlistFragment = new PlaylistLibraryFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_view, playlistFragment).commit();
            }
        });
        return view;
    }
}
