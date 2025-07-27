package com.example.rma_nurdor_project_v2

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.rma_nurdor_project_v2.fragments.EventPagerAdapter
import com.example.rma_nurdor_project_v2.fragments.EventVolunteersFragment
import com.example.rma_nurdor_project_v2.fragments.QrFragment
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.viewModel.EventViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private var selectedOpt = 1
    private var selectedTab = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, maxOf(systemBars.bottom, ime.bottom))
            insets
        }
        
        savedInstanceState?.let { selectedOpt = savedInstanceState.getInt("selectedOpt") }

        setStatusBarColor()

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        fetchDataChanges()

        updatePresentState(1)

        val toolbar: MaterialToolbar = findViewById(R.id.eventMaterialToolbar)
        toolbar.title = intent.getStringExtra("eventName")
        setSupportActionBar(toolbar)

        if(!isLandscape()) {
            val viewPagerEvent: ViewPager2 = findViewById(R.id.viewPagerEvent)
            val eventPagerAdapter = EventPagerAdapter(intent.getIntExtra("idEvent", 0),this)
            viewPagerEvent.adapter = eventPagerAdapter

            savedInstanceState?.let {
                viewPagerEvent.post {
                    viewPagerEvent.setCurrentItem(selectedOpt - 1, false)
                }
            }

            val tabLayoutEvent: TabLayout = findViewById(R.id.tabLayoutEvent)
            tabLayoutEvent.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab != null) { selectedTab = tab.position + 1 }
                    Log.i("selectedOpt", "selected option tab select listener: $selectedOpt")
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) { }
                override fun onTabReselected(tab: TabLayout.Tab?) { }
            })
            TabLayoutMediator(tabLayoutEvent, viewPagerEvent) { tab, position ->
                tab.text = when(position) {
                    0 -> getString(R.string.donations_qr)
                    1 -> getString(R.string.present_volunteers)
                    else -> ""
                }
            }.attach()
        } else {
            val optDonations: CardView = findViewById(R.id.optDonations)
            val optVolunteers: CardView = findViewById(R.id.optVolunteers)
            savedInstanceState?.let {
                replaceFrameContent(
                    R.id.contentContainer2,
                    when(selectedOpt) {
                        1 -> QrFragment()
                        2 -> EventVolunteersFragment.newInstance(intent.getIntExtra("idEvent", 0))
                        else -> throw IllegalArgumentException("Exception in EventActivity, line 99: Fragment must be a CreateEventFragment or EventArchiveFragment!")
                    }
                )
                Log.i("selectedOpt", "saved instance state in landscape sel opt: $selectedOpt")
            } ?: replaceFrameContent(R.id.contentContainer2, QrFragment())

            optDonations.setOnClickListener { replaceFrameContent(R.id.contentContainer2, QrFragment()) }
            optVolunteers.setOnClickListener { replaceFrameContent(R.id.contentContainer2, EventVolunteersFragment.newInstance(intent.getIntExtra("idEvent", 0))) }
        }
    }

    private fun replaceFrameContent(frameId: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(frameId, fragment)
        transaction.commit()
        supportFragmentManager.executePendingTransactions()
        selectedTab = when(fragment) {
            is QrFragment -> 1
            is EventVolunteersFragment -> 2
            else -> throw IllegalStateException("Exception in EventActivity, line 118: Fragment must be a QrFragment or EventVolunteersFragment!")
        }
        Log.i("selectedOpt", "replace frame content sel opt: $selectedOpt")
    }

    override fun onResume() {
        super.onResume()
        fetchDataChanges()
    }

    override fun onPause() {
        super.onPause()
        fetchDataChanges()
    }

    override fun onStop() {
        super.onStop()
        updatePresentState(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        updatePresentState(0)
    }

    private fun fetchDataChanges() {
        lifecycleScope.launch {
            eventViewModel.loadEventsLogs()
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 (API 31) and above
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.statusBarColor = ContextCompat.getColor(window.context, R.color.nurdor_green_2)
        } else {
            window.statusBarColor = ContextCompat.getColor(window.context, R.color.nurdor_green_2)
        }
    }

    private fun updatePresentState(isPresent: Byte) {
        val context = this
        CoroutineScope(Dispatchers.Main).launch {
            val rowsUpdated = withContext(Dispatchers.IO) {
                eventViewModel.markAsPresent(PreferenceHelper.getIdVolunteer(context), intent.getIntExtra("idEvent", 0), isPresent)
            }
            if(rowsUpdated != 1) {
                Toast.makeText(context, "Something went wrong with updating presence value!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedOpt = selectedTab
        outState.putInt("selectedOpt", selectedOpt)
    }
}