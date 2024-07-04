package com.example.playlistbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EnhancePlaylistScreen(viewModel: MainViewModel) {
    var playlistId by remember { mutableStateOf("") }
    var numTracks by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = playlistId,
            onValueChange = { playlistId = it },
            label = { Text("Playlist ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = numTracks,
            onValueChange = { newValue ->
                // Filter the input to allow only digits
                numTracks = newValue.filter { it.isDigit() }
            },
            label = { Text("Number of Tracks") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.enhancePlaylist(playlistId, numTracks.toIntOrNull() ?: 0) }) {
            Text("Enhance Playlist")
        }
    }
}
