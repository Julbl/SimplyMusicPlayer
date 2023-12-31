package com.example.musicplaylist

import TrackAdapter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.simplymusicplayer.Manifest
import com.example.simplymusicplayer.MusicTrack
import com.example.simplymusicplayer.R
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PlaylistActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val mediaPlayerManager = MediaPlayerManager.getInstance()
    private lateinit var playPauseButton : ImageButton
    private lateinit var nextTrackButton : ImageButton
    private lateinit var prevTrackButton : ImageButton
    private lateinit var editTextTitle : TextView
    private lateinit var editTextArtist : TextView
    private lateinit var trackAdapter : TrackAdapter
    private lateinit var originalTracks: List<MusicTrack>
    private val REQUEST_PERMISSION_CODE = 123
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
        loadTrack()
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

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Вызывается при отправке формы поиска (например, нажатии клавиши "Enter")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Вызывается при изменении текста в поле поиска
                // Обработайте newText и выполните поиск
                performSearch(newText)
                return true
            }
        })
        originalTracks = playlist.tracks

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        } else {
            // Разрешение уже предоставлено
            // Здесь вы можете вызывать метод для получения списка треков
            val tracks = getMusicTracks()
        }
        val rootView = findViewById<View>(R.id.rootLayout) // Замените на ID вашего корневого макета
        rootView.viewTreeObserver.addOnPreDrawListener {
            val insets = ViewCompat.getRootWindowInsets(rootView)
            val isKeyboardOpen = insets?.isVisible(WindowInsetsCompat.Type.systemBars())

            if (isKeyboardOpen == true) {
                // Клавиатура открыта, выполните действия по необходимости
                // Например, скройте или подстроите элементы интерфейса
            } else {
                // Клавиатура закрыта, выполните действия по необходимости
            }

            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение предоставлено, вызывайте метод для получения списка треков
                    val tracks = getMusicTracks()
                    // Ваши дальнейшие действия с полученным списком треков
                } else {
                    // Разрешение не предоставлено, предпримите соответствующие действия
                }
            }
            else -> {
                // Обработка других requestCode, если необходимо
            }
        }
    }
    private fun getMusicTracks(): List<String> {
        val tracks = mutableListOf<String>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (it.moveToNext()) {
                val path = it.getString(columnIndex)
                tracks.add(path)
            }
        }

        return tracks
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

    fun onAddTrackButtonClick(view: View) {
        openFilePicker()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*" // Фильтруем только аудиофайлы
        startActivityForResult(intent, REQUEST_PERMISSION_CODE)
    }
    private fun updatePlaylistRecyclerView() {
        // Проверяем, что текущий плейлист не равен null
        val currentPlaylist = mediaPlayerManager.getCurrentPlaylist()

        // Проверяем, что текущий плейлист не равен null и содержит треки
        if (currentPlaylist != null && currentPlaylist.isNotEmpty()) {
            // Обновляем данные в адаптере
            trackAdapter.updateData(currentPlaylist)

            // Оповещаем адаптер о том, что данные были изменены
            trackAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentPlaylist = mediaPlayerManager.getCurrentPlaylist()
        val currentAlbumName = currentPlaylist?.firstOrNull()?.album ?: "Unknown Album"

        if (requestCode == REQUEST_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val filePath = getRealPathFromURI(uri)

                if (filePath != null) {
                    // Создайте диалоговое окно для ввода данных
                    val builder = AlertDialog.Builder(this)
                    val inflater = layoutInflater
                    builder.setTitle("Введите информацию о треке")
                    val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
                    editTextTitle = dialogLayout.findViewById<EditText>(R.id.editTextTitle)
                    editTextArtist = dialogLayout.findViewById<EditText>(R.id.editTextArtist)

                    builder.setView(dialogLayout)
                    builder.setPositiveButton("OK") { dialogInterface, i ->
                        val title = editTextTitle.text.toString()
                        val artist = editTextArtist.text.toString()

                        // Создайте новый объект MusicTrack с введенными данными
                        val defaultImageResource = R.drawable.default_album_cover
                        val newTrack = MusicTrack(
                            title,
                            artist,
                            currentAlbumName,
                            defaultImageResource,
                            filePath
                        )

                        // Добавьте новый трек в текущий плейлист
                        currentPlaylist?.add(newTrack)
                        saveTracks(newTrack)

                        // Обновите ваш RecyclerView с треками в плейлисте
                        updatePlaylistRecyclerView()

                        Toast.makeText(this, "Выбран файл: $filePath", Toast.LENGTH_SHORT).show()
                    }
                    builder.show()
                } else {
                    Toast.makeText(this, "Не удалось получить путь к файлу", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private val sharedPreferences by lazy {
        getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }


    private fun performSearch(query: String?): MutableList<MusicTrack> {
        // Фильтруем список треков по введенному запросу
        val filteredTracks: MutableList<MusicTrack> = originalTracks.filter { track ->
            track.title.contains(query.orEmpty(), ignoreCase = true) ||
                    track.artist.contains(query.orEmpty(), ignoreCase = true)
        }.toMutableList()

        // Сохраняем обновленный список треков
        //saveTracks(filteredTracks)
        trackAdapter.updateData(filteredTracks)

        // Оповещаем адаптер о том, что данные были изменены
        trackAdapter.notifyDataSetChanged()

        // Возвращаем отфильтрованный список треков
        return filteredTracks
    }

    inline fun <reified T> Gson.fromJson(json: String): T {
        val type = object : TypeToken<T>() {}.type
        return this.fromJson(json, type)
    }

    private fun saveTracks(tracks: MusicTrack) {
        val tracksJson = Gson().toJson(tracks)
        sharedPreferences.edit().putString("tracks", tracksJson).apply()
    }

    private fun loadTrack(): MusicTrack? {
        val trackJson = sharedPreferences.getString("track", null)
        return if (trackJson != null) {
            Gson().fromJson(trackJson, MusicTrack::class.java)
        } else {
            null
        }
    }
    /*private fun loadTrack() {
        val tracksJson = sharedPreferences.getString("tracks", null)
        val loadedTracks: MutableList<MusicTrack> = Gson().fromJson(tracksJson ?: "[]", object : TypeToken<MutableList<MusicTrack>>() {}.type)
        originalTracks.clear()
        originalTracks.addAll(loadedTracks)
    }*/
}

