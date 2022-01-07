package com.nex.trainingTracker.ui.running

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nex.trainingTracker.databinding.FragmentRunningBinding

private const val TAG = "RunningFragmentLog" //for debugging
/*
ERROR       - Log.e(TAG, "")
WARN        - Log.w(TAG, "")
INFO        - Log.i(TAG, "")
DEBUG       - Log.d(TAG, "")
VERBOSE     - Log.v(TAG, "")

DEBUG and VERBOSE are not present in release builds
 */

class RunningFragment : Fragment() {

    private lateinit var runningViewModel: RunningViewModel
    private var _binding: FragmentRunningBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        runningViewModel =
            ViewModelProvider(this).get(RunningViewModel::class.java)
        _binding = FragmentRunningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textGallery
        runningViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }//endOnCreateView

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }//end onDestroyView
}//end class GalleryFragment