package com.example.teste

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        loadSongs()
        setupRecyclerView()
        setupButtonListeners()
        setupObservers()

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        updatePlayPauseButton(viewModel.isPlaying.value!!)
        refreshPlaylist()
    }

    private fun refreshPlaylist() {
        lifecycleScope.launch {
            viewModel.loadSongs()
        }
    }


    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_playlist)

        songAdapter = SongAdapter(
            songs = songList,
            onItemClick = { song ->
                // When a song is clicked, play it
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

        // Play/Pause button - use toggle function
        val playPauseButton = findViewById<android.widget.ImageButton>(R.id.button_play_pause)
        playPauseButton.setOnClickListener {
            viewModel.togglePlayPause()
        }
    }

    private fun setupObservers() {
        viewModel.songs.observe(this) { songs ->
            songList.clear()
            songList.addAll(songs)
            songAdapter.updateSongs(songList)
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

        viewModel.isPlaying.observe(this) { isPlaying ->
            updatePlayPauseButton(isPlaying)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Handle loading state if needed
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCurrentSong(song: Song) {
        findViewById<android.widget.ImageView>(R.id.image_current_song).setImageResource(song.imageResId)
        findViewById<android.widget.TextView>(R.id.text_current_title).text = song.title
        findViewById<android.widget.TextView>(R.id.text_current_artist).text = song.artist
        findViewById<android.widget.TextView>(R.id.text_current_duration).text = song.duration
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        val playPauseButton = findViewById<android.widget.ImageButton>(R.id.button_play_pause)
        if (isPlaying) {
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    private fun loadSongs() {
        lifecycleScope.launch {
            viewModel.loadSongs()
        }
    }

    private fun setupNavigation() {
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_songs).setOnClickListener {
            val intent = Intent(this, AddSongActivity::class.java)
            startActivity(intent)
        }
    }
}