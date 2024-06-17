package com.example.playlistbot

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ChatGPT {
    private val client = OkHttpClient()
    private val apiKey = "YOUR_OPENAI_API_KEY"

    fun chatWithGPT(query: String, callback: (String) -> Unit) {
        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo")
        json.put("messages", listOf(
            JSONObject().put("role", "system").put("content", "You are a helpful assistant."),
            JSONObject().put("role", "user").put("content", query)
        ))

        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
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
}
