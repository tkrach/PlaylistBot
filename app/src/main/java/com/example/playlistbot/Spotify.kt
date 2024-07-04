package com.example.playlistbot

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class Spotify(private val context: Context) {
    private val client = OkHttpClient()

    fun createPlaylist(name: String, description: String, callback: (String) -> Unit) {
        val token = getAccessToken(context) ?: return
        val json = JSONObject()
        json.put("name", name)
        json.put("description", description)
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/users/{user_id}/playlists")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string() ?: "")
                } else {
                    // Handle error
                }
            }
        })
    }
    private fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }
    fun searchTracks(query: String, callback: (List<String>) -> Unit) {

    }
}
