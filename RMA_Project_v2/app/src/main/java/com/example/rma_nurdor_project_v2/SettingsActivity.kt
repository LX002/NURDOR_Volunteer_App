package com.example.rma_nurdor_project_v2

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Spinner
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rma_nurdor_project_v2.fragments.AboutDialog
import com.example.rma_nurdor_project_v2.fragments.VolunteersListDialog
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private var selectedLanguage = 0
    private var uiManager: UiModeManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        uiManager = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager

        val statusBarColor = ContextCompat.getColor(this, R.color.nurdor_green_2)
        setStatusBarColor(statusBarColor)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarSettings)
        toolbar.setNavigationOnClickListener { finish() }
        setSupportActionBar(toolbar)

        val spinnerLanguage: Spinner = findViewById(R.id.spinnerLanguage)
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("English", "Serbian"))
        spinnerLanguage.adapter = languageAdapter
        spinnerLanguage.setSelection(0)
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLanguage = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val switchDarkMode: Switch = findViewById(R.id.switchDarkMode)
        switchDarkMode.isChecked = PreferenceHelper.isDarkMode(this)

        val aboutCardView: CardView = findViewById(R.id.cardView3)
        aboutCardView.setOnClickListener {
            val existingDialog = supportFragmentManager.findFragmentByTag("VolunteersListDialog")
            if(existingDialog == null) {
                AboutDialog().show(supportFragmentManager, "VolunteersListDialog")
            }
        }
    }

    private fun setStatusBarColor(color: Int) {
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
}