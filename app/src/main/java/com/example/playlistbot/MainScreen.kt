package com.example.playlistbot

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel) {
    Log.d("MainScreen", "MainScreen Composable")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("generatePlaylist") }) {
            Text("Generate Playlist")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("enhancePlaylist") }) {
            Text("Enhance Existing Playlist")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("chat") }) {
            Text("Talk to ChatGPT")
        }
    }
}
