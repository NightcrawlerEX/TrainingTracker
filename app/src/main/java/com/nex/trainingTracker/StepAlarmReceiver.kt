package com.nex.trainingTracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener

private const val TAG = "StepAlarmReceiverLog" //for debugging
/*
ERROR       - Log.e(TAG, "")
WARN        - Log.w(TAG, "")
INFO        - Log.i(TAG, "")
DEBUG       - Log.d(TAG, "")
VERBOSE     - Log.v(TAG, "")

 */
class StepAlarmReceiver: BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        var service = Intent(context, StepCountService::class.java)
        context?.startService(service)
    }//end onReceive
}//end class StepAlarmReceiver