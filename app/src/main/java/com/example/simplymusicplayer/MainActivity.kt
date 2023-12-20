package com.example.musicplaylist

import PlaylistAdapter
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.recyclerview.widget.ListAdapter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var playlists = getSamplePlaylists()
    private lateinit var adapter: PlaylistAdapter


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
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.i("MainActivity", "onCreate: MainActivity created")

        recyclerView = findViewById(R.id.recyclerView)
        loadPlaylists()
        adapter = PlaylistAdapter(playlists) { playlist -> openPlaylistActivity(playlist) }
        recyclerView.adapter = adapter
        setupRecyclerView()
        //loadPlaylists()

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun addPlaylist(playlist: Playlist) {
        playlists.add(playlist)
        adapter.notifyDataSetChanged()
        savePlaylists(playlists)
        Log.i("MainActivity", "Added new playlist: ${playlist.name}")
    }

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
    private fun getSamplePlaylists(): MutableList<Playlist> {
        val samplePlaylists = mutableListOf<Playlist>(
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
        Log.i("MainActivity", "Created sample playlists")
        return samplePlaylists
    }

    private val sharedPreferences by lazy {
        getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }
    private fun savePlaylists(playlists: MutableList<Playlist>) {
        val playlistsJson = Gson().toJson(playlists)
        sharedPreferences.edit().putString("playlists", playlistsJson).apply()
    }
    inline fun <reified T> Gson.fromJson(json: String): T {
        val type = object : TypeToken<T>() {}.type
        return this.fromJson(json, type)
    }

    private fun loadPlaylists(): MutableList<Playlist> {
        val playlistsJson = sharedPreferences.getString("playlists", null)
        val loadedPlaylists: MutableList<Playlist> = Gson().fromJson(playlistsJson ?: "[]")
        playlists.clear()
        playlists.addAll(loadedPlaylists)
        return playlists
    }
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
}
class PlaylistDiffCallback(
    private val oldList: List<Playlist>,
    private val newList: List<Playlist>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // В данном случае считаем, что плейлисты считаются одинаковыми, если их названия одинаковы
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}