package com.example.teste

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<Song>()
    private val viewModel: SongViewModel by viewModels()
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupButtonListeners()
        setupObservers()
        loadSongs()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_playlist)

        songAdapter = SongAdapter(
            songs = songList,
            onItemClick = { song ->
                // When a song is clicked, it should become the current song
                // In a real implementation, you might want to call a play endpoint
                viewModel.playSong(song)
            },
            onMenuClick = { song ->
                // TODO: talvez tirar isso
            }
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = songAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupButtonListeners() {
        // Next button
        findViewById<android.widget.ImageButton>(R.id.button_next).setOnClickListener {
            viewModel.nextSong()
        }

        // Previous button
        findViewById<android.widget.ImageButton>(R.id.button_previous).setOnClickListener {
            viewModel.previousSong()
        }

        // Play/Pause button
        val playPauseButton = findViewById<android.widget.ImageButton>(R.id.button_play_pause)
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                viewModel.pauseSong()
                playPauseButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                viewModel.resumeSong()
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupObservers() {
        viewModel.songs.observe(this) { songs ->
            songList.clear()
            songList.addAll(songs)
            songAdapter.updateSongs(songList)

            // Update play/pause button state based on whether we have songs
            val playPauseButton = findViewById<android.widget.ImageButton>(R.id.button_play_pause)
            if (songs.isEmpty()) {
                playPauseButton.setImageResource(android.R.drawable.ic_media_play)
                isPlaying = false
            }
        }

        viewModel.currentSong.observe(this) { song ->
            song?.let { updateCurrentSong(it) }
        }

        viewModel.currentSongPosition.observe(this) { position ->
            if (position != -1) {
                songAdapter.setCurrentSong(position)

                // Scroll to current song position
                findViewById<RecyclerView>(R.id.recycler_playlist).smoothScrollToPosition(position)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Handle loading state if needed
            // You can show/hide progress bar here
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                // Handle error state - show toast or error message
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCurrentSong(song: Song) {
        findViewById<android.widget.ImageView>(R.id.image_current_song).setImageResource(song.imageResId)
        findViewById<android.widget.TextView>(R.id.text_current_title).text = song.title
        findViewById<android.widget.TextView>(R.id.text_current_artist).text = song.artist
        findViewById<android.widget.TextView>(R.id.text_current_duration).text = song.duration

        // Update play/pause button to show pause icon when a song is current
        val playPauseButton = findViewById<android.widget.ImageButton>(R.id.button_play_pause)
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        isPlaying = true
    }

    private fun loadSongs() {
        lifecycleScope.launch {
            viewModel.loadSongs()
        }
    }
}