package com.example.playlistbot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.playlistbot.ui.theme.PlaylistBotTheme
import androidx.compose.foundation.layout.padding
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE = 1337
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistBotTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    content = { paddingValues ->
                        Text(
                            text = "Hello, World!",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }
                )
            }
        }
        val clientId = getString(R.string.spotify_client_id)
        val redirectUri = getString(R.string.spotify_redirect_uri)
        // Initiate authentication
        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(arrayOf("playlist-modify-public", "playlist-modify-private"))
            .build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    @OptIn(UnstableApi::class)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    // Successfully authenticated
                    val accessToken = response.accessToken
                    Log.d("SpotifyAuth", "Access Token: $accessToken")
                    // Proceed with making API calls using the accessToken
                }

                AuthorizationResponse.Type.ERROR -> {
                    // Handle error response
                    Log.e("SpotifyAuth", "Auth error: ${response.error}")
                }

                else -> {
                    // Handle other cases
                    Log.e("SpotifyAuth", "Auth result: ${response.type}")
                }
            }
        }
    }
}
