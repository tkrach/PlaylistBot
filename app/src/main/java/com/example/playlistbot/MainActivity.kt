package com.example.playlistbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playlistbot.ui.theme.PlaylistBotTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistBotTheme {
                Surface {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "landing") {
                        composable("landing") {
                            LandingScreen(navController, viewModel)
                        }
                        composable("main") {
                            MainScreen(navController, viewModel)
                        }
                        composable("generatePlaylist") {
                            GeneratePlaylistScreen(viewModel)
                        }
                        composable("enhancePlaylist") {
                            EnhancePlaylistScreen(viewModel)
                        }
                        composable("chat") {
                            ChatScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
