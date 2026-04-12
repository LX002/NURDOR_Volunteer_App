package com.example.nurdor_volunteer_app_v3.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.fragment.PresentVolunteersFragment
import com.example.nurdor_volunteer_app_v3.fragment.StandsFragment
import com.example.nurdor_volunteer_app_v3.fragment.TotalDonationsFragment
import com.example.nurdor_volunteer_app_v3.fragment.pagers.StatisticsPagerAdapter
import com.example.nurdor_volunteer_app_v3.viewModel.StatisticsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO(): use three tabs for picked event here:
//  present volunteers (with contact options menu, search), (**use search for every activity where data is displayed in rc view, "live search")
//  stands with their current donations,
//  current total donations
//  use tab fragments template from previous version of the project!

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val statisticsViewModel = ViewModelProvider(this)[StatisticsViewModel::class]
        val idEvent = intent.getIntExtra("idEvent", 0)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarStatistics)
        setSupportActionBar(toolbar)


        val tabLayout = findViewById<ViewGroup>(R.id.tabLayout)
        val tabs = tabLayout.children.toList()
        if(resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            val pagerStatistics = findViewById<ViewPager2>(R.id.viewPagerStatistics)
            pagerStatistics.adapter = StatisticsPagerAdapter(idEvent, this)
            setUpTabLayoutMediator(tabLayout, pagerStatistics)
            setUpTabs(tabLayout, statisticsViewModel)
        } else {
            setUpCardsOnClickListeners(tabs, statisticsViewModel)
        }
    }

    private fun setUpCardsOnClickListeners(tabs: List<View>, statisticsViewModel: StatisticsViewModel) {
        tabs.forEachIndexed { index, card ->
            (card as CardView).setOnClickListener {
                statisticsViewModel.selectedTabPosition.value = index
            }
        }
        statisticsViewModel.selectedTabPosition.observe(this) { pos ->
            val idEvent = intent.getIntExtra("idEvent", 0)
            val fragmentToShow = when (pos) {
                0 -> PresentVolunteersFragment.newInstance(idEvent)
                1 -> StandsFragment.newInstance(idEvent)
                else -> IllegalArgumentException("Exception in StatisticsActivity: Invalid tab position!")
            } as Fragment
            replaceFrameContent(fragmentToShow)
        }
    }

    private fun setUpTabLayoutMediator(tabLayout: ViewGroup, pagerStatistics: ViewPager2) {
        TabLayoutMediator(tabLayout as TabLayout, pagerStatistics) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.present_volunteers)
                1 -> getString(R.string.stands)
                else -> ""
            }
        }.attach()
    }

    private fun setUpTabs(tabLayout: ViewGroup, statisticsViewModel: StatisticsViewModel) {
        (tabLayout as TabLayout).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    statisticsViewModel.selectedTabPosition.value = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun replaceFrameContent(fragmentToShow: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragmentToShow)
        transaction.commit()
        supportFragmentManager.executePendingTransactions()

    }
}