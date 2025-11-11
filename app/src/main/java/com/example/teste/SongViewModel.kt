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

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

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

                    // Set first song as current song but don't auto-play
                    if (convertedSongs.isNotEmpty()) {
                        setCurrentSong(0)
                        // Set initial state to paused
                        _isPlaying.value = false
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
                    _isPlaying.value = true // Auto-play after next
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
                    _isPlaying.value = true // Auto-play after previous
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
                    if (convertedSongs.isNotEmpty()) {
                        setCurrentSong(0)
                    } else {
                        _currentSong.value = null
                        _currentSongPosition.value = -1
                        _isPlaying.value = false
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
                // Since we don't have a direct play endpoint, we'll use next/previous to navigate
                // Find the position of the song in the current list
                val songsList = _songs.value ?: emptyList()
                val position = songsList.indexOfFirst { it.uuid == song.uuid }

                if (position != -1) {
                    // If it's not the current song, we need to navigate to it
                    // For now, let's just set it as current and resume playback
                    setCurrentSong(position)

                    // Resume playback if not already playing
                    if (!(_isPlaying.value ?: false)) {
                        val response = RetrofitInstance.apiService.resumeSong()
                        if (response.isSuccessful) {
                            _isPlaying.value = true
                        } else {
                            _error.value = "Failed to resume song: ${response.code()}"
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error playing song: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val currentPlaying = _isPlaying.value ?: false
                if (currentPlaying) {
                    // If currently playing, pause it
                    val response = RetrofitInstance.apiService.pauseSong()
                    if (response.isSuccessful) {
                        _isPlaying.value = false
                    } else {
                        _error.value = "Failed to pause song: ${response.code()}"
                    }
                } else {
                    // If currently paused, resume it
                    val response = RetrofitInstance.apiService.resumeSong()
                    if (response.isSuccessful) {
                        _isPlaying.value = true
                    } else {
                        _error.value = "Failed to resume song: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error toggling play/pause: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun pauseSong() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.pauseSong()
                if (response.isSuccessful) {
                    _isPlaying.value = false
                } else {
                    _error.value = "Failed to pause song: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error pausing song: ${e.message}"
            }
        }
    }

    fun resumeSong() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.resumeSong()
                if (response.isSuccessful) {
                    _isPlaying.value = true
                } else {
                    _error.value = "Failed to resume song: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error resuming song: ${e.message}"
            }
        }
    }

    fun stopSong() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.stopSong()
                if (response.isSuccessful) {
                    _isPlaying.value = false
                } else {
                    _error.value = "Failed to stop song: ${response.code()}"
                }
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