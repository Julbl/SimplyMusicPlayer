package com.example.musicplaylist
import com.example.simplymusicplayer.MusicTrack
import java.io.Serializable

data class Playlist (
    val name: String,
    val coverImageUrl: Int,
    val tracks: MutableList<MusicTrack>
):Serializable