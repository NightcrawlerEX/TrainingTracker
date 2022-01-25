package com.nex.trainingTracker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.os.IBinder
import android.util.Log

// https://developer.android.com/guide/components/services
private const val TAG = "StepCountServiceLog" //for debugging

internal class TriggerListener : TriggerEventListener() {
    override fun onTrigger(event: TriggerEvent) {
        Log.d(TAG, "TriggerListener::onTrigger")

        // As it is a one shot sensor, it will be canceled automatically.
        // SensorManager.requestTriggerSensor(this, mSigMotion); needs to
        // be called again, if needed.
    }//end onTrigger
}//end class TriggerListener

class StepCountService : Service() {

    private var sensorManager: SensorManager? = null
    private val mListener = TriggerListener()

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind")
        TODO("Return the communication channel to the service.")
    }//end onBind

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager?.requestTriggerSensor(mListener, stepSensor)
        //this.stopSelf()
    }//end onCreate


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }//end onDestroy
}//end class StepCountService

