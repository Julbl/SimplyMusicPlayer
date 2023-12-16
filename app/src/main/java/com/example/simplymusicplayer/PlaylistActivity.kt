package com.example.musicplaylist

import TrackAdapter
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplymusicplayer.R
import java.io.Serializable

class PlaylistActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val mediaPlayerManager = MediaPlayerManager.getInstance()
    private lateinit var playPauseButton : ImageButton
    private lateinit var trackAdapter : TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_of_tracks)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val playlistTitleTextView: TextView = findViewById(R.id.albumTitleTextView)
        val recyclerViewTracks: RecyclerView = findViewById(R.id.recyclerViewTracks)

        // Получение данных о плейлисте из Intent
        val playlist = intent.getSerializableExtra("playlist") as Playlist

        // Настройка заголовка плейлиста
        playlistTitleTextView.text = playlist.name

        recyclerViewTracks.layoutManager = LinearLayoutManager(this)

        // Инициализация кнопки воспроизведения/паузы
        playPauseButton = findViewById(R.id.playPauseButton)
        playPauseButton.setImageResource(R.drawable.ic_play_arrow)

        // Создаем адаптер и присваиваем его переменной trackAdapter
        trackAdapter = TrackAdapter(this, playlist.tracks) { selectedTrack ->
            if (selectedTrack == mediaPlayerManager.getCurrentTrack()) {
                // Если трек уже воспроизводится, остановите его
                if (mediaPlayerManager.isPlaying()) {
                    mediaPlayerManager.stopTrack()
                } else {
                    // Иначе воспроизведите выбранный трек
                    mediaPlayerManager.playTrack(this, selectedTrack)
                }
            } else {
                // Если выбран новый трек, воспроизведите его
                mediaPlayerManager.playTrack(this, selectedTrack)
            }
            updatePlayPauseButton()
        }

        // Устанавливаем адаптер для RecyclerView
        recyclerViewTracks.adapter = trackAdapter

        // Обработчик нажатия на кнопку воспроизведения/паузы
        playPauseButton.setOnClickListener {
            val selectedTrack = mediaPlayerManager.getCurrentTrack()
            if (selectedTrack != null) {
                if (mediaPlayerManager.isPlaying()) {
                    mediaPlayerManager.stopTrack()
                } else {
                    mediaPlayerManager.playTrack(this, selectedTrack)
                }
                updatePlayPauseButton()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerManager.stopTrack()  // Останавливаем трек при уничтожении активности  // Освобождаем ресурсы MediaPlayer
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun updatePlayPauseButton() {
        runOnUiThread {
            val isPlaying = mediaPlayerManager.getCurrentMediaPlayer()?.isPlaying == true
            val resourceId = if (isPlaying) R.drawable.ic_play_arrow else R.drawable.ic_pause
            playPauseButton.setImageResource(resourceId)
        }
    }
}