package com.example.rma_nurdor_project_v2.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> CreateEventFragment()
            1 -> EventArchiveFragment()
            else -> throw IllegalStateException("Non existing position for admin tab layout!")
        }
    }
}