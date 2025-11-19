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

class AvailableSongAdapter(
    private var songs: List<Song>,
    private val onItemClick: (Song) -> Unit,
    private val onAddClick: (Song) -> Unit
) : RecyclerView.Adapter<AvailableSongAdapter.AvailableSongViewHolder>() {

    inner class AvailableSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSong: ImageView = itemView.findViewById(R.id.image_song)
        private val textTitle: TextView = itemView.findViewById(R.id.text_song_title)
        private val textArtist: TextView = itemView.findViewById(R.id.text_song_artist)
        private val textDuration: TextView = itemView.findViewById(R.id.text_song_duration)
        private val buttonMenu: ImageButton = itemView.findViewById(R.id.button_menu)
        private val songItemLayout: LinearLayout = itemView.findViewById(R.id.song_item_layout)

        fun bind(song: Song) {
            imageSong.setImageResource(song.imageResId)
            textTitle.text = song.title
            textArtist.text = song.artist
            textDuration.text = song.duration

            // Change menu button to "Add" icon and functionality
            buttonMenu.setImageResource(android.R.drawable.ic_input_add)
            buttonMenu.contentDescription = "Add to playlist"

            // Handle item click - add to playlist
            itemView.setOnClickListener {
                onAddClick(song)
            }

            // Handle add button click
            buttonMenu.setOnClickListener {
                onAddClick(song)
            }

            // Remove highlighting for available songs
            songItemLayout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    android.R.color.transparent
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableSongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return AvailableSongViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableSongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    fun updateSongs(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}