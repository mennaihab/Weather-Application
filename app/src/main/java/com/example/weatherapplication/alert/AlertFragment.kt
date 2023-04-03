package com.example.weatherapplication.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.databinding.FragmentAlertBinding

class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertAdapter: AlertAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val view = binding.root
        alertViewModel = ViewModelProvider(this)[AlertViewModel::class.java]
        return view
    }

}