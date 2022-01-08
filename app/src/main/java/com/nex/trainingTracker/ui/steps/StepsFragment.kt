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
import com.google.android.material.snackbar.Snackbar

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
    private val binding get() = _binding!!
    //step tracking stuff
    private var sensorManager: SensorManager? = null
    private var bIsTrackingSteps = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var stepsToday = 0
    private var stepEnergyMetric = 0.0f//energy used for one step (kilojoules) [from preferences]
    private var stepEnergyImperial = 0.0f//energy used for one step (kcal) [from preferences]
    private var exerciseStepsMetric = 0.0f//total kj used for steps today
    private var exerciseStepsImperial = 0.0f

    private val uniqueString = "TrainingTracker"//unique string for shared preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView()")
        stepsViewModel = ViewModelProvider(this).get(StepsViewModel::class.java)
        _binding = FragmentStepsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadData()
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return root
    }//end onCreateView

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }//end onDestroyView

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        bIsTrackingSteps = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            //no step sensor available
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }//endif
        loadData()

    }//end onResume

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
        sensorManager?.unregisterListener(this)
    }//end onStop

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(TAG, "onSensorChanged()")
        if (bIsTrackingSteps) {
            totalSteps = event!!.values[0]
            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val deltaSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            stepsToday += deltaSteps
            previousTotalSteps = totalSteps
            exerciseStepsMetric = stepsToday * stepEnergyMetric
            exerciseStepsImperial = stepsToday * stepEnergyImperial
            // It will show the current steps to the user
            binding.tvStepsTaken.text = "$stepsToday"
            binding.exerciseStepsMetric.text = exerciseStepsMetric.toString() + "kj"
            binding.exerciseStepsImperial.text = exerciseStepsImperial.toString() + "kcal"
            Log.d(TAG,"deltaSteps: $deltaSteps ,stepsToday: $stepsToday , totalSteps: $totalSteps")
            Log.d(TAG, "kj: $exerciseStepsMetric , kcal: $exerciseStepsImperial")
            saveData()
        }//endif
    }//end onSensorChanged

    private fun saveData() {
        Log.d(TAG, "saveData()")
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","")
        if(currentUser.isNullOrBlank()){
            Log.d(TAG, "Could not find current user")
            return
        }//endif
        Log.d(TAG, "currentUser: $currentUser")
        val sharedPreferencesFilename = uniqueString + currentUser
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPreferencesFilename, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("stepsToday", stepsToday)
        editor.putFloat("previousTotalSteps", previousTotalSteps)
        editor.putFloat("exerciseStepsMetric", exerciseStepsMetric)
        editor.putFloat("exerciseStepsImperial", exerciseStepsImperial)
        editor.apply()
    }//end saveData

    private fun loadData() {
        Log.d(TAG, "loadData()")
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
        stepsToday = sharedPreferences.getInt("stepsToday", 0)
        previousTotalSteps = sharedPreferences.getFloat("previousTotalSteps", 0.0f)
        stepEnergyMetric = sharedPreferences.getFloat("stepEnergyMetric", 0.0f)
        stepEnergyImperial = sharedPreferences.getFloat("stepEnergyImperial", 0.0f)
        Log.d(TAG,"stepsToday: $stepsToday")
        Log.d(TAG, "previousTotalSteps: $previousTotalSteps")
        Log.d(TAG, "stepEnergyMetric: $stepEnergyMetric")
        Log.d(TAG, "stepEnergyImperial: $stepEnergyImperial")
    }//end loadData

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }//end onAccuracyChanged

    private fun saveTestData() {
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
        /*sharedPreferencesEdit.putString("systemOfMeasurement", "metric")
        sharedPreferencesEdit.putFloat("foodTotalMetric", 1421.5f)
        sharedPreferencesEdit.putFloat("foodTotalImperial", 338.45f)
        sharedPreferencesEdit.putFloat("exerciseTotalMetric", 1786.2f)
        sharedPreferencesEdit.putFloat("exerciseTotalImperial", 425.29f)
        sharedPreferencesEdit.putFloat("remainingMetric",10024.728f)
        sharedPreferencesEdit.putFloat("remainingImperial",2386.84f)*/
        sharedPreferencesEdit.putFloat("stepEnergyMetric",0.209662f)
        sharedPreferencesEdit.putFloat("stepEnergyImperial",0.04991f)
        sharedPreferencesEdit.apply()
    }//end saveTestData
}//end class HomeFragment