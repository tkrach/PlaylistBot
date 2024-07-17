package com.example.playlistbot

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)
    val authenticationState = mutableStateOf(false)
    private val context: Context = application.applicationContext

    companion object {
        private const val REQUEST_CODE = 1337
    }

    fun authenticateSpotify(activity: ComponentActivity) {
        Log.d("MainViewModel", "Authenticating Spotify")
        val accessToken = getAccessTokenFromStorage()
        if (accessToken != null && !isTokenExpired()) {
           authenticationState.value = true
        } else {
            performSpotifyAuthentication(activity)
        }
    }

    private fun isTokenExpired(): Boolean {
        Log.d("MainViewModel", "Checking if token is expired")
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        val expiryTime = sharedPreferences.getLong("expiry_time", 0L)
        Log.d("MainViewModel", "expire time is $expiryTime")
        Log.d("MainViewModel", "current time is ${System.currentTimeMillis() / 1000}")
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime >= expiryTime
    }

    private fun performSpotifyAuthentication(activity: ComponentActivity) {
        Log.d("MainViewModel", "Performing Spotify authentication")
        val clientId = context.getString(R.string.spotify_client_id)
        val redirectUri = context.getString(R.string.spotify_redirect_uri)
        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(arrayOf("playlist-modify-public", "playlist-modify-private"))
            .build()
        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request)
    }

    fun handleAuthResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val accessToken = response.accessToken
                    saveAccessToken(accessToken, response.expiresIn)
                    authenticationState.value = true
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("SpotifyAuth", "Authentication error: ${response.error}")
                }
                else -> {
                    Log.d("SpotifyAuth", "Authentication result: ${response.type}")
                }
            }
        }
    }

    private fun getAccessTokenFromStorage(): String? {
        Log.d("MainViewModel", "Getting access token from storage")
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)

    }

    private fun saveAccessToken(token: String, expiresIn: Int) {
        Log.d("MainViewModel", "Saving access token to storage")
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", token)
        editor.putLong("expiry_time", System.currentTimeMillis() / 1000 + expiresIn)
        editor.apply()
        Log.d("MainViewModel", "expire time is $expiresIn")
    }
    fun createPlaylist(name: String, description: String, numTracks: Int) {
        viewModelScope.launch {
            repository.createPlaylist(name, description, numTracks)
        }
    }

    fun chatWithGPT(query: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            Log.d("MainViewModel", "Sending query to GPT: $query")
            ChatGPT(context).chatWithGPT(query) { result ->
                Log.d("MainViewModel", "Received response from GPT: $result")
                callback(result)
            }
        }
    }

    fun enhancePlaylist(playlistId: String, numTracks: Int) {
        viewModelScope.launch {
            repository.enhancePlaylist(playlistId, numTracks)
        }
    }
}
