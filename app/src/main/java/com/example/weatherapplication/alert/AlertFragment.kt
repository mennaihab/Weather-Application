package com.example.weatherapplication.alert

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.weatherapplication.R
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.FragmentAlertBinding
import com.example.weatherapplication.local.AlertsRoamState
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.repo.Repositry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null
    private lateinit var binding: FragmentAlertBinding
    private lateinit var alertsViewModel: AlertsViewModel
    private lateinit var alertsViewModelFactory: AlertsViewModelFactory
    private lateinit var alertAdapter: AlertAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        binding = _binding!!
        val view = binding.root
        alertsViewModelFactory = AlertsViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(requireContext()),
                LocalSourceImp.getInstance(requireContext()),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
            )
        )
        val pd = ProgressDialog(context)
        alertsViewModel =
            ViewModelProvider(this, alertsViewModelFactory)[AlertsViewModel::class.java]
        checkAlertsPermission()
        alertsViewModel.getAlerts()
        binding.addAlertBtn.setOnClickListener {
            val action =AlertFragmentDirections.actionAlertFragmentToSelectAlertSpecificationsFragment()
            Navigation.findNavController(it).navigate(action)
        }
        lifecycleScope.launch {
            alertsViewModel.alertsStateaFlow.collect {
                when (it) {
                    is AlertsRoamState.Loading -> {
                        pd.setMessage("loading")
                        pd.show()
                    }
                    is AlertsRoamState.Failure -> {
                        pd.dismiss()
                        Log.i("menna", "fail")
                        Toast.makeText(
                            context,
                            "Can't get data from alerts",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is AlertsRoamState.Success -> {
                        Log.i("menna", "success")
                        pd.dismiss()
                        binding.alertRec.apply {
                            this.adapter = AlertAdapter {
                                //delete it from worker
                                Utils.canelAlarm(requireContext(),it.toString(),it.startTime.toInt())
                                WorkManager.getInstance(context.applicationContext).cancelAllWorkByTag(it.startTime.toString())
                                Toast.makeText(requireContext(),"Your alarm been deleted", Toast.LENGTH_SHORT).show()
                                alertsViewModel.deleteAlerts(it)
                            }
                            alertAdapter = this.adapter as AlertAdapter
                            layoutManager = LinearLayoutManager(requireContext())
                                .apply { orientation = RecyclerView.VERTICAL }

                        }
                        alertAdapter.submitList(it.data)
//                        if (it.data?.isEmpty() == false) {
//
//                            alertAdapter.submitList(it.data)
//                        }
//
//
//                        else {
//                            alertAdapter.submitList(it.data)
//                        }


                    }
                }
            }

        }

        return view
    }

    private fun checkAlertsPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.need_permission))
                .setMessage(getString(R.string.want_app_to_access_alerts))
                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, i: Int ->
                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                    dialog.dismiss()
                }.setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                    val action = AlertFragmentDirections.actionAlertFragmentToHomeFragment()
                    view?.let { Navigation.findNavController(it).navigate(action) }

                }.show()
        }
    }

}