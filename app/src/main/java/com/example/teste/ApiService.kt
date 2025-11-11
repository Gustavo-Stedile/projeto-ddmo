package com.example.teste

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("get_playlist") // Replace with your actual endpoint
    suspend fun getPlaylist(): Response<List<SongResponse>>

    @GET("proxima") // Next endpoint
    suspend fun nextSong(): Response<Unit>

    @GET("anterior") // Previous endpoint
    suspend fun previousSong(): Response<Unit>

    @GET("pause")
    suspend fun pauseSong(): Response<Unit>

    @GET("resume")
    suspend fun resumeSong(): Response<Unit>

    @GET("stop")
    suspend fun stopSong(): Response<Unit>
}