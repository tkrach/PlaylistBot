package com.example.playlistbot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.playlistbot.ui.theme.PlaylistBotTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistBotTheme {
                Scaffold(
                    content = { paddingValues ->
                        MainScreen(viewModel = viewModel, modifier = Modifier.padding(paddingValues))
                    }
                )
            }
        }
    }
}
