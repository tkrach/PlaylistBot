package com.example.playlistbot

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val context: Context) {
    private val spotifyService = Spotify(context, )
    private val openAIService = ChatGPT(context)

    suspend fun createPlaylist(playlistName: String, description: String, numTracks: Int) = withContext(Dispatchers.IO) {

        ChatGPT(context).createPlaylist(playlistName, description, numTracks) { result ->
            Log.d("MainViewModel", "Received response from GPT: $result")
        }
    }

    suspend fun chatWithGPT(query: String, callback: (String) -> Unit) = withContext(Dispatchers.IO) {
        openAIService.chatWithGPT(query) { response ->
            // Handle the response from OpenAI API
        }
    }
    suspend fun enhancePlaylist(playlistId: String, numTracks: Int) = withContext(Dispatchers.IO) {

    }

}
