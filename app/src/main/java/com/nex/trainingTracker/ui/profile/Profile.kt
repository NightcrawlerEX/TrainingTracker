package com.nex.trainingTracker.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nex.trainingTracker.databinding.FragmentProfileBinding

private const val TAG = "ProfileFragmentLog" //for debugging
/*
ERROR       - Log.e(TAG, "")
WARN        - Log.w(TAG, "")
INFO        - Log.i(TAG, "")
DEBUG       - Log.d(TAG, "")
VERBOSE     - Log.v(TAG, "")

DEBUG and VERBOSE are not present in release builds
 */

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val uniqueString = "TrainingTracker"//unique string for shared preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreate")
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textProfile
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        this.loadData()
        return root
    }//end onCreateView

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView")
    }//end onDestroyView

    override fun onResume()
    {
        super.onResume()
        Log.d(TAG, "onResume")
    }//end onResume

    private fun loadData()
    {
        Log.d(TAG, "loadData")
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val currentUser = preferences.getString("currentUser","")
        if(currentUser.isNullOrBlank()){
            Log.d(TAG, "Could not find current user")
            return
        }//endif
        Log.d(TAG, "currentUser: $currentUser")
        val sharedPreferencesFilename = uniqueString + currentUser
        val sharedPreferences = requireActivity().getSharedPreferences(sharedPreferencesFilename, Context.MODE_PRIVATE)
        val weightMetric = sharedPreferences.getFloat("weightMetric", 0f)
        val weightImperial = sharedPreferences.getFloat("weightImperial",0f)
        val heightMetric = sharedPreferences.getFloat("heightMetric", 0f)
        val heightImperial = sharedPreferences.getFloat("heightImperial",0f)
        val bmrMetric = sharedPreferences.getFloat("bmrMetric",0f)
        val bmrImperial = sharedPreferences.getFloat("bmrImperial",0f)
        val targetMetric = sharedPreferences.getFloat("targetMetric",0f)
        val targetImperial = sharedPreferences.getFloat("targetImperial",0f)
        Log.d(TAG, "weightMetric: $weightMetric")
        Log.d(TAG, "weightImperial: $weightImperial")
        Log.d(TAG, "heightMetric: $heightMetric")
        Log.d(TAG, "heightImperial: $heightImperial")
        Log.d(TAG, "bmrMetric: $bmrMetric")
        Log.d(TAG, "bmrImperial $bmrImperial")
        Log.d(TAG, "targetMetric $targetMetric")
        Log.d(TAG, "targetImperial $targetImperial")
        //set UI
        binding.profileWeightMetric.text = weightMetric.toString() + "kg"
        binding.profileWeightImperial.text = weightImperial.toString() + "lbs"
        binding.profileHeightMetric.text = heightMetric.toString() + "cm"
        binding.profileHeightImperial.text = heightImperial.toString() + "ft"
        binding.profileBmrMetric.text = bmrMetric.toString() + "kj"
        binding.profileBmrImperial.text = bmrImperial.toString() + "kcal"
        binding.profileEnergyTargetMetric.text = targetMetric.toString() + "kj"
        binding.profileEnergyTargetImperial.text = targetImperial.toString() + "kcal"
    }//end loadData

    /*
    private fun saveTestData()
    {
        Log.d(TAG, "saveTestData")
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
        sharedPreferencesEdit.putFloat("weightMetric", 82.0f)
        sharedPreferencesEdit.putFloat("weightImperial", 180.779f)
        sharedPreferencesEdit.putFloat("heightMetric", 193.0f)
        sharedPreferencesEdit.putFloat("heightImperial", 6.332f)
        sharedPreferencesEdit.putFloat("bmrMetric", 9660.0f)
        sharedPreferencesEdit.putFloat("bmrImperial", 2300.0f)
        sharedPreferencesEdit.putFloat("targetMetric", 9660.0f)
        sharedPreferencesEdit.putFloat("targetImperial", 2300.0f)
        sharedPreferencesEdit.apply()
    }//end saveTestData*/
}//end class ProfileFragment