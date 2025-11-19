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

class AddSongActivity : AppCompatActivity() {
    private lateinit var availableSongsAdapter: AvailableSongAdapter
    private val viewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_song)

        setupRecyclerView()
        setupButtonListeners()
        setupObservers()
        loadAvailableSongs()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_available_songs)

        availableSongsAdapter = AvailableSongAdapter(
            songs = emptyList(),
            onItemClick = { song ->
                addSongToPlaylist(song)
            },
            onAddClick = { song ->
                addSongToPlaylist(song)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddSongActivity)
            adapter = availableSongsAdapter
            addItemDecoration(DividerItemDecoration(this@AddSongActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupButtonListeners() {
        findViewById<android.widget.Button>(R.id.button_back).setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.availableSongs.observe(this) { songs ->
            availableSongsAdapter.updateSongs(songs)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide loading indicator if needed
            if (isLoading) {
                // Show loading
            } else {
                // Hide loading
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                android.widget.Toast.makeText(this, it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.songAdded.observe(this) { success ->
            if (success) {
                android.widget.Toast.makeText(this, "Song added to playlist!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(this, "Failed to add song", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addSongToPlaylist(song: Song) {
        viewModel.addSongToPlaylist(song.uuid)
    }

    private fun loadAvailableSongs() {
        lifecycleScope.launch {
            viewModel.loadAvailableSongs()
        }
    }
}