package com.example.trymusicplayer.playlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trymusicplayer.navigationfragments.PlaylistLibraryShowFragment;
import com.example.trymusicplayer.R;
import com.example.trymusicplayer.OnPlaylistItemClick;

import java.util.List;

public class PlaylistRecyclerViewAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private OnPlaylistItemClick playlistItemClickListener;

    public PlaylistRecyclerViewAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }


    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final PlaylistViewHolder playlist = new PlaylistViewHolder((LayoutInflater.from(context)
                .inflate(R.layout.playlist_item, parent, false)));
        //        View view = LayoutInflater.from(context).inflate(R.layout.recycler_songs, parent, false);
        //        return new PlaylistViewHolder(view);

        playlist.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = playlist.getAdapterPosition();
                playlistItemClickListener.onItemClick(playlists.get(position));
            }
        });
        return playlist;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.getPlaylistTitle().setText(playlist.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistLibraryShowFragment fragment = new PlaylistLibraryShowFragment();

                Bundle args = new Bundle();
                args.putString("playlistTitle", playlist.getName());
                fragment.setArguments(args);

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.playlist_library_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
