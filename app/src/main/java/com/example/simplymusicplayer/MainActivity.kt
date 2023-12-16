package com.example.musicplaylist

import PlaylistAdapter
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplymusicplayer.MusicTrack
import com.example.simplymusicplayer.R
import com.example.simplymusicplayer.getSampleTracksForPlaylistClassic
import com.example.simplymusicplayer.getSampleTracksForPlaylistGoodMood
import com.example.simplymusicplayer.getSampleTracksForPlaylistSad
import com.example.simplymusicplayer.getSampleTracksForPlaylistSleeping
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var playlists = getSamplePlaylists()
    private lateinit var adapter: PlaylistAdapter
    private val mediaPlayerManager = MediaPlayerManager.getInstance()
    private lateinit var nowPlayingTitleTextView: TextView
    private lateinit var nowPlayingArtistTextView: TextView
    private lateinit var nowPlayingCoverImageView: ImageView


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_playlist -> {
                showCreatePlaylistDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playlist_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.i("MainActivity", "onCreate: MainActivity created")

        recyclerView = findViewById(R.id.recyclerView)
        //searchView = findViewById(R.id.searchView)
        adapter = PlaylistAdapter(playlists) { playlist -> openPlaylistActivity(playlist) }
        recyclerView.adapter = adapter
        setupRecyclerView()
        /* nowPlayingTitleTextView = findViewById(R.id.nowPlayingTitleTextView)
        nowPlayingArtistTextView = findViewById(R.id.nowPlayingArtistTextView)
        nowPlayingCoverImageView = findViewById(R.id.nowPlayingCoverImageView)*/


        // Handle searchView changes (you may want to filter the playlists here)
        /* searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search text changes
                return true
            }
        })*/

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
            Playlist(
                "Мелодии для сна",
                R.drawable.playlist_for_sleeping,
                tracks = getSampleTracksForPlaylistSleeping()
            ),
            Playlist(
                "Для хорошего настроения",
                R.drawable.playlist_for_goodmood,
                tracks = getSampleTracksForPlaylistGoodMood()
            ),
            Playlist(
                "Когда грустно",
                R.drawable.playlist_when_ur_sad,
                tracks = getSampleTracksForPlaylistSad()
            ),
            Playlist(
                "Классика",
                R.drawable.playlist_for_classic,
                tracks = getSampleTracksForPlaylistClassic()
            ),
        )
    }
    /*override fun updateNowPlayingInfo(track: MusicTrack) {
        nowPlayingTitleTextView.text = track.title
        nowPlayingArtistTextView.text = track.artist

    }*/

    private fun showCreatePlaylistDialog() {
        val dialogView = layoutInflater.inflate(R.layout.to_add_playlist, null)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Создать новый плейлист")
            .setView(dialogView)
            .setPositiveButton("Создать") { _, _ ->
                val playlistName =
                    dialogView.findViewById<EditText>(R.id.editTextPlaylistName).text.toString()
                if (playlistName.isNotEmpty()) {
                    val newPlaylist =
                        Playlist(playlistName, R.drawable.empty_image, mutableListOf())
                    addPlaylist(newPlaylist)
                } else {
                    Toast.makeText(this, "Введите название плейлиста", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun addPlaylist(playlist: Playlist) {
        val updatedList = playlists.toMutableList()
        updatedList.add(playlist)
        playlists = updatedList

        // Уведомьте адаптер о вставке нового элемента
        adapter.notifyItemInserted(updatedList.size - 1)
    }
}