package com.example.playlistbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val generatePlaylistButton: Button = view.findViewById(R.id.generatePlaylistButton)
        generatePlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_generatePlaylistFragment)
        }

        val enhancePlaylistButton: Button = view.findViewById(R.id.enhancePlaylistButton)
        enhancePlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_enhancePlaylistFragment)
        }

        val chatButton: Button = view.findViewById(R.id.chatButton)
        chatButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
        }

        return view
    }
}
