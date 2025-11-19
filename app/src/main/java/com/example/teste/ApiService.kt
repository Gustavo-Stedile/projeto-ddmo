package com.example.teste

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET("musicas")
    suspend fun getAvailableSongs(): Response<List<SongResponse>>

    @POST("to_playlist/{uuid}")
    suspend fun addSongToPlaylist(@Path("uuid") uuid: String): Response<Void>
}