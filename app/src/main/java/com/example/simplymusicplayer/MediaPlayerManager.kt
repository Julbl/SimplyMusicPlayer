package com.example.musicplaylist

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.example.simplymusicplayer.MusicTrack

class MediaPlayerManager private constructor(){
    private var track: MusicTrack? = null
    private var listener: ((MusicTrack?) -> Unit)? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: MusicTrack? = null

    interface OnTrackChangedListener {
        fun onTrackChanged(track: MusicTrack)
        fun onTrackStopped()
    }

    private val trackChangedListeners = mutableListOf<OnTrackChangedListener>()

    fun addOnTrackChangedListener(listener: OnTrackChangedListener) {
        trackChangedListeners.add(listener)
    }

    fun removeOnTrackChangedListener(listener: OnTrackChangedListener) {
        trackChangedListeners.remove(listener)
    }

    fun notifyTrackChangedOrStopped(track: MusicTrack?) {
        for (listener in trackChangedListeners) {
            if (track != null) {
                listener.onTrackChanged(track)
            } else {
                listener.onTrackStopped()
            }
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

    fun playTrack(context: Context, track: MusicTrack) {
        try {
            stopTrack()

            val audioResId = context.resources.getIdentifier(track.audioFileName, "raw", context.packageName)

            if (audioResId != 0) {
                mediaPlayer = MediaPlayer.create(context, audioResId)

                mediaPlayer?.setOnErrorListener { mp, what, extra ->
                    Log.e("MediaPlayerManager", "MediaPlayer error: what=$what, extra=$extra")
                    false
                }

                mediaPlayer?.setOnCompletionListener {
                    stopTrack()
                    notifyTrackChangedOrStopped(null)
                }

                mediaPlayer?.setOnPreparedListener {
                    // При успешной подготовке начинаем воспроизведение
                    it.start()
                    currentTrack = track
                    updateTrack(track)
                    notifyTrackChangedOrStopped(track)
                }

                mediaPlayer?.prepareAsync()

            } else {
                Toast.makeText(context, "Resource not found for track: ${track.audioFileName}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MediaPlayerManager", "Error during playback: ${e.message}")
        }
    }



    fun stopTrack() {
        // Останавливаем предыдущий трек, если он существует
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        // Сбрасываем текущий трек
        currentTrack = null
        mediaPlayer = null
    }

    fun getCurrentTrack(): MusicTrack? {
        return currentTrack
    }

    fun updateTrack(track: MusicTrack) {
        this.track = track
        listener?.invoke(track)
    }

    fun getCurrentMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }



    // Другие методы управления воспроизведением, если нужно
}