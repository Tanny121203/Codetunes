package com.example.trymusicplayer.navigationfragments;

import static com.example.trymusicplayer.MainActivity.songsList;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.trymusicplayer.MainActivity;
import com.example.trymusicplayer.R;
import com.example.trymusicplayer.musicmediaplayer.MusicPlayerActivity;
import com.example.trymusicplayer.musicmediaplayer.MusicRecyclerViewAdapter;
import com.example.trymusicplayer.playlist.PlaylistNameFragment;
import com.example.trymusicplayer.songs.SongQueue;

import java.util.Collections;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    MusicRecyclerViewAdapter musicRecyclerViewAdapter;

    ImageButton favoriteBtn, optionBtn, shuffleBtn;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.song_recyclerview);
        recyclerView.setHasFixedSize(true);
        if (songsList != null && !songsList.isEmpty())
        {
            musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(this.getContext(), songsList);
            recyclerView.setAdapter(musicRecyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        }


        /*
         * Initialize option button and set the click listener
         */
        optionBtn = view.findViewById(R.id.option_button);
        optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        shuffleBtn = view.findViewById(R.id.shuffle_home);
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        favoriteBtn = view.findViewById(R.id.favorite_button);
//        favoriteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return view;
    }




    /**
     * Popup Menu when clicking the option button on the Home Page.
     * @param view
     */
    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu_home, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.create_new_playlist) {
                    PlaylistNameFragment playListFragment = new PlaylistNameFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.nav_home, playListFragment, "playListFragment")
                            .addToBackStack(null).commit();
                    return true;
                } else if (id == R.id.go_to_queue) {
                    // Handle "go to queue" action
                }
                return true;
            }
        });
        popupMenu.show();
    }
}