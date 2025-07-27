package com.example.rma_nurdor_project_v2.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventPagerAdapter(private val idEvent: Int, activity: FragmentActivity): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> QrFragment()
            1 -> EventVolunteersFragment.newInstance(idEvent) //napravi volonter fragment
            else -> throw IllegalStateException("Non existing position for tab layout")
        }
    }

}