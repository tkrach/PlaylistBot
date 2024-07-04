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
    fun generatePlaylist(description: String, numTracks: Int, callback: (String) -> Unit) {
        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().put("role", "system").put("content", "You are being used to " +
                        "generate a playlist for users."))
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
}
