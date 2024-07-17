package com.example.playlistbot

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import org.json.JSONArray

class Spotify(private val context: Context) {
    private val client = OkHttpClient()

    private fun getAccessToken(): String? {
        val sharedPreferences = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("access_token", null)
        val expiryTime = sharedPreferences.getLong("expiry_time", 0L)
        val currentTime = System.currentTimeMillis() / 1000

        if (accessToken != null && currentTime < expiryTime) {
            return accessToken
        } else {
            Log.e("Spotify", "Access token is expired or not found")
            // Trigger re-authentication if needed FIX THIS LATER???
            return null
        }
    }
    fun getCurrentUserId(callback: (String?) -> Unit) {
        Log.d("Spotify", "Getting user ID...")
        val accessToken = getAccessToken() ?: run {
            Log.e("Spotify", "No access token found")
            callback(null)
            return
        }
        Log.d("Spotify", "Access token: $accessToken")
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Spotify", "Failed to connect to Spotify: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val userId = jsonResponse.getString("id")
                            Log.d("Spotify", "User ID: $userId")
                            Log.d("Spotify", "URI: ${jsonResponse.getString("uri")}")
                            callback(userId)
                        } catch (e: Exception) {
                            Log.e("Spotify", "Failed to parse response: ${e.message}")
                            callback(null)
                        }
                    } ?: run {
                        Log.e("Spotify", "Empty response body")
                        callback(null)
                    }
                } else {
                    val responseBody = response.body?.string()
                    Log.e("Spotify", "Error response from Spotify: ${response.message}, Code: ${response.code}, Body: $responseBody")
                    callback(null)
                }
            }
        })
    }
    fun findTrackIDs(tracks: List<Pair<String, String>>, callback: (List<String>) -> Unit) {
        Log.d("Spotify", "Finding track IDs...")
        val trackIDs = mutableListOf<String>()
        val accessToken = getAccessToken() ?: return
        Log.d("Spotify", "Access token: $accessToken")

        tracks.forEach { (artist, song) ->
            val url = "https://api.spotify.com/v1/search?q=track:$song%20artist:$artist&type=track"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("Spotify", "Failed to connect to Spotify: ${e.message}")
                    callback(emptyList())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        response.body?.string()?.let { responseBody ->
                            try {
                                val jsonResponse = JSONObject(responseBody)
                                val itemsArray = jsonResponse.getJSONObject("tracks")
                                    .getJSONArray("items")

                                // Check if there are any items in the array
                                if (itemsArray.length() > 0) {
                                    val trackID = itemsArray
                                        .getJSONObject(0)
                                        .getString("id")
                                    trackIDs.add(trackID)
                                } else {
                                    Log.d("Spotify", "Track not found for: $song by $artist")
                                }

                                // Check if we have collected all track IDs
                                if (trackIDs.size == tracks.size) {
                                    Log.d("Spotify", "Track IDs: $trackIDs")
                                    callback(trackIDs)
                                }
                            } catch (e: Exception) {
                                Log.e("Spotify", "Failed to parse response: ${e.message}")
                                callback(emptyList())
                            }
                        } ?: callback(emptyList())
                    } else {
                        Log.e("Spotify", "Error response from Spotify: ${response.message}")
                        callback(emptyList())
                    }
                }
            })
        }
    }

    fun createSpotifyPlaylist(
        userId: String,
        name: String,
        description: String,
        trackIDs: List<String>,
        callback: (String) -> Unit
    ) {
        Log.d("Spotify", "Creating playlist...")

        // Construct JSON object for the request body
        val json = JSONObject().apply {
            put("name", name)
            put("description", description)
            put("public", true)
        }

        // Retrieve access token
        val accessToken = getAccessToken()
        if (accessToken == null) {
            Log.e("Spotify", "Failed to get access token")
            callback("Failed to get access token")
            return
        }

        // Build request body and request
        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "https://api.spotify.com/v1/users/$userId/playlists"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Spotify", "Failed to create playlist", e)
                callback("Failed to create playlist: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {

                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val playlistID = jsonResponse.getString("id")
                            Log.d("Spotify", "Created playlist ID: $playlistID")

                            // Now add tracks to the playlist
                            addTracksToPlaylist(playlistID, trackIDs, accessToken) { addTracksResponse ->
                                callback(addTracksResponse)
                            }
                        } catch (e: Exception) {
                            Log.e("Spotify", "Failed to parse response", e)
                            callback("Failed to parse response: ${e.message}")
                        }
                    } else {
                        val errorBody = responseBody ?: "No error body"
                        Log.e(
                            "Spotify",
                            "Error response from Spotify: ${response.code} - $errorBody"
                        )
                        callback("Error: ${response.code} - $errorBody")
                    }
            }
        })

        // Log critical details for debugging
        Log.d("Spotify", "Request URL: ${request.url}")
        Log.d("Spotify", "Request Headers: ${request.headers}")
        Log.d("Spotify", "Request Body: $json")
        Log.d("Spotify", "Access Token: $accessToken")
    }


    private fun addTracksToPlaylist(playlistID: String, trackIDs: List<String>, accessToken: String, callback: (String) -> Unit) {
        val uris = JSONArray(trackIDs.map { "spotify:track:$it" })
        if (uris.length() == 0) {
            Log.e("Spotify - ADD TRACKS", "Empty uris array")
            callback("Error: Empty uris array")
            return
        }

        val json = JSONObject().apply {
            put("uris", uris)
        }
        Log.d("Spotify - ADD TRACKS", "Adding tracks to the playlist: $json")

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/playlists/$playlistID/tracks")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Spotify", "Failed to add tracks: ${e.message}")
                callback("Failed to add tracks: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val snapshotId = jsonResponse.getString("snapshot_id")
                        Log.d("Spotify", "Tracks added successfully! Snapshot ID: $snapshotId")
                        callback("Tracks added successfully!")
                    } catch (e: Exception) {
                        Log.e("Spotify", "Failed to parse response: ${e.message}")
                        callback("Failed to parse response: ${e.message}")
                    }
                } else {
                    val errorBody = responseBody ?: "No error body"
                    Log.e("Spotify", "Error response from Spotify: $errorBody")
                    callback("Error: ${response.code} - $errorBody")
                }
            }
        })
    }
}
