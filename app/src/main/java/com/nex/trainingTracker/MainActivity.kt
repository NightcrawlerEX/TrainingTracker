package com.nex.trainingTracker

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.nex.trainingTracker.databinding.ActivityMainBinding
import android.view.MenuItem
import android.util.Log
import android.app.PendingIntent
import java.util.*


//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.WorkInfo
//import androidx.work.WorkManager
//import java.util.concurrent.TimeUnit

//import androidx.fragment.app.FragmentManager

private const val TAG = "MainActivityLog" //for debugging
private const val ALARM_REQUEST_CODE = 1000
/*
ERROR       - Log.e(TAG, "")
WARN        - Log.w(TAG, "")
INFO        - Log.i(TAG, "")
DEBUG       - Log.d(TAG, "")
VERBOSE     - Log.v(TAG, "")

DEBUG and VERBOSE are not present in release builds
 */

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val uniqueString = "TrainingTracker"//unique string for shared preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_steps, R.id.nav_running, R.id.nav_foodDiary, R.id.nav_profile
            ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        Log.d(TAG, "onCreate")

        this.createAlarmForSteps()
    }//end onCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }//end onCreateOptionsMenu

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }//end onSupportNavigateUp

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            Snackbar.make(binding.appBarMain.toolbar, "Todo: Put an action here", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            true
        }
        R.id.action_changeUnits -> {
            Log.d(TAG, "Change Units selected from action bar")
            val preferences = getPreferences(MODE_PRIVATE)
            val currentUser = preferences.getString("currentUser","")
            if(currentUser.isNullOrBlank()){
                Log.d(TAG, "Could not find current user")
                true
            }//endif
            Log.d(TAG, "currentUser: $currentUser")
            val sharedPreferencesFilename = uniqueString + currentUser
            Log.d(TAG, "sharedPreferencesFilename: $sharedPreferencesFilename")
            val sharedPreferences = getSharedPreferences(sharedPreferencesFilename, MODE_PRIVATE)
            val systemOfMeasurement = sharedPreferences.getString("systemOfMeasurement", "")
            Log.d(TAG, "systemOfMeasurement: $systemOfMeasurement")
            val sharedPreferencesEdit = sharedPreferences.edit()
            if(systemOfMeasurement == "imperial"){
                Log.d(TAG, "> Changing to metric")
                sharedPreferencesEdit.putString("systemOfMeasurement", "metric")
            }
            else{
                Log.d(TAG, "> Changing to imperial")
                sharedPreferencesEdit.putString("systemOfMeasurement", "imperial")
            }//endif
            sharedPreferencesEdit.apply()
            true
        }
        else -> {
            //if we get here, the users action was not recognized
            super.onOptionsItemSelected(item)
        }
    }//end onOptionsItemSelected

    private fun createAlarmForSteps(){
        Log.d(TAG, "createAlarmForSteps")

        val calendar = Calendar.getInstance()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, StepAlarmReceiver::class.java);
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }//end createAlarmForSteps
}//end class MainActivity