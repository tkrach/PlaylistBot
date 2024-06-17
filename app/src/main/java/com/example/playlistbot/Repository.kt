package com.example.playlistbot

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val context: Context) {
    private val spotifyService = Spotify(context)
    private val openAIService = ChatGPT()

    suspend fun createPlaylist(description: String, numTracks: Int) = withContext(Dispatchers.IO) {
        spotifyService.createPlaylist("PlaylistName", description) { response ->
            // Handle the response from Spotify API
        }
    }

    suspend fun chatWithGPT(query: String) = withContext(Dispatchers.IO) {
        openAIService.chatWithGPT(query) { response ->
            // Handle the response from OpenAI API
        }
    }
}
