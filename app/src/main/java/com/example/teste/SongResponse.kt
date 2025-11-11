package com.example.teste

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("autor") val artist: String,
    @SerializedName("duracao") val duration: Double,
    @SerializedName("titulo") val title: String,
    @SerializedName("uuid") val uuid: String
)