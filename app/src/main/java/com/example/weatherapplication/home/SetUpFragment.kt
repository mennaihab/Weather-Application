package com.example.weatherapplication.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.FragmentHomeBinding


class SetUpFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedPreference: Int = 0
    private lateinit var selectedp: String
    private lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withMultiChoiceList(view)
    }



    fun withMultiChoiceList(view: View) {
        val items = arrayOf("Gps","Map")

        selectedp = items[selectedPreference].toString()
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("choose your preferences")
            setSingleChoiceItems(items, selectedPreference) { dialog_, which ->
                selectedPreference = which
                selectedp = items[which]
            }
            builder.setPositiveButton("Ok") { dialog, which ->
                if (selectedp.equals("Gps")){
                    val action=SetUpFragmentDirections.actionSetUpFragmentToHomeFragment()
                    findNavController().navigate(action)

                }

            }
        }

        builder.show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}