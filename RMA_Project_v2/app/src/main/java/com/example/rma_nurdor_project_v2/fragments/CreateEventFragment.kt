package com.example.rma_nurdor_project_v2.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.rma_nurdor_project_v2.OsmActivity
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.viewModel.CreateEventViewModel
import com.example.rma_nurdor_project_v2.viewModel.EventViewModel
import com.example.rma_nurdor_project_v2.viewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateEventFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var createEventViewModel: CreateEventViewModel
    private lateinit var mapResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var imgPickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private var selectedCity: City? = null
    private var txtEventName: EditText? = null
    private var txtDescription: EditText? = null
    private var btnStartDate: Button? = null
    private var btnStartTime: Button? = null
    private var btnEndDate: Button? = null
    private var btnEndTime: Button? = null
    private var txtStartDate: EditText? = null
    private var txtStartTime: EditText? = null
    private var txtEndDate: EditText? = null
    private var txtEndTime: EditText? = null
    private var btnPickImg: Button? = null
    private var btnPickLocation: Button? = null
    private var spinnerCity: Spinner? = null
    private var btnCreateEvent: Button? = null
    private var txtImage: EditText? = null
    private var txtLocationDesc: EditText? = null
    private var eventCoordinates: Triple<Double, Double, String?>? = null
    private var eventImg: ByteArray? = null
    private var eventImgName: String? = null

    private val txtFields = listOf(txtEventName, txtDescription, txtStartDate, txtStartTime, txtEndDate, txtEndTime, txtImage, txtLocationDesc)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // owner is this u requireActivity()
        eventViewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        createEventViewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]

        mapResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if(res.resultCode == Activity.RESULT_OK) {
                val resIntent = res.data?.let {
                    val latitude = it.getDoubleExtra("latitude", 0.0)
                    val longitude = it.getDoubleExtra("longitude", 0.0)
                    val markerTitle = it.getStringExtra("title")
                    Log.i("MapResultLoc", "$latitude, $longitude")
                    createEventViewModel.eventCoordinates.value = Triple(latitude, longitude, markerTitle)
                }
            } else {
                Log.i("MapResultLoc", "location failed.....")
            }
        }

        // stari img picker launcher
        imgPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            Log.i("imgPickerEvent", "Uri to string: ${uri.toString()}")
            uri?.let {
                createEventViewModel.eventImg.value = readBytes(requireContext(), uri)
                createEventViewModel.eventImgName.value = getFileName(requireContext(), uri)
                Log.i("imgPickerEvent", "$eventImgName")
            }
        }

        // novi img picker launcher
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            Log.i("imgPickerEvent", "Uri to string: ${uri.toString()}")
            uri?.let {
                createEventViewModel.eventImg.value = readBytes(requireContext(), uri)
                createEventViewModel.eventImgName.value = getFileName(requireContext(), uri)
                Log.i("imgPickerEvent", "$eventImgName")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            loginViewModel.loadCities()
        }

        txtEventName = view.findViewById(R.id.editTxtEventName)
        txtDescription = view.findViewById(R.id.editTxtEventDesc)
        btnStartDate = view.findViewById(R.id.btnStartDate)
        btnStartTime = view.findViewById(R.id.btnStartTime)
        btnEndDate = view.findViewById(R.id.btnEndDate)
        btnEndTime = view.findViewById(R.id.btnEndTime)
        txtStartDate = view.findViewById(R.id.editTxtStartDate)
        txtStartTime = view.findViewById(R.id.editTxtStartTime)
        txtEndDate = view.findViewById(R.id.editTxtEndDate)
        txtEndTime = view.findViewById(R.id.editTxtEndTime)
        btnPickImg = view.findViewById(R.id.btnPickImg)
        btnPickLocation = view.findViewById(R.id.btnPickLocation)
        spinnerCity = view.findViewById(R.id.spinnerCity)
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent)
        txtImage = view.findViewById(R.id.editTxtImg)
        txtLocationDesc = view.findViewById(R.id.editTxtLocationDesc)

        val txtFieldsValues = mutableListOf(
            createEventViewModel.eventName, createEventViewModel.eventDescription,
            createEventViewModel.startDate, createEventViewModel.startTime,
            createEventViewModel.endDate, createEventViewModel.endTime)

        val txtFields = listOf(txtEventName, txtDescription, txtStartDate, txtStartTime, txtEndDate, txtEndTime)

        for((i, fieldValue) in txtFieldsValues.withIndex()) {
            if(fieldValue.isNotBlank()) txtFields[i]?.setText(fieldValue)
        }

        createEventViewModel.eventImgName.observe(viewLifecycleOwner) {
            eventImgName = it
            txtImage?.setText(it)
        }

        createEventViewModel.eventImg.observe(viewLifecycleOwner) { eventImg = it }

        // registrovanje promene lokacije
        createEventViewModel.eventCoordinates.observe(viewLifecycleOwner) {
            eventCoordinates = it
            txtLocationDesc?.setText(it?.third)
        }

        // konfigurisanje spinnerCity andaptera
        val spinnerCityAdapterDef = CoroutineScope(Dispatchers.IO).async {
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, loginViewModel.getLoadedCities())
        }

        CoroutineScope(Dispatchers.Main).launch {
            val cityAdapter = spinnerCityAdapterDef.await()
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCity?.adapter = cityAdapter
            spinnerCity?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedCity = cityAdapter.getItem(p2)
                    if(createEventViewModel.selectedCity != selectedCity) {
                        createEventViewModel.selectedCity = selectedCity
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.e("CreateEventFragment", "Nothing selected in spinnerCity!")
                }
            }

            loginViewModel.cities.observe(viewLifecycleOwner) { cities ->
                cityAdapter.clear()
                cityAdapter.addAll(cities.sortedBy { it.zipCode })
                cityAdapter.notifyDataSetChanged()

                Log.i("CreateEventFragment", "$cityAdapter")
                Log.i("CreateEventFragment", cities.toString())
                Log.i("CreateEventFragment", cities.isEmpty().toString())
                if(createEventViewModel.selectedCity != null) {
                    spinnerCity?.setSelection(cityAdapter.getPosition(createEventViewModel.selectedCity))
                }
            }
        }

        btnStartDate?.setOnClickListener {
            val datePicker = DatePickerFragment(txtStartDate, null)
            datePicker.show(requireActivity().supportFragmentManager, "startDatePicker")
        }

        btnEndDate?.setOnClickListener {
            val datePicker = DatePickerFragment(txtEndDate, null)
            datePicker.show(requireActivity().supportFragmentManager, "endDatePicker")
        }

        btnStartTime?.setOnClickListener {
            val timePicker = TimePickerFragment(txtStartTime, null)
            timePicker.show(requireActivity().supportFragmentManager, "startTimePicker")
        }

        btnEndTime?.setOnClickListener {
            val timePicker = TimePickerFragment(txtEndTime, null)
            timePicker.show(requireActivity().supportFragmentManager, "endTimePicker")
        }

        btnPickLocation?.setOnClickListener {
            val intent = Intent(requireContext(), OsmActivity::class.java)
            eventCoordinates?.let {
                intent.apply {
                    putExtra("latitude", it.first)
                    putExtra("longitude", it.second)
                }
            }
            mapResultLauncher.launch(intent)
        }

        // ovde ubaciti logiku za biranje slike iz bilo kog foldera (interna / eksterna memorija)
        // obrada permisija?
        btnPickImg?.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "image/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                imgPickerLauncher.launch(intent)
            }

        }

        createEventViewModel.isCreatingEnabled.observe(viewLifecycleOwner) { btnCreateEvent?.isEnabled = it }

        btnCreateEvent?.setOnClickListener {
            // kreira se novi event a pre toga validacija u CreateEventViewModel -> napravi ga
            val eventTimeStamps = convertToStartAndEndTime()

            CoroutineScope(Dispatchers.Main).launch {
                val id = withContext(Dispatchers.IO) {
                    createEventViewModel.insertEvent(Event(
                        eventName = txtEventName?.text.toString(),
                        description = txtDescription?.text.toString(),
                        startTime = eventTimeStamps.first,
                        endTime = eventTimeStamps.second,
                        latitude = eventCoordinates!!.first,
                        longitude = eventCoordinates!!.second,
                        eventImg = Base64.getEncoder().encodeToString(eventImg).replace("\n", ""),
                        locationDesc = eventCoordinates?.third,
                        city = selectedCity!!.zipCode
                    ))
                }

                if(id > 0) {
                    Toast.makeText(requireContext(), "Event is saved successfully!", Toast.LENGTH_SHORT).show()
                    txtFields.forEach { it?.setText("") }
                } else {
                    // obradi update deo ovde i za volontera takodje (volontera u sign in i gde god drugde treba insert njegov)!!!
                    Toast.makeText(requireContext(), "Event is not saved successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        txtEventName?.doAfterTextChanged { createEventViewModel.validateField("eventName", it.toString()) }
        txtDescription?.doAfterTextChanged { createEventViewModel.validateField("eventDesc", it.toString()) }
        txtStartDate?.doAfterTextChanged { createEventViewModel.validateField("startDate", it.toString()) }
        txtStartTime?.doAfterTextChanged { createEventViewModel.validateField("startTime", it.toString()) }
        txtEndDate?.doAfterTextChanged { createEventViewModel.validateField("endDate", it.toString()) }
        txtEndTime?.doAfterTextChanged { createEventViewModel.validateField("endTime", it.toString()) }
        txtImage?.doAfterTextChanged { createEventViewModel.validateField("image", it.toString()) }
        txtLocationDesc?.doAfterTextChanged { createEventViewModel.validateField("locationDesc", it.toString()) }
    }


    // permisije za ove dve metode?
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

    private fun convertToStartAndEndTime(): Pair<String, String> {
        val startTimeStr = txtStartTime?.text.toString()
        val startDateStr = txtStartDate?.text.toString()
        val endTimeStr = txtEndTime?.text.toString()
        val endDateStr = txtEndDate?.text.toString()

        val sd = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val sdStr = sd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val ed = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val edStr = ed.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return Pair("$sdStr $startTimeStr", "$edStr $endTimeStr")
    }
}