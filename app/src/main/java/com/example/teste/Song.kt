package com.example.teste

data class Song(
    val uuid: String,
    val title: String,
    val artist: String,
    val duration: String,
    val imageResId: Int = R.drawable.ic_launcher_background,
    var isCurrent: Boolean = false // Add this field to track current song
) {
    companion object {
        fun formatDuration(durationInSeconds: Double): String {
            val minutes = (durationInSeconds / 60).toInt()
            val seconds = (durationInSeconds % 60).toInt()
            return String.format("%d:%02d", minutes, seconds)
        }
    }
}