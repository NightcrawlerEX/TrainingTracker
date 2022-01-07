package com.nex.trainingTracker.ui.foodDiary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodDiaryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Food Diary Fragment"
    }
    val text: LiveData<String> = _text
}//end SlideshowViewModel