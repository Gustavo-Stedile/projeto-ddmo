package com.example.teste

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private var songs: List<Song>,
    private val onItemClick: (Song) -> Unit,
    private val onMenuClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var currentSongPosition: Int = -1

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSong: ImageView = itemView.findViewById(R.id.image_song)
        private val textTitle: TextView = itemView.findViewById(R.id.text_song_title)
        private val textArtist: TextView = itemView.findViewById(R.id.text_song_artist)
        private val textDuration: TextView = itemView.findViewById(R.id.text_song_duration)
        private val buttonMenu: ImageButton = itemView.findViewById(R.id.button_menu)
        private val songItemLayout: LinearLayout = itemView.findViewById(R.id.song_item_layout)

        fun bind(song: Song, position: Int) {
            imageSong.setImageResource(song.imageResId)
            textTitle.text = song.title
            textArtist.text = song.artist
            textDuration.text = song.duration

            // Highlight current song
            if (position == currentSongPosition) {
                songItemLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.highlight_color
                    )
                )
            } else {
                // Use transparent background for non-current items
                songItemLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        android.R.color.transparent
                    )
                )
            }

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(song)
                setCurrentSong(position)
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
        holder.bind(songs[position], position)
    }

    override fun getItemCount(): Int = songs.size

    // Update the songs list
    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        currentSongPosition = -1 // Reset current position when list changes
        notifyDataSetChanged()
    }

    // Set current song and update highlight
    fun setCurrentSong(position: Int) {
        val previousPosition = currentSongPosition
        currentSongPosition = position

        // Update previous current song
        if (previousPosition != -1 && previousPosition < songs.size) {
            notifyItemChanged(previousPosition)
        }

        // Update new current song
        if (currentSongPosition != -1 && currentSongPosition < songs.size) {
            notifyItemChanged(currentSongPosition)
        }
    }

    // Get current song position
    fun getCurrentSongPosition(): Int = currentSongPosition

    // Get song at position
    fun getSongAt(position: Int): Song = songs[position]
}