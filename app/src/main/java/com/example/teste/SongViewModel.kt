package com.example.teste

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SongViewModel : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs

    private val _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?> get() = _currentSong

    private val _currentSongPosition = MutableLiveData<Int>(-1)
    val currentSongPosition: LiveData<Int> get() = _currentSongPosition

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = RetrofitInstance.apiService.getPlaylist()
                if (response.isSuccessful) {
                    val songResponses = response.body() ?: emptyList()
                    val convertedSongs = convertToSongList(songResponses)
                    _songs.value = convertedSongs

                    // Set first song as current song
                    if (convertedSongs.isNotEmpty()) {
                        setCurrentSong(0)
                    }
                } else {
                    _error.value = "Failed to load songs: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun nextSong() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = RetrofitInstance.apiService.nextSong()
                if (response.isSuccessful) {
                    // After calling next, reload the playlist to sync with server
                    reloadPlaylist()
                } else {
                    _error.value = "Failed to go to next song: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun previousSong() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = RetrofitInstance.apiService.previousSong()
                if (response.isSuccessful) {
                    // After calling previous, reload the playlist to sync with server
                    reloadPlaylist()
                } else {
                    _error.value = "Failed to go to previous song: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun reloadPlaylist() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getPlaylist()
                if (response.isSuccessful) {
                    val songResponses = response.body() ?: emptyList()
                    val convertedSongs = convertToSongList(songResponses)
                    _songs.value = convertedSongs

                    // Always set the first song as current after next/previous
                    // because the server moves songs between tocar/tocado lists
                    if (convertedSongs.isNotEmpty()) {
                        setCurrentSong(0)
                    } else {
                        _currentSong.value = null
                        _currentSongPosition.value = -1
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error reloading playlist: ${e.message}"
            }
        }
    }

    fun setCurrentSong(position: Int) {
        val songsList = _songs.value ?: return
        if (position in songsList.indices) {
            _currentSongPosition.value = position
            _currentSong.value = songsList[position]
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // First, find the position of the song in the current list
                val songsList = _songs.value ?: emptyList()
                val position = songsList.indexOfFirst { it.uuid == song.uuid }

                if (position != -1) {
                    setCurrentSong(position)
                }
            } catch (e: Exception) {
                _error.value = "Error playing song: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun pauseSong() {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiService.pauseSong()
            } catch (e: Exception) {
                _error.value = "Error pausing song: ${e.message}"
            }
        }
    }

    fun resumeSong() {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiService.resumeSong()
            } catch (e: Exception) {
                _error.value = "Error resuming song: ${e.message}"
            }
        }
    }

    fun stopSong() {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiService.stopSong()
            } catch (e: Exception) {
                _error.value = "Error stopping song: ${e.message}"
            }
        }
    }

    private fun convertToSongList(songResponses: List<SongResponse>): List<Song> {
        return songResponses.mapIndexed { index, songResponse ->
            Song(
                uuid = songResponse.uuid,
                title = songResponse.title,
                artist = songResponse.artist,
                duration = Song.formatDuration(songResponse.duration)
            )
        }
    }
}