package com.example.nurdor_volunteer_app_v3.activity

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.nurdor_volunteer_app_v3.NurdorVolunteerApplication
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.dto.eventsLogDto.UpdatePresenceDto
import com.example.nurdor_volunteer_app_v3.fragment.PresentVolunteersFragment
import com.example.nurdor_volunteer_app_v3.fragment.StandsFragment
import com.example.nurdor_volunteer_app_v3.fragment.dialog.DisplayMessageDialog
import com.example.nurdor_volunteer_app_v3.fragment.pagers.StatisticsPagerAdapter
import com.example.nurdor_volunteer_app_v3.utils.DateTimeUtils
import com.example.nurdor_volunteer_app_v3.utils.PreferenceHelper
import com.example.nurdor_volunteer_app_v3.viewModel.EventsLogViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.StatisticsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Timer
import java.util.TimerTask

class RunningEventStatisticsActivity : AppCompatActivity() {

    private lateinit var eventsLogViewModel: EventsLogViewModel
    private var lastSeenTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.rgb(0, 191, 51)))
        setContentView(R.layout.activity_statistics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val leftPadding = maxOf(bars.left, cutout.left)
            val rightPadding = maxOf(bars.right, cutout.right)
            v.setPadding(leftPadding, bars.top, rightPadding, bars.bottom)
            insets
        }

        if(isLandscape()) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }

        eventsLogViewModel = ViewModelProvider(this)[EventsLogViewModel::class]
        val statisticsViewModel = ViewModelProvider(this)[StatisticsViewModel::class]
        val (idEvent, idVolunteer) = getIds()

        supportFragmentManager.setFragmentResultListener("display_message_result", this) { _, bundle ->
            if(bundle.getString("status") == "SUCCESS") { finish() }
        }

        onBackPressedDispatcher.addCallback(this) {
            leave(idVolunteer, idEvent)
        }

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

    override fun onResume() {
        super.onResume()
        val (idEvent, idVolunteer) = getIds()
        startLastSeenTimer(idVolunteer, idEvent)
    }

    override fun onStop() {
        super.onStop()
        Log.i("timerLogFired", "timer log and activity stopped")
        lastSeenTimer?.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_running_event_stats, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.itemLeave) {
            if(!PreferenceHelper.isAdmin(this)) {
                val (idEvent, idVolunteer) = getIds()
                leave(idVolunteer, idEvent)
            } else {
                finish()
            }
            return true
        }
        return false
    }

    private fun startLastSeenTimer(idVolunteer: Int, idEvent: Int) {
        lastSeenTimer = Timer()
        lastSeenTimer?.schedule(object: TimerTask() {
            override fun run() {
                lifecycleScope.launch {
                    Log.i("timerLogFired", "timer log fired")
                    eventsLogViewModel.updateLastSeenTimestamp(UpdatePresenceDto(
                        idVolunteer,
                        idEvent,
                        1.toByte(),
                        DateTimeUtils.changeDateFormat(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss")
                    ))
                }
            }
        }, 0, 30000)
    }

    private fun getIds(): Pair<Int, Int> {
        val idEvent = intent.getIntExtra("idEvent", 0)
        val idVolunteer = intent.getIntExtra("idVolunteer", 0)
        return Pair(idEvent, idVolunteer)
    }

    private fun leave(idVolunteer: Int, idEvent: Int) {
        NurdorVolunteerApplication.applicationScope.launch {
            if(idVolunteer != 0) {
                val message = eventsLogViewModel.updatePresence(0, idEvent, idVolunteer)
                DisplayMessageDialog.newInstance(message, true).show(supportFragmentManager, "displayMessageDialogFragment")
            }
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

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}