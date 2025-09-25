package com.example.teste

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private var songs: List<Song>,
    private val onItemClick: (Song) -> Unit,
    private val onMenuClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSong: ImageView = itemView.findViewById(R.id.image_song)
        private val textTitle: TextView = itemView.findViewById(R.id.text_song_title)
        private val textArtist: TextView = itemView.findViewById(R.id.text_song_artist)
        private val textDuration: TextView = itemView.findViewById(R.id.text_song_duration)
        private val buttonMenu: ImageButton = itemView.findViewById(R.id.button_menu)

        fun bind(song: Song) {
            imageSong.setImageResource(song.imageResId)
            textTitle.text = song.title
            textArtist.text = song.artist
            textDuration.text = song.duration

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(song)
            }

            // Handle menu button click
            buttonMenu.setOnClickListener {
                onMenuClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    // Update the songs list
    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    // Get song at position
    fun getSongAt(position: Int): Song = songs[position]
}