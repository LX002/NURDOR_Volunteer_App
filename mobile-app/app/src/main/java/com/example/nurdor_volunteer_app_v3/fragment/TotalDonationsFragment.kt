package com.example.nurdor_volunteer_app_v3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.nurdor_volunteer_app_v3.fragment.dialog.SetUpStandsForEventDialog

class TotalDonationsFragment: Fragment() {

    companion object {
        fun newInstance(idEvent: Int): TotalDonationsFragment {
            return TotalDonationsFragment().apply {
                arguments = Bundle().apply {
                    putInt("idEvent", idEvent)
                }
            }
        }
    }
}