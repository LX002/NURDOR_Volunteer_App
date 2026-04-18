package com.example.nurdor_volunteer_app_v3.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.fragment.dialog.DatePickerDialogFragment
import com.example.nurdor_volunteer_app_v3.fragment.dialog.TimePickerDialogFragment
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.CreateEventViewModel
import kotlinx.coroutines.launch
import java.util.Base64

class CreateEventActivity : AppCompatActivity() {

    private lateinit var createEventViewModel: CreateEventViewModel
    private lateinit var cityViewModel: CityViewModel
    private lateinit var imgPicker1: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var imgPicker2: ActivityResultLauncher<Intent>
    private lateinit var mapLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createEventViewModel = ViewModelProvider(this)[CreateEventViewModel::class]
        cityViewModel = ViewModelProvider(this)[CityViewModel::class]

        val btnCreateEvent = findViewById<Button>(R.id.btnCreateEvent)
        btnCreateEvent.isEnabled = false

        // edit texts
        val editTxtEventName = findView(R.id.editTxtEventName, btnCreateEvent, 0) as EditText
        val editTxtEventDesc = findView(R.id.editTxtEventDesc, btnCreateEvent, 1) as EditText
        val editTxtStartDate = findView(R.id.editTxtStartDate, btnCreateEvent, 2) as EditText
        val editTxtStartTime = findView(R.id.editTxtStartTime, btnCreateEvent, 3) as EditText
        val editTxtEndDate = findView(R.id.editTxtEndDate, btnCreateEvent, 4) as EditText
        val editTxtEndTime = findView(R.id.editTxtEndTime, btnCreateEvent, 5) as EditText
        val editTxtImg = findView(R.id.editTxtImg, btnCreateEvent, 6) as EditText
        val editTxtLocationDesc = findView(R.id.editTxtLocationDesc, btnCreateEvent, 7) as EditText

        // image buttons
        val btnStartDate = findView(R.id.btnStartDate) as ImageButton
        val btnStartTime = findView(R.id.btnStartTime) as ImageButton
        val btnEndDate = findView(R.id.btnEndDate) as ImageButton
        val btnEndTime = findView(R.id.btnEndTime) as ImageButton
        val btnPickImg = findView(R.id.btnPickImg) as ImageButton
        val btnPickLocation = findView(R.id.btnPickLocation) as ImageButton

        val spinnerCity = findViewById<Spinner>(R.id.spinnerCity)
        val spinnerCityAdapter = ArrayAdapter<City>(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        spinnerCity.adapter = spinnerCityAdapter
        cityViewModel.allCities.observe(this) { cities ->
            spinnerCityAdapter.clear()
            spinnerCityAdapter.addAll(cities)
        }
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                createEventViewModel.selectedCity = spinnerCityAdapter.getItem(p2) as City
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }

        }

        setUpDateAndTimePickersResultsListeners(editTxtStartDate, editTxtStartTime, editTxtEndDate, editTxtEndTime)
        setImgPicker(editTxtImg)
        setMapLauncher(editTxtLocationDesc)

        btnCreateEvent.setOnClickListener {
            lifecycleScope.launch {
                val message = createEventViewModel.createEvent()
                Toast.makeText(this@CreateEventActivity, message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setMapLauncher(editTxtLocationDesc: EditText) {
        mapLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                res.data?.let {
                    val latitude = it.getDoubleExtra("latitude", 0.0)
                    val longitude = it.getDoubleExtra("longitude", 0.0)
                    val markerTitle = it.getStringExtra("title") ?: "PMF Novi Sad"
                    createEventViewModel.locationPin = Triple(latitude, longitude, markerTitle)
                    Log.i("pinOsm", "location pin: ${createEventViewModel.locationPin}")
                    editTxtLocationDesc.setText(markerTitle)
                }
            } else {
                Toast.makeText(this, "ERROR: Picking of location failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setImgPicker(editTxtImg: EditText) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            imgPicker2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val uri = it.data?.data
                setSelectedImage(uri, editTxtImg)
            }
        } else {
            imgPicker1 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                setSelectedImage(uri, editTxtImg)
            }
        }

    private fun setSelectedImage(uri: Uri?, editTxtImg: EditText) {
        uri?.let {
            createEventViewModel.selectedImage =
                Base64.getEncoder().encodeToString(readBytes(this, uri))
            editTxtImg.setText(getFileName(this, uri))
        }
    }

    private fun findView(resource: Int, btnCreateEvent: Button? = null, ind: Int = 0): View {
        return findViewById<View>(resource).also { view ->
            if(view is EditText) {
                view.setText(createEventViewModel.editTxtFields[ind])
                view.doAfterTextChanged { text ->
                    createEventViewModel.editTxtFields[ind] = text.toString()
                    btnCreateEvent?.isEnabled = createEventViewModel.isFormValid()
                }
            } else if(view is ImageButton) {
                view.setOnClickListener {
                    when(resource) {
                        R.id.btnStartDate -> { DatePickerDialogFragment.newInstance("SET_START_DATE").show(supportFragmentManager, "datePickerDialogFragment") }
                        R.id.btnEndDate -> { DatePickerDialogFragment.newInstance("SET_END_DATE").show(supportFragmentManager, "datePickerDialogFragment") }
                        R.id.btnStartTime -> { TimePickerDialogFragment.newInstance("SET_START_TIME").show(supportFragmentManager, "timePickerDialogFragment") }
                        R.id.btnEndTime -> { TimePickerDialogFragment.newInstance("SET_END_TIME").show(supportFragmentManager, "timePickerDialogFragment") }
                        R.id.btnPickImg -> { launchImgPicker() }
                        R.id.btnPickLocation -> { launchOpenStreetMap() }
                    }
                }
            }
        }

    }

    private fun launchOpenStreetMap() {
        val intent = Intent(this, OpenStreetMapActivity::class.java).apply {
            putExtra("latitude", createEventViewModel.locationPin.first)
            putExtra("longitude", createEventViewModel.locationPin.second)
            putExtra("title", createEventViewModel.locationPin.third)
        }
        mapLauncher.launch(intent)
    }

    private fun launchImgPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            imgPicker1.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            imgPicker2.launch(intent)
        }
    }

    private fun setUpDateAndTimePickersResultsListeners(vararg editTexts: EditText) {
        supportFragmentManager.setFragmentResultListener("SET_START_DATE", this) { requestKey, result ->
            editTexts[0].setText(result.getString("picked_date"))
        }
        supportFragmentManager.setFragmentResultListener("SET_START_TIME", this) { requestKey, result ->
            editTexts[1].setText(result.getString("picked_time"))
        }
        supportFragmentManager.setFragmentResultListener("SET_END_DATE", this) { requestKey, result ->
            editTexts[2].setText(result.getString("picked_date"))
        }
        supportFragmentManager.setFragmentResultListener("SET_END_TIME", this) { requestKey, result ->
            editTexts[3].setText(result.getString("picked_time"))
        }
    }

    // [NOTE TO SELF] move these two into utils?
    private fun readBytes(context: Context, uri: Uri) =
        context.contentResolver.openInputStream(uri)?.use {
            it.buffered(16 * 1024).readBytes()
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if(nameIndex !=  -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        return fileName
    }
}