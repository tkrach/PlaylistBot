package com.example.playlistbot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Playlist Bot") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { viewModel.authenticateSpotify() }) {
                    Text("Authenticate with Spotify")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.createPlaylist("Relaxing Music", 10) }) {
                    Text("Create Playlist")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.chatWithGPT("Create a playlist description") }) {
                    Text("Chat with GPT")
                }
            }
        }
    )
}
