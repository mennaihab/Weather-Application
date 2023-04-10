package com.example.weatherapplication.alert

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.work.*
import com.example.weatherapplication.Constants
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.FragmentSelectAlertSpecificationsBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.repo.Repositry
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SelectAlertSpecificationsFragment : Fragment() {

    private var _binding: FragmentSelectAlertSpecificationsBinding? = null
    private lateinit var binding: FragmentSelectAlertSpecificationsBinding
   // private var alertObject =AlertsData(0,0,"",33.3,34.2)
    private lateinit var alertsViewModel:AlertsViewModel
    private lateinit var alertsViewModelFactory:AlertsViewModelFactory
    private lateinit var selectAlert:SelectAlertSpecificationsFragmentViewModel
    private lateinit var startCalender:Calendar
    private lateinit var endCalender:Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectAlertSpecificationsBinding.inflate(inflater, container, false)
        binding = _binding!!
        val view = binding.root
  val repo =  Repositry.getInstance(
      WeatherClient.getInstance(requireContext()),
      LocalSourceImp.getInstance(requireContext()),
      PreferenceManager.getDefaultSharedPreferences(requireContext())
  )
        alertsViewModelFactory = AlertsViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(requireContext()),
                LocalSourceImp.getInstance(requireContext()),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
            )
        )
        val pd = ProgressDialog(context)
        alertsViewModel =
            ViewModelProvider(requireActivity(), alertsViewModelFactory)[AlertsViewModel::class.java]
        selectAlert =  ViewModelProvider(this)[SelectAlertSpecificationsFragmentViewModel::class.java]

        val alarm = repo.getAlertSettings()
        startCalender = Calendar.getInstance()
        endCalender = Calendar.getInstance()
        binding.startDateBtn.text = getCurrentDate()
        binding.endDateBtn.text = getCurrentDate()
        binding.startTimeBtn.text = getCurrentTime().first
        binding.endTimeBtn.text =getCurrentTime2().first
        if(Utils.isOnline(requireContext()))
        {
            binding.locationBtn.isEnabled=true
            binding.saveAlarmBtn.isEnabled=true
        }
        else

        {
            binding.locationBtn.isEnabled=false
            binding.saveAlarmBtn.isEnabled=false
            Toast.makeText(requireContext(),"No Connection ",Toast.LENGTH_SHORT).show()
        }
        if(alarm?.isALarm == true&&alarm.isNotification==false)
        {
            binding.alarmBtn.isChecked=true
        }
        if(alarm?.isALarm == false && alarm.isNotification)
        {
            binding.notificatioBtn.isChecked=true
        }

        binding.startDateBtn.setOnClickListener {
            pickDateTime(binding.startDateBtn, binding.startTimeBtn, startCalender)
        }
        binding.endDateBtn.setOnClickListener {
            pickDateTime(binding.endDateBtn, binding.endTimeBtn, endCalender)
        }
        binding.locationBtn.text =
            Utils.getAddressEnglish(requireContext(), alarm?.lat, alarm?.lon)
        binding.locationBtn.setOnClickListener {
            val action =
                SelectAlertSpecificationsFragmentDirections.actionSelectAlertSpecificationsFragmentToMapsFragment().apply {
                    isFromAlert = true
                }
            NavHostFragment.findNavController(this).navigate(action)
        }
        val args: SelectAlertSpecificationsFragmentArgs by navArgs()
        if(args!=null) {
            binding.locationBtn.text = args.location.toString()
        }
        binding.saveAlarmBtn.setOnClickListener {
            val args: SelectAlertSpecificationsFragmentArgs by navArgs()
            val lat = args.latitude
            if (alarm != null) {
                alarm.lat = args.latitude.toDouble()
                alarm.lon = args.longitude.toDouble()
            }
            var alert = AlertsData(
                startTime = startCalender.timeInMillis,
                endTime = endCalender.timeInMillis,
                lat = alarm!!.lat,
                lon = alarm.lon,
                location = Utils.getAddressEnglish(requireContext(), alarm!!.lat, alarm.lon)
            )

            Log.i("fragment",endCalender.timeInMillis.toString())
            if (alert.startTime < alert.endTime) {
                if (binding.alarmBtn.isChecked) {
                    alarm?.isALarm = true
                    alarm?.isNotification = false
                }
                if (binding.notificatioBtn.isChecked) {
                    alarm?.isALarm = false
                    alarm?.isNotification = true
                }
                alarm?.let { repo.saveAlertSettings(it) }
                alertsViewModel.insertAlert(alert)

                val inputData = Data.Builder()
                inputData.putString(Constants.Alert, Gson().toJson(alert).toString())

                val myConstraints: Constraints = Constraints.Builder()
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                Toast.makeText(context, "Daily", Toast.LENGTH_SHORT).show()

                val myWorkRequest =
                    PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.DAYS).setConstraints(
                        myConstraints
                    ).setInputData(inputData.build()).addTag(alert.startTime.toString()).build()
                WorkManager.getInstance(requireContext().applicationContext)
                    .enqueueUniquePeriodicWork(
                        alert.startTime.toString(),
                        ExistingPeriodicWorkPolicy.REPLACE,
                        myWorkRequest
                    )
                val action = SelectAlertSpecificationsFragmentDirections.actionSelectAlertSpecificationsFragmentToAlertFragment()
                Navigation.findNavController(it).navigate(action)
               // dismiss()
            } else {
                Toast.makeText(
                    context,
                    "Please specify the end time of your alert",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        return view
    }

    private fun pickDateTime(tvdate: TextView, tvTime: TextView, calendar: Calendar) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)
        var pickedDateTime: Calendar
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        tvdate.text = Utils.pickedDateFormatDate(pickedDateTime.time)
                        tvTime.text = Utils.pickedDateFormatTime(pickedDateTime.time)
                        calendar.time = pickedDateTime.time

                    },
                    startHour,
                    startMinute,
                    false
                ).show()
            },
            startYear,
            startMonth,
            startDay
        ).show()
    }

    fun getCurrentTime(): Pair<String,Long> {
        val calender=Calendar.getInstance()
        val currentTime = calender.time
        val sdf = SimpleDateFormat("HH:mm")
        val alert=calender.timeInMillis
        return Pair( sdf.format(currentTime),alert)

    }


    fun getCurrentTime2(): Pair<String,Long> {
        val calender=Calendar.getInstance()
        val currentTime = calender.time
        val sdf = SimpleDateFormat("HH:mm")
        val alert=calender.timeInMillis
        return Pair( sdf.format(currentTime),alert)

    }

    fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(currentTime)
    }


}
        /*
        binding.startDateBtn.setOnClickListener {
         showDatePicker1()
        }
        binding.endDateBtn.setOnClickListener {
            showDatePicker2()

        }
        binding.timeBtn.setOnClickListener {
            showTimePicker()
        }


        binding.locationBtn.setOnClickListener {
            val action = SelectAlertSpecificationsFragmentDirections.actionSelectAlertSpecificationsFragmentToMapsFragment().apply {
                isFromAlert = true
            }
            Navigation.findNavController(it).navigate(action)
        }

        binding.saveAlarmBtn.setOnClickListener {

            val arg: SelectAlertSpecificationsFragmentArgs by navArgs()
            alertObject.lat = arg.latitude.toDouble()
            alertObject.lon = arg.longitude.toDouble()
            alertObject.location = arg.location.toString()
            alertsViewModel.insertAlerts(alertObject)
            scheduleWork(alertObject.startDate,alertObject.endDate,alertObject.time,"tag")

            val action = SelectAlertSpecificationsFragmentDirections.actionSelectAlertSpecificationsFragmentToAlertFragment()
            Navigation.findNavController(it).navigate(action)
        }

        selectAlert.startDate.observe(viewLifecycleOwner) { data ->
            // UI UPDATE
            binding.startDateBtn.text= data.toString()
        }

        return view
    }

    private fun showTimePicker(){
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireActivity(),
            R.style.CustomDatePickerDialog,
            { timePicker, hourOfDay, minute ->
                var time = (TimeUnit.MINUTES.toSeconds(minute.toLong()) + TimeUnit.HOURS.toSeconds(hourOfDay.toLong()))
                time = time.minus(3600L * 2)
                binding.timeBtn.text = convertToTime(time)
                alertObject.timeString =convertToTime(time)
              alertObject.time = time
            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
        )
        timePickerDialog.show()
    }

    private fun scheduleWork(startTime: Long, endTime: Long,time:Long, tag: String) {

        val _Day_TIME_IN_MILLISECOND = 24 * 60 * 60 * 1000L
        val timeNow = Calendar.getInstance().timeInMillis

        val inputData = Data.Builder()
        inputData.putString(ID, tag)
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val myWorkRequest: WorkRequest = if ((endTime - startTime) < _Day_TIME_IN_MILLISECOND) {
            Log.d("TAG", "scheduleWork: one")
            OneTimeWorkRequestBuilder<AlertWorker>().addTag(tag).setInitialDelay(
                startTime - timeNow, TimeUnit.MILLISECONDS
            ).setInputData(
                inputData = inputData.build()
            ).setConstraints(constraints).build()

        } else {

            WorkManager.getInstance(requireContext()).enqueue(
                OneTimeWorkRequestBuilder<AlertWorker>().addTag(tag).setInitialDelay(
                    startTime - timeNow, TimeUnit.MILLISECONDS
                ).setInputData(
                    inputData = inputData.build()
                ).setConstraints(constraints).build()
            )

            Log.d("TAG", "scheduleWork: periodic")

            PeriodicWorkRequest.Builder(
                AlertWorker::class.java, 24L, TimeUnit.HOURS, 1L, TimeUnit.HOURS
            ).addTag(tag).setInputData(
                inputData = inputData.build()
            ).setConstraints(constraints).build()
        }
        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy - EEE", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun convertToTime(dt: Long): String {
        val date = Date(dt * 1000)
        val format = SimpleDateFormat("h:mm a", Locale("en"))
        return format.format(date)
    }

    fun getseconds(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        return calendar.timeInMillis / 1000
    }

    private fun showDatePicker1(){
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            R.style.CustomDatePickerDialog,
            { datePicker, year, month, day ->
                binding.startDateBtn.text = convertToDate(getseconds(year,month+1,day))
                alertObject.startDateString =convertToDate(getseconds(year,month+1,day))
                Log.i("alert1 ",  binding.startDateBtn.text.toString())
                alertObject.startDate=getseconds(year,month+1,day)
                selectAlert.startDate.postValue( alertObject.startDate)
                Log.i("alert1 ",    alertObject.startDate.toString())
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    private fun showDatePicker2(){
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            R.style.CustomDatePickerDialog,
            { datePicker, year, month, day ->
                binding.endDateBtn.text = convertToDate(getseconds(year,month+1,day))
                alertObject.endDateString =convertToDate(getseconds(year,month+1,day))
                alertObject.endDate=getseconds(year,month+1,day)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()


    }

    fun convertToDate(dt: Long): String {
        val date = Date(dt * 1000)
        val format = SimpleDateFormat("d MMM, yyyy", Locale("en"))
        return format.format(date)
    }

}

         */