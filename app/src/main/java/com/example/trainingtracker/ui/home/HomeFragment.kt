package com.example.trainingtracker.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.trainingtracker.R
import com.example.trainingtracker.databinding.FragmentHomeBinding
//for logging
import android.util.Log
import android.widget.Toast

private const val TAG = "StepFragmentLog" //for debugging
/*
ERROR       - Log.e(TAG, "")
WARN        - Log.w(TAG, "")
INFO        - Log.i(TAG, "")
DEBUG       - Log.d(TAG, "")
VERBOSE     - Log.v(TAG, "")

DEBUG and VERBOSE are not present in release builds
 */

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //step tracking stuff
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private lateinit var tv_stepsTaken: TextView;


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        //Log.i(TAG, "onCreateView")
        loadData()
        resetSteps()
        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        //sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return root
    }//end onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_stepsTaken = view.findViewById<TextView>(R.id.tv_stepsTaken)
    }//end onViewCreated

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }//end onDestroyView

    override fun onResume() {
        super.onResume()
        running = true
        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            //Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }//endif
    }//end onResume

    override fun onSensorChanged(event: SensorEvent?) {
        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView

        //var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)

        if (running) {
            totalSteps = event!!.values[0]
            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            // It will show the current steps to the user
            tv_stepsTaken.text = ("$currentSteps")
        }//endif
    }//end onSensorChanged

    fun resetSteps() {
        /*var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        tv_stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tv_stepsTaken.text = 0.toString()
            // This will save the data
            saveData()
            true
        }*/
    }//end resetSteps

    private fun saveData() {
        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }//end saveData

    private fun loadData() {
        // In this function we will retrieve data
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }//end loadData

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }//end onAccuracyChanged
}//end class HomeFragment