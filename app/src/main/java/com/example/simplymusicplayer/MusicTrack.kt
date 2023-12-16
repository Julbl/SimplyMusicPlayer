package com.example.simplymusicplayer

import java.io.Serializable


data class MusicTrack(
    val title: String,
    val artist: String,
    val album: String,
    val imageResourse: Int,
    val audioFileName: String,
):Serializable

fun getSampleTracksForPlaylistGoodMood(): List<MusicTrack> {
    return listOf(
        MusicTrack("Asphalt 8", "MACAN", "Для хорошего настроения",R.drawable.asphalt8, "asphalt"),
        MusicTrack("LastChristmas", "WHAM!", "Для хорошего настроения",R.drawable.wham_last_christmas, "last_christmas")
        // Добавьте другие треки, если необходимо
    )
}

fun getSampleTracksForPlaylistSleeping(): List<MusicTrack> {
    return listOf(
        MusicTrack("Музыка для сна 1", "-", "Музыка для сна",R.drawable.music_for_sleeping_1, "music_for_sleeping_1"),
        // Добавьте другие треки, если необходимо
    )
}

fun getSampleTracksForPlaylistClassic(): List<MusicTrack> {
    return listOf(
        MusicTrack("Бетховен", "Für Elise", "Классика",R.drawable.beethoven, "beethoven"),
        // Добавьте другие треки, если необходимо
    )
}

fun getSampleTracksForPlaylistSad(): List<MusicTrack> {
    return listOf(
        MusicTrack("Я знаю, что делал прошлым летом", "Oxxxymiron", "Когда грустно",R.drawable.lust_summer, "i_know_what_ur_doing_lust_summer"),
        // Добавьте другие треки, если необходимо
    )
}