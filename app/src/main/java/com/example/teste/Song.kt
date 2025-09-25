package com.example.teste

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val imageResId: Int = R.drawable.ic_launcher_background
)