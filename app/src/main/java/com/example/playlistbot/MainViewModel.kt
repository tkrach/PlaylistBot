package com.example.playlistbot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)

    fun authenticateSpotify() {
        // Implement Spotify authentication logic if needed
    }

    fun createPlaylist(description: String, numTracks: Int) {
        viewModelScope.launch {
            repository.createPlaylist(description, numTracks)
        }
    }

    fun chatWithGPT(query: String) {
        viewModelScope.launch {
            repository.chatWithGPT(query)
        }
    }
}
