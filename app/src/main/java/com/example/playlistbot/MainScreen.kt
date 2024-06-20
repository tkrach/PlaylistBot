package com.example.playlistbot

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var description by remember { mutableStateOf("") }
    var numTracks by remember { mutableStateOf("") }
    val activity = LocalContext.current as? ComponentActivity

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
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Playlist Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = numTracks,
                    onValueChange = { numTracks = it },
                    label = { Text("Number of Tracks") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { activity?.let { viewModel.authenticateSpotify(it) } }) {
                    Text("Authenticate with Spotify")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.createPlaylist(description, numTracks.toIntOrNull() ?: 0) }) {
                    Text("Create Playlist")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.chatWithGPT(description) }) {
                    Text("Chat with GPT")
                }
            }
        }
    )
}
