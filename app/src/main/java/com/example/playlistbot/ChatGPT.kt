package com.example.playlistbot

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatGPT(private val context: Context) {
    private val client = OkHttpClient()
    private val apiKey = context.getString(R.string.openAI_key)
    private val spotifyService = Spotify(context)

    fun chatWithGPT(query: String, callback: (String) -> Unit) {
        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                put(JSONObject().put("role", "user").put("content", query))
            })
        }

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatGPT", "Failed to connect to ChatGPT: ${e.message}")
                callback("Failed to connect to ChatGPT: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            Log.d("ChatGPT", "Received response from GPT: $message")
                            callback(message)
                        } catch (e: Exception) {
                            Log.e("ChatGPT", "Failed to parse response: ${e.message}")
                            callback("Failed to parse response from ChatGPT")
                        }
                    } ?: callback("Empty response from ChatGPT")
                } else {
                    Log.e("ChatGPT", "Error response from ChatGPT: ${response.message}")
                    callback("Error: ${response.message}")
                }
            }
        })
    }

    fun createPlaylist(playlistName: String, description: String, numTracks: Int, callback: (String) -> Unit) {
        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().put("role", "system").put("content", "You are being used to " +
                        "generate a spotify playlist for users. Please format your message in a numbered list " +
                        "with exactly $numTracks entries, \"Please only provide song and artist names surrounded by double quotes. Like this \\\"Accordion \"\n" +
                        "\"- MF DOOM\\\" \" Also if the song is in another language please put it in the original language, like in kanji or katakana ONLY. If you " +
                        "can't understand a user's input, just generate a playlist based on whatever they entered." +
                        "You have no functionality to speak with the user. Additionally, try your best to include" +
                        "tracks that are found on the spotify platform, as tracks from soundcloud, youtube, etc. " +
                        "cannot be added. "))
                put(JSONObject().put("role", "user").put("content", description))
            })
        }

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatGPT", "Failed to connect to ChatGPT: ${e.message}")
                callback("Failed to connect to ChatGPT: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            Log.d("ChatGPT", "Received response from GPT: $message")

                            val tracks = extractTracks(message)
                            Log.d("ChatGPT", "Extracted tracks: $tracks")
                            spotifyService.getCurrentUserId { userID ->
                                if (userID != null) {
                                    Log.d("ChatGPT", "User ID: $userID")
                                    spotifyService.findTrackIDs(tracks) { trackIDs ->
                                        spotifyService.createSpotifyPlaylist(
                                            userID,
                                            playlistName,
                                            description,
                                            trackIDs
                                        ) { playlistResponse ->
                                            callback(playlistResponse)
                                        }
                                    }
                                    }
                                }
                        } catch (e: Exception) {
                            Log.e("ChatGPT", "Failed to parse response: ${e.message}")
                            callback("Failed to parse response from ChatGPT")
                        }
                    } ?: callback("Empty response from ChatGPT")
                } else {
                    Log.e("ChatGPT", "Error response from ChatGPT: ${response.message}")
                    callback("Error: ${response.message}")
                }
            }
        })
    }

    private fun extractTracks(response: String): List<String> {
        Log.d("ChatGPT", "Extracting tracks from response: $response")
        val regex = Regex("\"(.*?)\"")
        val matches = regex.findAll(response)
        Log.d("ChatGPT", "Matches found: ${matches.count()}")
        return matches.map { matchResult ->
            matchResult.groupValues[1].trim()
        }.toList()
    }


}
