package com.example.playlistbot

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import android.util.Log


@Composable
fun LandingScreen(navController: NavHostController, viewModel: MainViewModel) {
    val authenticated by viewModel.authenticationState

    LaunchedEffect(authenticated) {
        if (authenticated) {
            navController.navigate("main") {
                popUpTo("landing") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.authenticateSpotify(navController.context as ComponentActivity) }) {
            Text("Authenticate with Spotify")
        }
    }
}
