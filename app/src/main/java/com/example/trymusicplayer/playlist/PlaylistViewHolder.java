package com.example.trymusicplayer.playlist;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trymusicplayer.R;

public class PlaylistViewHolder extends RecyclerView.ViewHolder {


    private TextView playlistTitle;
    private ImageButton playlistMenu;

    public PlaylistViewHolder(View itemView) {
        super(itemView);

        playlistTitle = itemView.findViewById(R.id.playlist_title);
    }


    public TextView getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(TextView playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public ImageButton getPlaylistMenu() {
        return playlistMenu;
    }

    public void setPlaylistMenu(ImageButton playlistMenu) {
        this.playlistMenu = playlistMenu;
    }
}
