package com.example.simplymusicplayer

import java.io.Serializable


data class MusicTrack(
    val title: String,
    val artist: String,
    val album: String,
    val imageResourse: Int,
    val audioFileName: String,
):Serializable

fun getSampleTracksForPlaylistGoodMood(): MutableList<MusicTrack> {
    return mutableListOf(
        MusicTrack("Asphalt 8", "MACAN", "Для хорошего настроения",R.drawable.asphalt8, "asphalt"),
        MusicTrack("LastChristmas", "WHAM!", "Для хорошего настроения",R.drawable.wham_last_christmas, "last_christmas")
        // Добавьте другие треки, если необходимо
    )
}

fun getSampleTracksForPlaylistSleeping(): MutableList<MusicTrack> {
    return mutableListOf(
        MusicTrack("Музыка для сна 1", "-", "Музыка для сна",R.drawable.music_for_sleeping_1, "music_for_sleeping_1"),
        // Добавьте другие треки, если необходимо
    )
}

fun getSampleTracksForPlaylistClassic():MutableList<MusicTrack> {
    return mutableListOf(
        MusicTrack("Бетховен", "Für Elise", "Классика",R.drawable.beethoven, "beethoven"),
        // Добавьте другие треки, если необходимо
    )
}

fun getSampleTracksForPlaylistSad(): MutableList<MusicTrack> {
    return mutableListOf(
        MusicTrack("Я знаю, что делал прошлым летом", "Oxxxymiron", "Когда грустно",R.drawable.lust_summer, "i_know_what_ur_doing_lust_summer"),
        // Добавьте другие треки, если необходимо
    )
}