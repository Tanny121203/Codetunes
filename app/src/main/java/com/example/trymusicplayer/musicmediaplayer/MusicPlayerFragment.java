package com.example.trymusicplayer.musicmediaplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.trymusicplayer.R;
import com.example.trymusicplayer.database.PlaylistDBHelper;
import com.example.trymusicplayer.playlist.Playlist;
import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicPlayerFragment extends Fragment {

    // UI Elements
    private TextView titleTv, artistTrack, albumName, currentTimeTv, totalTimeTv;
    private SeekBar seekBar;
    private ImageButton pausePlay, nextBtn, previousBtn, shuffleBtn, repeatBtn, backBtn, optionBtn;

    // Song and Player
    protected static SongQueue songsList;
    protected static Song currentSong;
    private MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    // Database Helper
    private PlaylistDBHelper playlistDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_music_player, container, false);

        initializeViews(view);
        initializePlaylistHelper();
        initializeSongQueue();
        setResourcesWithMusic();
        setupUiListeners();
        updateUiThread();
        setupSeekBarListener();

        return view;
    }

    private void initializeViews(View view) {
        titleTv = view.findViewById(R.id.track_title);
        artistTrack = view.findViewById(R.id.track_artist);
        albumName = view.findViewById(R.id.track_album);
        currentTimeTv = view.findViewById(R.id.start_duration);
        totalTimeTv = view.findViewById(R.id.end_duration);
        seekBar = view.findViewById(R.id.duration_seekbar);
        pausePlay = view.findViewById(R.id.play_track);
        nextBtn = view.findViewById(R.id.skip_track);
        previousBtn = view.findViewById(R.id.previous_track);
        backBtn = view.findViewById(R.id.arrow_back);
        shuffleBtn = view.findViewById(R.id.shuffle_track);
        repeatBtn = view.findViewById(R.id.repeat_track);
        optionBtn = view.findViewById(R.id.option_button);

        titleTv.setSelected(true);
    }

    private void initializePlaylistHelper() {
        playlistDBHelper = new PlaylistDBHelper(getContext());
    }

    private void initializeSongQueue() {
        songsList = (SongQueue) getArguments().getSerializable("LIST");
    }

    private void setupUiListeners() {
        pausePlay.setOnClickListener(view -> pausePlay());
        nextBtn.setOnClickListener(view -> playNextSong());
        previousBtn.setOnClickListener(view -> playPreviousSong());
        shuffleBtn.setOnClickListener(view -> shuffleTrack());
        repeatBtn.setOnClickListener(view -> repeatTrack());
        backBtn.setOnClickListener(view -> exitActivity());
        optionBtn.setOnClickListener(view -> showPopupMenu(view));
    }

    private void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        artistTrack.setText(currentSong.getArtist());
        albumName.setText(currentSong.getAlbum());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        playMusic();
    }

    private void updateUiThread() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));
                                if (mediaPlayer.isPlaying()) {
                                    pausePlay.setImageResource(R.drawable.pause_track);
                                } else {
                                    pausePlay.setImageResource(R.drawable.play_track);
                                }
                            }
                            new Handler().postDelayed(this, 100);
                        }
                    });
                }
            }
        }, 100);
    }

    private void setupSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }
        });
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        if (songsList.isRepeat()) {
            mediaPlayer.reset();
            setResourcesWithMusic();
        } else {
            if (MyMediaPlayer.currentIndex < songsList.getSize() - 1) {
                MyMediaPlayer.currentIndex++;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                setResourcesWithMusic();
            } else {
                mediaPlayer.stop();
            }
        }
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0) {
            return;
        }
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    private void shuffleTrack() {
        if (songsList.isShuffled()) {
            songsList.resetShuffle();
            MyMediaPlayer.currentIndex = songsList.getOriginalIndex(currentSong);
            shuffleBtn.setImageResource(R.drawable.shuffle_off);
            Toast.makeText(getContext(), "Shuffle Off", Toast.LENGTH_SHORT).show();
        } else {
            songsList.shuffle();
            shuffleBtn.setImageResource(R.drawable.shuffle_on);
            Toast.makeText(getContext(), "Shuffle On", Toast.LENGTH_SHORT).show();
        }
    }

    private void repeatTrack() {
        if (songsList.isRepeat()) {
            songsList.resetRepeat();
            MyMediaPlayer.currentIndex = songsList.getOriginalIndex(currentSong);
            repeatBtn.setImageResource(R.drawable.repeat_track);
            Toast.makeText(getContext(), "Repeat Off", Toast.LENGTH_SHORT).show();
        } else {
            songsList.repeat(currentSong);
            repeatBtn.setImageResource(R.drawable.repeat_track_on);
            Toast.makeText(getContext(), "Repeat On", Toast.LENGTH_SHORT).show();
        }
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu_musicplaying, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.add_to_queue) {
                    songsList.enqueue(currentSong);
                    Toast.makeText(getContext(), "Added to Queue", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.add_to_playlist) {
                    showPlaylistDialog(currentSong);
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void showPlaylistDialog(Song song) {
        List<Playlist> playlists = playlistDBHelper.getAllPlaylists();

        String[] playlistNames = new String[playlists.size()];
        for (int i = 0; i < playlists.size(); i++) {
            playlistNames[i] = playlists.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Playlist");
        builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Playlist selectedPlaylist = playlists.get(which);

                SQLiteDatabase db = playlistDBHelper.getWritableDatabase();
                playlistDBHelper.addSongToPlaylist(song.getTitle(), song.getArtist(), song.getAlbum(), song.getDuration(), song.getPath(), selectedPlaylist.getId(), db);
                db.close();

                Toast.makeText(getContext(), "Added to " + selectedPlaylist.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void exitActivity() {
        // Instead of finishing the activity, you can navigate back or perform any other action
        // based on your app's requirements
    }
}
