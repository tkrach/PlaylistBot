package com.example.playlistbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class LandingFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_landing, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val authenticateButton: Button = view.findViewById(R.id.authenticateButton)
        authenticateButton.setOnClickListener {
            viewModel.authenticateSpotify(requireActivity() as ComponentActivity)
        }

        viewModel.authenticationState.observe(viewLifecycleOwner, { authenticated ->
            if (authenticated) {
                findNavController().navigate(R.id.action_landingFragment_to_mainFragment)
            }
        })

        return view
    }
}
