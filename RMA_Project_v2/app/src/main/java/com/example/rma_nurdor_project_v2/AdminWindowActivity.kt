package com.example.rma_nurdor_project_v2

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.rma_nurdor_project_v2.fragments.AdminPagerAdapter
import com.example.rma_nurdor_project_v2.fragments.CreateEventFragment
import com.example.rma_nurdor_project_v2.fragments.EventArchiveFragment
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminWindowActivity : AppCompatActivity() {

    private var selectedOpt = 1
    private var selectedTab = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, maxOf(systemBars.bottom, ime.bottom))
            insets
        }

        var temp = 0
        savedInstanceState?.let { selectedOpt = savedInstanceState.getInt("selectedOpt") }

        setStatusBarColor()

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarAdmin)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        if(!isLandscape()) {
            val viewPagerAdmin: ViewPager2 = findViewById(R.id.viewPagerAdmin)
            val adminPagerAdapter = AdminPagerAdapter(this)
            viewPagerAdmin.adapter = adminPagerAdapter

            val tabLayoutAdmin: TabLayout = findViewById(R.id.tabLayoutAdmin)

            savedInstanceState?.let {
                viewPagerAdmin.post {
                    Log.i("selectedOpt", "saved instance state in post: $selectedOpt")
                    viewPagerAdmin.setCurrentItem(selectedOpt - 1, false)
                }
            }

            tabLayoutAdmin.addOnTabSelectedListener( object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab != null) { selectedTab = tab.position + 1 }
                    Log.i("selectedOpt", "selected option tab select listener: $selectedOpt")
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) { }
                override fun onTabReselected(tab: TabLayout.Tab?) { }
            })

            TabLayoutMediator(tabLayoutAdmin, viewPagerAdmin) { tab, position ->
                tab.text = when(position) {
                    0 -> getString(R.string.create_event)
                    1 -> getString(R.string.event_archive)
                    else -> ""
                }
            }.attach()
        } else {
            val optCreateEvent: CardView = findViewById(R.id.optCreateEvent)
            val optEventArchive: CardView = findViewById(R.id.optEventArchive)
            savedInstanceState?.let {
                replaceFrameContent(
                    R.id.contentContainer,
                    when(selectedOpt) {
                        1 -> CreateEventFragment()
                        2 -> EventArchiveFragment()
                        else -> throw IllegalArgumentException("Exception in AdminWindowActivity, line 85: Fragment must be a CreateEventFragment or EventArchiveFragment!")
                    }
                )
                Log.i("selectedOpt", "saved instance state in landscape sel opt: $selectedOpt")
            } ?: replaceFrameContent(R.id.contentContainer, CreateEventFragment())

            optCreateEvent.setOnClickListener { replaceFrameContent(R.id.contentContainer, CreateEventFragment()) }
            optEventArchive.setOnClickListener { replaceFrameContent(R.id.contentContainer, EventArchiveFragment()) }
        }

    }

    private fun replaceFrameContent(frameId: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(frameId, fragment)
        transaction.commit()
        supportFragmentManager.executePendingTransactions()
        selectedTab = when(fragment) {
            is CreateEventFragment -> 1
            is EventArchiveFragment -> 2
            else -> throw IllegalStateException("Exception in AdminWindowActivity, line 105: Fragment must be a CreateEventFragment or EventArchiveFragment!")
        }
        Log.i("selectedOpt", "replace frame content sel opt: $selectedOpt")
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

    private fun isLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedOpt = selectedTab
        outState.putInt("selectedOpt", selectedOpt)
    }
}