package com.example.musicplaylist

import PlaylistAdapter
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplymusicplayer.MusicTrack
import com.example.simplymusicplayer.R
import com.example.simplymusicplayer.getSampleTracksForPlaylistClassic
import com.example.simplymusicplayer.getSampleTracksForPlaylistGoodMood
import com.example.simplymusicplayer.getSampleTracksForPlaylistSad
import com.example.simplymusicplayer.getSampleTracksForPlaylistSleeping


class MainActivity : AppCompatActivity(), MediaPlayerManager.NowPlayingListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private val playlists = getSamplePlaylists()
    private val mediaPlayerManager = MediaPlayerManager.getInstance()
    private lateinit var nowPlayingTitleTextView: TextView
    private lateinit var nowPlayingArtistTextView: TextView
    private lateinit var nowPlayingCoverImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.i("MainActivity", "onCreate: MainActivity created")

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        nowPlayingTitleTextView = findViewById(R.id.nowPlayingTitleTextView)
        nowPlayingArtistTextView = findViewById(R.id.nowPlayingArtistTextView)
        nowPlayingCoverImageView = findViewById(R.id.nowPlayingCoverImageView)


        setupRecyclerView()

        // Handle searchView changes (you may want to filter the playlists here)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search text changes
                return true
            }
        })

    }
        //mediaPlayerManager.getCurrentTrack()?.let { yourListener.onTrackChanged(it) }

        // Добавление слушателя
        //mediaPlayerManager.addOnTrackChangedListener(yourListener))

    private fun setupRecyclerView() {
        val adapter = PlaylistAdapter(playlists) { playlist -> openPlaylistActivity(playlist) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }


    // Метод для открытия активности с треками выбранного плейлиста
    private fun openPlaylistActivity(playlist: Playlist) {
        Log.i("MainActivity", "Opening PlaylistActivity for playlist: ${playlist.name}")
        val intent = Intent(this, PlaylistActivity::class.java)
        intent.putExtra("playlist", playlist)
        startActivity(intent)
    }

    // Замените этот код на ваш фактический метод получения данных о плейлистах
    private fun getSamplePlaylists(): List<Playlist> {
        return listOf(
            Playlist("Мелодии для сна",  R.drawable.playlist_for_sleeping, tracks = getSampleTracksForPlaylistSleeping()),
            Playlist("Для хорошего настроения",  R.drawable.playlist_for_goodmood, tracks = getSampleTracksForPlaylistGoodMood()),
            Playlist("Когда грустно",  R.drawable.playlist_when_ur_sad, tracks = getSampleTracksForPlaylistSad()),
            Playlist("Классика",  R.drawable.playlist_for_classic, tracks = getSampleTracksForPlaylistClassic()),
        )
    }
    override fun updateNowPlayingInfo(track: MusicTrack) {
        nowPlayingTitleTextView.text = track.title
        nowPlayingArtistTextView.text = track.artist
        // Дополнительные действия, которые могут быть необходимы для обновления интерфейса
    }
}