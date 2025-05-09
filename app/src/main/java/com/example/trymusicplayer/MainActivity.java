package com.example.trymusicplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trymusicplayer.databinding.ActivityMainBinding;
import com.example.trymusicplayer.musicmediaplayer.MusicPlayerActivity;
import com.example.trymusicplayer.musicmediaplayer.MyMediaPlayer;
import com.example.trymusicplayer.navigationfragments.HomeFragment;
import com.example.trymusicplayer.navigationfragments.LibraryFragment;
import com.example.trymusicplayer.songs.Song;
import com.example.trymusicplayer.songs.SongQueue;

public class MainActivity extends AppCompatActivity  {

    // Navigation
    private ActivityMainBinding binding;

    private static final int REQUEST_CODE = 1;
    public static SongQueue songsList;


    private TextView miniPlayerSongTitle;
    private TextView miniPlayerArtistName;
    private ImageView miniPlayerThumbnail;

    private ImageButton miniPlayerPlayPause;
    private RelativeLayout miniPlayerContainer;
    private MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.nav_library) {
                replaceFragment(new LibraryFragment());
            }
            return true;
        });


        // Check
        if (checkPermission()) {
            getAllSongs(this);
        } else {
            requestPermission();
        }

        miniPlayerSongTitle = findViewById(R.id.mini_player_song_title);
        miniPlayerArtistName = findViewById(R.id.mini_player_artist_name);
        miniPlayerThumbnail = findViewById(R.id.mini_player_thumbnail);
        miniPlayerPlayPause = findViewById(R.id.mini_player_playpause);
        miniPlayerContainer = findViewById(R.id.mini_player_container);

        // Enable marquee effect
        miniPlayerSongTitle.setSelected(true);
        miniPlayerArtistName.setSelected(true);

        miniPlayerContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
            intent.putExtra("LIST", songsList);
            startActivity(intent);
        });

        miniPlayerPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                miniPlayerPlayPause.setImageResource(R.drawable.mini_player_play);
            } else {
                mediaPlayer.start();
                miniPlayerPlayPause.setImageResource(R.drawable.mini_player_pause);
            }
        });

        // Register the receiver
        IntentFilter filter = new IntentFilter("com.example.trymusicplayer.UPDATE_MINI_PLAYER");
        registerReceiver(songUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(songUpdateReceiver);
    }


    // FOR BOTTOM NAVIGATION (FRAGMENTS_start)
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAllSongs(this);
            } else {
                Toast.makeText(this, "Permission denied to read your media files", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static SongQueue getAllSongs(Context context) {
        songsList = new SongQueue();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.DATA,      // Path
                MediaStore.Audio.Media.TITLE,     // Title
                MediaStore.Audio.Media.ARTIST,    // Artist
                MediaStore.Audio.Media.ALBUM,     // Album
                MediaStore.Audio.Media.DURATION   // Duration
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            while (cursor.moveToNext()) {
                String path = cursor.getString(pathIndex);
                String title = cursor.getString(titleIndex);
                String artist = cursor.getString(artistIndex);
                String album = cursor.getString(albumIndex);
                String duration = cursor.getString(durationIndex);

                Song song = new Song(path, title, artist, album, duration);
                songsList.enqueue(song);
            }
            cursor.close();
        }
        return songsList;
    }


    private BroadcastReceiver songUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Song currentSong = (Song) intent.getSerializableExtra("CURRENT_SONG");
            updateMiniPlayer(currentSong);
        }
    };

    public void updateMiniPlayer(Song currentSong) {
        miniPlayerSongTitle.setText(currentSong.getTitle());
        miniPlayerArtistName.setText(currentSong.getArtist());
        // Update thumbnail if needed
        miniPlayerContainer.setVisibility(View.VISIBLE);

    }
}