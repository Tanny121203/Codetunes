package com.example.trymusicplayer.musicmediaplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trymusicplayer.MainActivity;
import com.example.trymusicplayer.R;
import com.example.trymusicplayer.database.PlaylistDBHelper;
import com.example.trymusicplayer.playlist.Playlist;
import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initializeViews();
        initializePlaylistHelper();
        initializeSongQueue();
        setResourcesWithMusic();
        setupUiListeners();
        updateUiThread();
        setupSeekBarListener();

        int currentPosition = getIntent().getIntExtra("CURRENT_POSITION", 0);
        if (currentPosition > 0) {
            mediaPlayer.seekTo(currentPosition);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
        }
    }


    /*
     * Initialize Views for interactive layouts
     */
    private void initializeViews() {
        titleTv = findViewById(R.id.track_title);
        artistTrack = findViewById(R.id.track_artist);
        albumName = findViewById(R.id.track_album);
        currentTimeTv = findViewById(R.id.start_duration);
        totalTimeTv = findViewById(R.id.end_duration);
        seekBar = findViewById(R.id.duration_seekbar);
        pausePlay = findViewById(R.id.play_track);
        nextBtn = findViewById(R.id.skip_track);
        previousBtn = findViewById(R.id.previous_track);
        backBtn = findViewById(R.id.arrow_back);
        shuffleBtn = findViewById(R.id.shuffle_track);
        repeatBtn = findViewById(R.id.repeat_track);
        optionBtn = findViewById(R.id.option_button);

        titleTv.setSelected(true);
    }


    /*
     * Initialize Database for Playlist
     */
    private void initializePlaylistHelper() {
        playlistDBHelper = new PlaylistDBHelper(this);
    }


    /*
     * Initialize SongQueue from MusicRecyclerView Adapter
     */
    private void initializeSongQueue() {
        songsList = (SongQueue) getIntent().getSerializableExtra("LIST");
    }


    /**
     * Set up User Interface Listeners
     */
    private void setupUiListeners() {
        pausePlay.setOnClickListener(view -> pausePlay());
        nextBtn.setOnClickListener(view -> playNextSong());
        previousBtn.setOnClickListener(view -> playPreviousSong());
        shuffleBtn.setOnClickListener(view -> shuffleTrack());
        repeatBtn.setOnClickListener(view -> repeatTrack());
        backBtn.setOnClickListener(view -> exitActivity());
        optionBtn.setOnClickListener(view -> showPopupMenu(view));
    }


    /**
     * Getting and Setting the songs
     */
    private void setResourcesWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        artistTrack.setText(currentSong.getArtist());
        albumName.setText(currentSong.getAlbum());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        // Send broadcast to update miniplayer
        Intent intent = new Intent("com.example.trymusicplayer.UPDATE_MINI_PLAYER");
        intent.putExtra("CURRENT_SONG", currentSong);
        sendBroadcast(intent);

        if (!mediaPlayer.isPlaying()) {
            playMusic();
        }
    }

    /**
     * Updates the Ui threads
     */
    private void updateUiThread() {
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));
                    updatePlayPauseButtonIcon();
                }
                new Handler().postDelayed(this, 100);
            }
        });
    }


    /**
     * Update the Play and Pause Button
     */
    private void updatePlayPauseButtonIcon() {
        pausePlay.setImageResource(mediaPlayer.isPlaying()
                ? R.drawable.pause_track
                : R.drawable.play_track);
    }


    /**
     * The seekbar where it displays an interactive duration line of the song
     */
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


    /**
     * It plays the songs
     */
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

    /**
     * It plays the succeeding song
     */
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

    /**
     * It plays the previous song from the current one
     */
    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0) {
            return;
        }
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    /**
     * Pause and Play the song
     */
    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }


    /**
     * Shuffles the current queue
     * When activating the shuffle mode, the succeeding songs are shuffled up until the user disables the mode
     * When the mode is disabled, the current song will be the current queue and the succeeding will be from
     * what was in the queue (Original Queue)
     */
    private void shuffleTrack() {
        if (songsList.isShuffled()) {
            songsList.resetShuffle();
            MyMediaPlayer.currentIndex = songsList.getOriginalIndex(currentSong);
            Toast.makeText(MusicPlayerActivity.this, "Shuffle Off", Toast.LENGTH_SHORT).show();
        } else {
            songsList.shuffle();
            Toast.makeText(MusicPlayerActivity.this, "Shuffle On", Toast.LENGTH_SHORT).show();
        }
        updateShuffleButtonIcon();
    }


    /**
     * Updates the Shuffle Button Icon
     */
    private void updateShuffleButtonIcon() {
        shuffleBtn.setImageResource(songsList.isShuffled() ? R.drawable.shuffle_on : R.drawable.shuffle_off);
    }


    /**
     * Repeat the current song
     * When activating the repeat mode, it repeats the current song up until the user disables the mode
     */
    private void repeatTrack() {
        if (songsList.isRepeat()) {
            songsList.resetRepeat();
            MyMediaPlayer.currentIndex = songsList.getOriginalIndex(currentSong);
            Toast.makeText(MusicPlayerActivity.this, "Repeat Off", Toast.LENGTH_SHORT).show();
        } else {
            songsList.repeat(currentSong);
            Toast.makeText(MusicPlayerActivity.this, "Repeat On", Toast.LENGTH_SHORT).show();
        }
        updateRepeatButtonIcon();
    }


    /**
     * Updates the Repeat Button Icon
     */
    private void updateRepeatButtonIcon() {
        repeatBtn.setImageResource(songsList.isRepeat() ? R.drawable.repeat_track_on : R.drawable.repeat_track);
    }


    /**
     * Convert song duration to the used to interface (00:00)
     * @param duration - Duration of the Song
     * @return - Formatted duration
     */
    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    /**
     * Show the user options through a popup menu
     * @param view -
     */
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu_musicplaying, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.add_to_queue) {
                    songsList.enqueue(currentSong);
                    Toast.makeText(MusicPlayerActivity.this, "Added to Queue", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.add_to_playlist) {
                    showPlaylistDialog(currentSong);
                }
                return true;
            }
        });
        popupMenu.show();
    }


    /**
     * Add song to a Playlist from the Database
     * @param song Song to be added
     */
    private void showPlaylistDialog(Song song) {
        List<Playlist> playlists = playlistDBHelper.getAllPlaylists();

        String[] playlistNames = new String[playlists.size()];
        for (int i = 0; i < playlists.size(); i++) {
            playlistNames[i] = playlists.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayerActivity.this);
        builder.setTitle("Select a Playlist");
        builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Playlist selectedPlaylist = playlists.get(which);

                SQLiteDatabase db = playlistDBHelper.getWritableDatabase();
                playlistDBHelper.addSongToPlaylist(song.getTitle(), song.getArtist(), song.getAlbum(), song.getDuration(), song.getPath(), selectedPlaylist.getId(), db);
                db.close();

                Toast.makeText(MusicPlayerActivity.this, "Added to " + selectedPlaylist.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void exitActivity() {
        finish();
    }

}
