package com.example.musicplaylist

import TrackAdapter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplymusicplayer.MusicTrack
import com.example.simplymusicplayer.R
import java.io.Serializable

class PlaylistActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val mediaPlayerManager = MediaPlayerManager.getInstance()
    private lateinit var playPauseButton : ImageButton
    private lateinit var nextTrackButton : ImageButton
    private lateinit var prevTrackButton : ImageButton
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
        mediaPlayerManager.addOnTrackChangedListener(object : MediaPlayerManager.OnTrackChangedListener {
            override fun onTrackChanged(track: MusicTrack) {
                updateNowPlayingInfo()
            }

            override fun onTrackStopped() {
                updateNowPlayingInfo()
            }
        })
        // Инициализация кнопки воспроизведения/паузы
        playPauseButton = findViewById(R.id.playPauseButton)
        playPauseButton.setImageResource(R.drawable.ic_pause)

        // Создаем адаптер и присваиваем его переменной trackAdapter
        trackAdapter = TrackAdapter(this, playlist.tracks) { selectedTrack ->
            if (selectedTrack == mediaPlayerManager.getCurrentTrack()) {
                // Если трек уже воспроизводится, остановите его
                if (mediaPlayerManager.isPlaying()) {
                    mediaPlayerManager.stopTrack()
                } else {
                    // Иначе воспроизведите выбранный трек
                    mediaPlayerManager.playOrPauseTrack(this, selectedTrack)
                }
            } else {
                // Если выбран новый трек, воспроизведите его
                mediaPlayerManager.playOrPauseTrack(this, selectedTrack)
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
                    mediaPlayerManager.playOrPauseTrack(this, selectedTrack)
                }
                updatePlayPauseButton()
            }
        }

        prevTrackButton = findViewById<ImageButton>(R.id.prevTrackButton)
        prevTrackButton.setImageResource(R.drawable.baseline_skip_previous_24)


        prevTrackButton.setOnClickListener {
            val selectedPlaylist = mediaPlayerManager.getCurrentPlaylist()
            if (selectedPlaylist != null) {
                mediaPlayerManager.playPrevTrack(this)
            } else {
                // Обработка случая, когда плейлист не инициализирован
                Toast.makeText(this, "Playlist not available", Toast.LENGTH_SHORT).show()
            }
        }

        nextTrackButton = findViewById<ImageButton>(R.id.nextTrackButton)
        nextTrackButton.setImageResource(R.drawable.baseline_skip_next_24)


        nextTrackButton.setOnClickListener {
            val selectedPlaylist = mediaPlayerManager.getCurrentPlaylist()
            if (selectedPlaylist != null) {
                mediaPlayerManager.playNextTrack(this)
            } else {
                // Обработка случая, когда плейлист не инициализирован
                Toast.makeText(this, "Playlist not available", Toast.LENGTH_SHORT).show()
            }
        }
        val nowPlayingInfoTextView: TextView = findViewById(R.id.nowPlayingInfoTextView)

        // Получаем текущий трек из MediaPlayerManager
        val currentTrack = mediaPlayerManager.getCurrentTrack()

        // Если текущий трек не равен null, отображаем информацию о треке в TextView
        if (currentTrack != null) {
            val nowPlayingText = "${currentTrack.title} - ${currentTrack.artist}"
            nowPlayingInfoTextView.text = nowPlayingText
        } else {
            // Если текущий трек равен null, очищаем TextView (или выводим нужный текст)
            nowPlayingInfoTextView.text = "No track playing"
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
    private fun updateNowPlayingInfo() {
        val nowPlayingInfoTextView: TextView = findViewById(R.id.nowPlayingInfoTextView)
        val currentTrack = mediaPlayerManager.getCurrentTrack()

        if (currentTrack != null) {
            val nowPlayingText = "${currentTrack.title} - ${currentTrack.artist}"
            nowPlayingInfoTextView.text = nowPlayingText
        } else {
            nowPlayingInfoTextView.text = "No track playing"
        }
    }
}