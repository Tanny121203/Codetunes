package com.example.trymusicplayer.musicmediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trymusicplayer.R;
import com.example.trymusicplayer.songs.Song;

import java.io.IOException;

public class MusicViewHolder extends RecyclerView.ViewHolder {

    private TextView songTitle, songArtist, songAlbum;
    private ImageView songImage, circleSeparator;
    private ImageButton songMenu;
    private Context context;

    public MusicViewHolder(View itemView) {
        super(itemView);

        //INITIALIZE
        songTitle = itemView.findViewById(R.id.song_title);
        songArtist = itemView.findViewById(R.id.song_artist);
        songImage = itemView.findViewById(R.id.icon_view);
        songAlbum = itemView.findViewById(R.id.song_album);
        circleSeparator = itemView.findViewById(R.id.circle_separator);
        songMenu = itemView.findViewById(R.id.song_menu);
        context = itemView.getContext();

    }

    public void bind(Song song) {
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
        songAlbum.setText(song.getAlbum());
        // Set other song details
    }

    public void bind(Song song, boolean isCurrentSong) throws IOException {
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
        songAlbum.setText(song.getAlbum());
        byte[] image = getAlbumArt(song.getPath());
        if (image != null) {
            Glide.with(context).asBitmap()
                    .load(image)
                    .placeholder(R.drawable.loading_logo) // Placeholder image while loading
                    .error(R.drawable.loading_logo) // Error image if loading fails
                    .into(songImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.loading_logo)
                    .into(songImage);
        }


        int textColor = isCurrentSong ? Color.parseColor("#8400B2") : Color.parseColor("#FFFFFFFF");
        songTitle.setTextColor(textColor);
        songArtist.setTextColor(textColor);
        songAlbum.setTextColor(textColor);
        circleSeparator.setColorFilter(textColor);
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri);
            return retriever.getEmbeddedPicture();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        } finally {
            retriever.release();
        }
    }

    public TextView getSongTitle() { return songTitle; }

    public void setSongTitle(TextView songTitle) { this.songTitle = songTitle; }

    public TextView getSongArtist() { return songArtist; }

    public void setSongArtist(TextView songArtist) { this.songArtist = songArtist; }

    public ImageView getSongImage() { return songImage; }

    public void setSongImage(ImageView songImage) { this.songImage = songImage; }

    public TextView getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(TextView songAlbum) {
        this.songAlbum = songAlbum;
    }

    public ImageView getCircleSeparator() {
        return circleSeparator;
    }

    public void setCircleSeparator(ImageView circleSeparator) {
        this.circleSeparator = circleSeparator;
    }

    public ImageButton getSongMenu() {
        return songMenu;
    }

    public void setSongMenu(ImageButton songMenu) {
        this.songMenu = songMenu;
    }


}