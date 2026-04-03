package com.example.nurdor_volunteer_app_v3.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.activity.AuthActivity
import com.example.nurdor_volunteer_app_v3.dto.RegisterDto
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.model.VolunteerRole
import com.example.nurdor_volunteer_app_v3.utils.PasswordUtils
import com.example.nurdor_volunteer_app_v3.viewModel.AuthViewModel
import com.example.nurdor_volunteer_app_v3.viewModel.CityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Base64


class SignInFragment: Fragment() {

    private lateinit var callback : OnLoginFragmentListener
    private var btnSignIn : Button? = null
    private var btnPickImage: Button? = null
    private var txtName : EditText? = null
    private var txtSurname : EditText? = null
    private var txtAddress : EditText? = null
    private var txtPhone : EditText? = null
    private var txtEmail : EditText? = null
    private var txtUsername : EditText? = null
    private var txtPassword : EditText? = null
    private var txtRepeatPassword : EditText? = null
    private var txtProfileImg: EditText? = null

    private var spinnerNearestCity : Spinner? = null
    private var spinnerRole : Spinner? = null

    private var profileImg: ByteArray? = null
    private var profileImgName: String? = null

    private lateinit var authViewModel: AuthViewModel
    private lateinit var cityViewModel: CityViewModel
    private lateinit var oldImgPicker: ActivityResultLauncher<Intent>
    private lateinit var newImgPicker: ActivityResultLauncher<PickVisualMediaRequest>

//    private var isCityFirstSelection = true
//    private var isRoleFirstSelection = true

    interface OnLoginFragmentListener {
        fun newSignInHandling()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val oldImgPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            uri?.let{
                authViewModel.profileImg = readBytes(requireContext(), uri)
                authViewModel.signInTextFieldsValues[8] = getFileName(requireContext(), uri)
            }
        }

        val newImagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let{
                authViewModel.profileImg = readBytes(requireContext(), uri)
                authViewModel.signInTextFieldsValues[8] = getFileName(requireContext(), uri)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = context as OnLoginFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class]
        cityViewModel = ViewModelProvider(requireActivity())[CityViewModel::class]
        return inflater.inflate(R.layout.fragment_sign_in , container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            cityViewModel.fetchAll()
        }

        btnSignIn = view.findViewById(R.id.btnSignIn)
        authViewModel.isSignInEnabled.observe(requireActivity()) {
            btnSignIn?.isEnabled = it
        }

        btnPickImage = view.findViewById(R.id.btnPickImage)
        btnPickImage?.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                newImgPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val intent  = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "image/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                oldImgPicker.launch(intent)
            }
        }

        txtName = view.findViewById(R.id.editTextName)
        txtSurname = view.findViewById(R.id.editTextSurname)
        txtAddress = view.findViewById(R.id.editTextAddress)
        txtPhone = view.findViewById(R.id.editTextPhone)
        txtEmail = view.findViewById(R.id.editTextEmail)
        txtUsername = view.findViewById(R.id.editTextUsername)
        txtPassword = view.findViewById(R.id.editTextPassword)
        txtRepeatPassword = view.findViewById(R.id.editTextRepeatPassword)
        txtProfileImg = view.findViewById(R.id.editTextProfilePic)

        val txtFields = listOf(txtName, txtSurname, txtAddress, txtPhone, txtEmail, txtUsername, txtPassword, txtRepeatPassword, txtProfileImg)

        for((i, txtField) in txtFields.withIndex()) {
            if(authViewModel.signInTextFieldsValues[i]?.isNotBlank() == true) {
                txtField?.setText(authViewModel.signInTextFieldsValues[i])
            }
        }

        spinnerNearestCity = view.findViewById(R.id.spinnerNearestCity)
        spinnerRole = view.findViewById(R.id.spinnerRole)

        val roleAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf(VolunteerRole(1, "Admin"), VolunteerRole(2, "Volunteer"))
        )

        cityViewModel.allCities.observe(viewLifecycleOwner) { cities ->
            val cityAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cities)
            spinnerNearestCity?.adapter = cityAdapter
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerNearestCity?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                authViewModel.selectedCity = spinnerNearestCity?.adapter?.getItem(p2) as City
                Log.i("authViewModelFields", "sel city value: ${authViewModel.selectedCity}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.e("SignInFragment", "Nothing selected in spinnerNearestCity!")
            }
        }

        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole?.adapter = roleAdapter
        spinnerRole?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                authViewModel.selectedRole = roleAdapter.getItem(p2) as VolunteerRole
                Log.i("authViewModelFields", "sel role value: ${authViewModel.selectedRole}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.e("SignInFragment", "Nothing selected in spinnerRole!")
            }
        }

        for(i in 0 until authViewModel.signInTextFieldsValues.size) {
            txtFields[i]?.doAfterTextChanged { text ->
                authViewModel.signInTextFieldsValues[i] = text.toString()
            }
        }

        txtEmail?.doAfterTextChanged { text ->
            authViewModel.validateEmail(text)
        }
        txtAddress?.doAfterTextChanged { text ->
            authViewModel.validateAddress(text)
        }
        txtUsername?.doAfterTextChanged { text ->
            authViewModel.validateUsername(text)
        }
        txtPassword?.doAfterTextChanged { text ->
            authViewModel.validatePassword(text)
        }
        txtPhone?.doAfterTextChanged { text ->
            authViewModel.validatePhone(text)
        }


        // klik na sign in i nista se ne desava, proveri validacije i ostalo (retrofit poziv nije a mozda i jeste)
        btnSignIn?.setOnClickListener {
            val isFormFilled = authViewModel.isFormFilled()
            Log.i("signInButtonListener", "isFormFilled: $isFormFilled")
            when(isFormFilled) {
                1 -> {
                    val registerAsync = CoroutineScope(Dispatchers.IO).async {
                        val registerDto = RegisterDto(
                            txtName?.text.toString(),
                            txtSurname?.text.toString(),
                            txtAddress?.text.toString(),
                            txtPhone?.text.toString(),
                            txtEmail?.text.toString(),
                            txtUsername?.text.toString(),
                            txtPassword?.text.toString(),
                            profileImg?.let { Base64.getEncoder().encodeToString(profileImg).replace("\n", "") },
                            authViewModel.selectedCity.zipCode,
                            authViewModel.selectedRole.idVolunteerRole
                        )
                        authViewModel.register(registerDto)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        val result = registerAsync.await()
                        Log.i("signInButtonListener", "registerResult: $result")
                        when {
                            result > 0 -> {
                                Toast.makeText(requireContext(), "Successfully signed in!", Toast.LENGTH_SHORT).show()
                                callback.newSignInHandling()
                            }
                            result == 0 -> {
                                Toast.makeText(requireContext(), "Sign in response unsuccessful", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(requireContext(), "Successfully signed in!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                0 -> {
                    // [NOTE TO SELF] change to dialog later
                    Toast.makeText(requireContext(), "All text fields must be filled!", Toast.LENGTH_SHORT).show()
                }
                -1 -> {
                    Toast.makeText(requireContext(), "City and role must be selected!", Toast.LENGTH_SHORT).show()
                }
                -2 -> {
                    Toast.makeText(requireContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readBytes(context: Context, uri: Uri) =
        context.contentResolver.openInputStream(uri)?.use {
            it.buffered(16 * 1024).readBytes()
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use {cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if(nameIndex !=  -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        return fileName
    }
}