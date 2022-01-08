package com.nex.trainingTracker.ui.steps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
//import com.nex.trainingTracker.R
import com.nex.trainingTracker.databinding.FragmentStepsBinding
//for logging
import android.util.Log

private const val TAG = "StepFragmentLog" //for debugging
/*
ERROR       - Log.e(TAG, "")
WARN        - Log.w(TAG, "")
INFO        - Log.i(TAG, "")
DEBUG       - Log.d(TAG, "")
VERBOSE     - Log.v(TAG, "")

DEBUG and VERBOSE are not present in release builds
 */

class StepsFragment : Fragment(), SensorEventListener {

    private lateinit var stepsViewModel: StepsViewModel
    private var _binding: FragmentStepsBinding? = null

    private val uniqueString = "TrainingTracker"//unique string for shared preferences

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //step tracking stuff
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        stepsViewModel =
            ViewModelProvider(this).get(StepsViewModel::class.java)

        _binding = FragmentStepsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        //Log.i(TAG, "onCreateView")
        loadData()
        // Adding a context of SENSOR_SERVICE as Sensor Manager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return root
    }//end onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        if (running) {
            totalSteps = event!!.values[0]
            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            // It will show the current steps to the user
            binding.tvStepsTaken.text = "$currentSteps"
        }//endif
    }//end onSensorChanged

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
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","")
        if(currentUser.isNullOrBlank()){
            Log.d(TAG, "Could not find current user")
            return
        }//endif
        Log.d(TAG, "currentUser: $currentUser")

        val sharedPreferencesFilename = uniqueString + currentUser
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPreferencesFilename, Context.MODE_PRIVATE)
        val systemOfMeasurement = sharedPreferences.getString("systemOfMeasurement", "")
        Log.d(TAG, "systemOfMeasurement: $systemOfMeasurement")

        if(systemOfMeasurement == "imperial"){
            binding.goalCardTitleMetric.visibility = View.INVISIBLE
            binding.goalCardTitleImperial.visibility = View.VISIBLE
            val targetImperial = sharedPreferences.getFloat("targetImperial", 0.0f)
            val foodTotalImperial = sharedPreferences.getFloat("foodTotalImperial", 0.0f)
            val exerciseTotalImperial = sharedPreferences.getFloat("exerciseTotalImperial", 0.0f)
            val remainingImperial = sharedPreferences.getFloat("remainingImperial", 0.0f)
            Log.d(TAG, "targetImperial: $targetImperial")
            Log.d(TAG, "foodTotalImperial: $foodTotalImperial")
            Log.d(TAG, "exerciseTotalImperial: $exerciseTotalImperial")
            Log.d(TAG, "remainingImperial: $remainingImperial")
            binding.goalCardTargetValue.text = targetImperial.toString()
            binding.goalCardFoodValue.text = foodTotalImperial.toString()
            binding.goalCardExerciseValue.text = exerciseTotalImperial.toString()
            binding.goalCardRemainingValue.text = remainingImperial.toString()
        }else{
            binding.goalCardTitleImperial.visibility = View.INVISIBLE
            binding.goalCardTitleMetric.visibility = View.VISIBLE
            val targetMetric = sharedPreferences.getFloat("targetMetric", 0.0f)
            val foodTotalMetric = sharedPreferences.getFloat("foodTotalMetric", 0.0f)
            val exerciseTotalMetric = sharedPreferences.getFloat("exerciseTotalMetric",0.0f)
            val remainingMetric = sharedPreferences.getFloat("remainingMetric", 0.0f)
            Log.d(TAG, "targetMetric: $targetMetric")
            Log.d(TAG, "foodTotalMetric: $foodTotalMetric")
            Log.d(TAG, "exerciseTotalMetric: $exerciseTotalMetric")
            Log.d(TAG, "remainingMetric: $remainingMetric")
            binding.goalCardTargetValue.text = targetMetric.toString()
            binding.goalCardFoodValue.text = foodTotalMetric.toString()
            binding.goalCardExerciseValue.text = exerciseTotalMetric.toString()
            binding.goalCardRemainingValue.text = remainingMetric.toString()
        }//endif
    }//end loadData

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }//end onAccuracyChanged

    /*private fun saveTestData() {
        Log.d(TAG, "saveTestData()")
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","")
        if(currentUser.isNullOrBlank()){
            Log.d(TAG, "Could not find current user")
            return
        }//endif
        Log.d(TAG, "currentUser: $currentUser")
        val sharedPreferencesFilename = uniqueString + currentUser
        Log.d(TAG, "sharedPreferencesFilename: $sharedPreferencesFilename")
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPreferencesFilename, Context.MODE_PRIVATE)
        val sharedPreferencesEdit = sharedPreferences.edit()
        sharedPreferencesEdit.putString("systemOfMeasurement", "metric")
        sharedPreferencesEdit.putFloat("foodTotalMetric", 1421.5f)
        sharedPreferencesEdit.putFloat("foodTotalImperial", 338.45f)
        sharedPreferencesEdit.putFloat("exerciseTotalMetric", 1786.2f)
        sharedPreferencesEdit.putFloat("exerciseTotalImperial", 425.29f)
        sharedPreferencesEdit.putFloat("remainingMetric",10024.728f)
        sharedPreferencesEdit.putFloat("remainingImperial",2386.84f)
        sharedPreferencesEdit.apply()
    }//end saveTestData*/
}//end class HomeFragment