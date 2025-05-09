package com.example.trymusicplayer.musicmediaplayer;

import static com.example.trymusicplayer.musicmediaplayer.MusicPlayerActivity.currentSong;
import static com.example.trymusicplayer.musicmediaplayer.MusicPlayerActivity.songsList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trymusicplayer.OnSongItemClick;
import com.example.trymusicplayer.R;
import com.example.trymusicplayer.database.PlaylistDBHelper;
import com.example.trymusicplayer.playlist.Playlist;
import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

import java.io.IOException;
import java.util.List;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicViewHolder> {

    private Context context;
    private SongQueue songList;
    private OnSongItemClick songItemClickListener;
    private PlaylistDBHelper playlistDBHelper;
    private boolean isInPlaylist;
    private String currentPlaylistTitle;

    public MusicRecyclerViewAdapter(Context context, SongQueue songList) {
        this.context = context;
        this.songList = songList;
        this.playlistDBHelper = new PlaylistDBHelper(context);
        this.isInPlaylist = false;
    }

    public void setInPlaylist(boolean inPlaylist, String playlistTitle) {
        this.isInPlaylist = inPlaylist;
        this.currentPlaylistTitle = playlistTitle;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_songs, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        try {
            Song song = songList.get(position);
            if (song != null) {
                holder.bind(song);

                boolean isCurrentSong = MyMediaPlayer.currentIndex == position;
                holder.bind(song, isCurrentSong);

                holder.itemView.setOnClickListener(v -> {
                    try {
                        int previousIndex = MyMediaPlayer.currentIndex;
                        MyMediaPlayer.currentIndex = position;

                        notifyItemChanged(previousIndex);
                        notifyItemChanged(position);

                        MyMediaPlayer.getInstance().reset();
                        Intent intent = new Intent(context, MusicPlayerActivity.class);
                        intent.putExtra("LIST", songList);
                        intent.putExtra("CURRENT_POSITION", MyMediaPlayer.getInstance().getCurrentPosition());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error playing song", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.getSongMenu().setOnClickListener(v -> showPopupMenu(v, holder, position));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error loading song", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.getSize() : 0;
    }

    private void showPopupMenu(View view, MusicViewHolder holder, int position) {
        try {
            PopupMenu popupMenu = new PopupMenu(context, view);
            
            // Inflate the appropriate menu based on context
            if (isInPlaylist) {
                popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu_playlist_song, popupMenu.getMenu());
            } else {
                popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu_musicplaying, popupMenu.getMenu());
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                Song currentSong = songList.get(position);
                if (currentSong != null) {
                    if (id == R.id.add_to_queue) {
                        songsList.enqueue(currentSong);
                        Toast.makeText(context, "Added to Queue", Toast.LENGTH_SHORT).show();
                    } else if (id == R.id.add_to_playlist) {
                        showPlaylistDialog(currentSong);
                    } else if (id == R.id.remove_from_playlist) {
                        removeFromPlaylist(currentSong);
                    }
                }
                return true;
            });
            popupMenu.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error showing menu", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromPlaylist(Song song) {
        try {
            if (currentPlaylistTitle != null) {
                Playlist playlist = playlistDBHelper.getPlaylistByTitle(currentPlaylistTitle);
                if (playlist != null) {
                    SQLiteDatabase db = playlistDBHelper.getWritableDatabase();
                    playlistDBHelper.removeSongFromPlaylist(song.getTitle(), song.getArtist(), playlist.getId(), db);
                    db.close();

                    // Remove from current view
                    int position = songList.getOriginalIndex(song);
                    if (position != -1) {
                        songList.getOriginalSongs().remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, songList.getSize());
                    }

                    Toast.makeText(context, "Removed from playlist", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error removing song from playlist", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPlaylistDialog(Song song) {
        try {
            List<Playlist> playlists = playlistDBHelper.getAllPlaylists();
            if (playlists != null && !playlists.isEmpty()) {
                String[] playlistNames = new String[playlists.size()];
                for (int i = 0; i < playlists.size(); i++) {
                    playlistNames[i] = playlists.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select a Playlist");
                builder.setItems(playlistNames, (dialog, which) -> {
                    try {
                        Playlist selectedPlaylist = playlists.get(which);
                        SQLiteDatabase db = playlistDBHelper.getWritableDatabase();
                        playlistDBHelper.addSongToPlaylist(
                            song.getTitle(),
                            song.getArtist(),
                            song.getAlbum(),
                            song.getDuration(),
                            song.getPath(),
                            selectedPlaylist.getId(),
                            db
                        );
                        db.close();
                        Toast.makeText(context, "Added to " + selectedPlaylist.getName(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error adding to playlist", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(context, "No playlists available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error showing playlists", Toast.LENGTH_SHORT).show();
        }
    }
}