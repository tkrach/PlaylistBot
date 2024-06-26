package com.example.playlistbot

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)
    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext

    companion object {
        private const val REQUEST_CODE = 1337
    }

    fun authenticateSpotify(activity: ComponentActivity) {
        val accessToken = getAccessTokenFromStorage()
        if (accessToken != null) {
            showMessage("Authentication already successful!")
            // Handle already authenticated state
            Log.d("SpotifyAuth", "Already authenticated with access token: $accessToken")
            // Proceed with using the existing access token for API calls
            // Example: viewModel.useExistingAccessToken(accessToken)
        } else {
            showMessage("Performing authentication...")
            // Perform authentication flow
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
    }

    private fun getAccessTokenFromStorage(): String? {
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }


    fun handleAuthResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val accessToken = response.accessToken
                    saveAccessToken(accessToken)
                    showMessage("Authentication successful!")
                }
                AuthorizationResponse.Type.ERROR -> {
                    val errorMessage = "Authentication error: ${response.error}"
                    showMessage(errorMessage)
                    Log.e("SpotifyAuth", errorMessage)
                }
                else -> {
                    val message = "Authentication result: ${response.type}"
                    showMessage(message)
                    Log.d("SpotifyAuth", message)
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveAccessToken(token: String) {
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", token)
        editor.apply()
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
