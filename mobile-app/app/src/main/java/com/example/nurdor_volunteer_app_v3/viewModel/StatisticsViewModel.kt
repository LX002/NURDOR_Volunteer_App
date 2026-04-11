package com.example.nurdor_volunteer_app_v3.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class StatisticsViewModel(application: Application): AndroidViewModel(application) {

    val selectedTabPosition = MutableLiveData(0)
}