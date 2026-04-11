package com.example.nurdor_volunteer_app_v3.fragment.pagers

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nurdor_volunteer_app_v3.fragment.PresentVolunteersFragment
import com.example.nurdor_volunteer_app_v3.fragment.StandsFragment
import com.example.nurdor_volunteer_app_v3.fragment.TotalDonationsFragment

class StatisticsPagerAdapter(private val idEvent: Int, activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        Log.i("StatisticsActivity", "create fragment")
        return when(position) {
            0 -> PresentVolunteersFragment.newInstance(idEvent)
            1 -> StandsFragment.newInstance(idEvent)
            2 -> TotalDonationsFragment.newInstance(idEvent)
            else -> throw IllegalStateException("Non existing position for admin tab layout!")
        }
    }

    override fun getItemCount(): Int = 3


}