package com.example.musicplaylist

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.simplymusicplayer.MusicTrack
import com.example.simplymusicplayer.getSampleTracksForPlaylistClassic
import com.example.simplymusicplayer.getSampleTracksForPlaylistGoodMood
import com.example.simplymusicplayer.getSampleTracksForPlaylistSad
import com.example.simplymusicplayer.getSampleTracksForPlaylistSleeping

class MediaPlayerManager private constructor() {
    private var track: MusicTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: MusicTrack? = null
    private var currentPlaylist: List<MusicTrack>? = null

    interface OnTrackChangedListener {
        fun onTrackChanged(track: MusicTrack)
        fun onTrackStopped()
    }

    private val trackChangedListeners = mutableListOf<OnTrackChangedListener>()
    fun setPlaylistFromAlbum(album: String) {
        // По имени альбома получаем плейлист и устанавливаем его
        when (album) {
            "Для хорошего настроения" -> setCurrentPlaylist(getSampleTracksForPlaylistGoodMood())
            "Музыка для сна" -> setCurrentPlaylist(getSampleTracksForPlaylistSleeping())
            "Классика" -> setCurrentPlaylist(getSampleTracksForPlaylistClassic())
            "Когда грустно" -> setCurrentPlaylist(getSampleTracksForPlaylistSad())
            // Добавьте другие альбомы по мере необходимости
            else -> setCurrentPlaylist(emptyList()) // Пустой плейлист, если альбом не найден
        }
    }
    companion object {
        @Volatile
        private var instance: MediaPlayerManager? = null

        fun getInstance(): MediaPlayerManager =
            instance ?: synchronized(this) {
                instance ?: MediaPlayerManager().also { instance = it }
            }
    }

    fun setCurrentPlaylist(playlist: List<MusicTrack>) {
        currentPlaylist = playlist
    }

    fun playOrPauseTrack(context: Context, track: MusicTrack) {
        try {
            val audioResId = context.resources.getIdentifier(track.audioFileName, "raw", context.packageName)

            if (audioResId != 0) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(context, audioResId)

                    mediaPlayer?.setOnErrorListener { mp, what, extra ->
                        Log.e("MediaPlayerManager", "MediaPlayer error: what=$what, extra=$extra")
                        false
                    }

                    mediaPlayer?.setOnCompletionListener {
                        stopTrack()
                        notifyTrackChangedOrStopped(null)
                    }
                }

                if (currentTrack == track && mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(context, Uri.parse("android.resource://${context.packageName}/$audioResId"))
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    currentTrack = track
                    updateTrack(track)
                    notifyTrackChangedOrStopped(track)
                }
            } else {
                Toast.makeText(context, "Resource not found for track: ${track.audioFileName}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MediaPlayerManager", "Error during playback: ${e.message}")
        }
    }


    fun playNextTrack(context: Context) {
        val playlist = currentPlaylist
        if (playlist != null && currentTrack != null) {
            val currentTrackIndex = playlist.indexOf(currentTrack)
            val nextTrackIndex = (currentTrackIndex + 1) % playlist.size
            val nextTrack = playlist[nextTrackIndex]

            playOrPauseTrack(context, nextTrack)
        }
    }

    fun playPrevTrack(context: Context) {
        val playlist = currentPlaylist
        if (playlist != null && currentTrack != null) {
            val currentTrackIndex = playlist.indexOf(currentTrack)
            val prevTrackIndex = (currentTrackIndex - 1 + playlist.size) % playlist.size
            val prevTrack = playlist[prevTrackIndex]

            playOrPauseTrack(context, prevTrack)
        }
    }
    fun stopTrack() {
            mediaPlayer?.apply {
                if (isPlaying) {
                    pause()
                }
                seekTo(0)
            }
            notifyTrackChangedOrStopped(null)
        }


    fun getCurrentTrack(): MusicTrack? {
        return currentTrack
    }

    fun updateTrack(track: MusicTrack) {
        this.track = track
        notifyTrackChangedOrStopped(track)
    }

    fun getCurrentMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }


    fun getCurrentPlaylist(): List<MusicTrack>? {
        return currentPlaylist
    }

    private fun notifyTrackChangedOrStopped(track: MusicTrack?) {
        for (listener in trackChangedListeners) {
            if (track != null) {
                listener.onTrackChanged(track)
            } else {
                listener.onTrackStopped()
            }
        }
    }

    fun addOnTrackChangedListener(listener: OnTrackChangedListener) {
        trackChangedListeners.add(listener)
    }

    fun removeOnTrackChangedListener(listener: OnTrackChangedListener) {
        trackChangedListeners.remove(listener)
    }

    // Другие методы управления воспроизведением, если нужно
}