package com.example.teste

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupSongs()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_playlist)

        songAdapter = SongAdapter(
            songs = songList,
            onItemClick = { song ->
                // TODO: tocar musica
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

    private fun setupSongs() {
        songList.apply {
            add(Song(1, "Cats on Mars", "Seatbelts", "2:46"))
            add(Song(2, "Tank!", "Seatbelts", "3:30"))
            add(Song(3, "Space Lion", "Seatbelts", "4:10"))
            add(Song(4, "The Real Folk Blues", "Seatbelts", "4:25"))
            add(Song(5, "Bad Dog No Biscuits", "Seatbelts", "3:15"))
        }
    }
}