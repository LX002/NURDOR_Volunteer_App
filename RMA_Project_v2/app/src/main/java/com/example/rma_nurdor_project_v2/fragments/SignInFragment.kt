package com.example.rma_project_demo_v1.fragments

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
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.rma_nurdor_project_v2.LoginActivity
import com.example.rma_nurdor_project_v2.utils.PasswordUtils
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.model.VolunteerRole
import com.example.rma_nurdor_project_v2.viewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Base64

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {

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

    private var selectedCity: City? = null
    private var selectedRole: VolunteerRole? = null

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var imgPickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private var isCityFirstSelection = true
    private var isRoleFirstSelection = true

    interface OnLoginFragmentListener {
        fun newSignInHandling()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imgPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            Log.i("pickImageDebugSignIn", "Uri to string: ${uri.toString()}")
            uri?.let {
                loginViewModel.profileImg.value = readBytes(requireContext(), uri)
                loginViewModel.profileImgName.value = getFileName(requireContext(), uri)
                Log.i("pickImageDebugSignIn", "${loginViewModel.profileImgName.value}")
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            Log.i("imgPickerEvent", "Uri to string: ${uri.toString()}")
            uri?.let {
                loginViewModel.profileImg.value = readBytes(requireContext(), uri)
                loginViewModel.profileImgName.value = getFileName(requireContext(), uri)
                Log.i("pickImageDebugSignIn", "${loginViewModel.profileImgName.value}")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as OnLoginFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        return inflater.inflate(R.layout.fragment_sign_in_alt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            loginViewModel.loadCities()
            loginViewModel.loadVolunteerRoles()
            loginViewModel.loadVolunteers()
        }

        val owner: LifecycleOwner? = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(owner) {
            if (isAdded && isVisible) {
                Log.i("fragmentIndicator", "Sign in is added to backStack and visible")
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainFrame, LoginFragment())
                    .commit().also { (requireActivity() as LoginActivity).fragmentIndicator = 1 }
            } else {
                // Handle the case when the fragment isn't in a valid state to handle the back press
                Log.i("fragmentIndicator", "Sign in is added: $isAdded visible: $isVisible")
            }
        }

        btnSignIn = view.findViewById(R.id.btnSignIn)
        loginViewModel.isSignInEnabled.observe(requireActivity()) {
            btnSignIn?.isEnabled = it
        }

        btnPickImage = view.findViewById(R.id.btnPickImage)
        btnPickImage?.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val intent  = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "image/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                imgPickerLauncher.launch(intent)
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

        val txtFields = listOf(txtName, txtSurname, txtAddress, txtPhone, txtEmail, txtUsername, txtPassword, txtRepeatPassword)

        for((i, txtField) in txtFields.withIndex()) {
            if(loginViewModel.signInFieldsValues[i].isNotBlank()) {
                txtField?.setText(loginViewModel.signInFieldsValues[i])
            }
        }

        spinnerNearestCity = view.findViewById(R.id.spinnerNearestCity)
        spinnerRole = view.findViewById(R.id.spinnerRole)

        // manipulacija adapterima i spinnerima za gradove i uloge

        val adapterPair = CoroutineScope(Dispatchers.IO).async {
            val c = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, loginViewModel.getLoadedCities())
            val r = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, loginViewModel.getLoadedRoles())
            Pair(c, r)
        }

        owner?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val (cityAdapter, roleAdapter) = adapterPair.await()

                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerNearestCity?.adapter = cityAdapter
                spinnerNearestCity?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedCity = cityAdapter.getItem(p2)
                        loginViewModel.selectedCity = cityAdapter.getItem(p2)
                        Log.i("loginViewModelFields", "sel city value: ${loginViewModel.selectedCity}")
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Log.e("SignInFragment", "Nothing selected in spinnerNearestCity!")
                    }
                }

                loginViewModel.cities.observe(owner) { cities ->
                    cityAdapter.clear()
                    cityAdapter.addAll(cities.sortedBy { it.zipCode })
                    cityAdapter.notifyDataSetChanged()

                    Log.i("onStartCitiesObserve", "$cityAdapter")
                    Log.i("citiesTagObserve", cities.toString())
                    Log.i("citiesTagObserve", cities.isEmpty().toString())

                    if(loginViewModel.selectedCity != null) {
                        spinnerNearestCity?.setSelection(cityAdapter.getPosition(loginViewModel.selectedCity))
                    }
                }

                loginViewModel.profileImg.observe(owner) {
                    profileImg = it
                }

                loginViewModel.profileImgName.observe(owner) {
                    profileImgName = it
                    txtProfileImg?.setText(it)
                }

                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerRole?.adapter = roleAdapter
                spinnerRole?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedRole = roleAdapter.getItem(p2)
                        loginViewModel.selectedRole = roleAdapter.getItem(p2)
                        Log.i("loginViewModelFields", "sel role value: ${loginViewModel.selectedRole}")
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        Log.e("SignInFragment", "Nothing selected in spinnerRole!")
                    }
                }

                loginViewModel.volunteerRoles.observe(owner) { roles ->
                    roleAdapter.clear()
                    roleAdapter.addAll(roles)
                    roleAdapter.notifyDataSetChanged()
                    Log.i("onStartRoles", "$roles")
                    if(loginViewModel.selectedRole != null) {
                        spinnerRole?.setSelection(roleAdapter.getPosition(loginViewModel.selectedRole))
                    }
                }
            }
        }

        for(i in 0 until loginViewModel.signInFieldsValues.size) {
            txtFields[i]?.doAfterTextChanged { text ->
                loginViewModel.signInFieldsValues[i] = text.toString()
            }
        }

        // field validation
        txtEmail?.doAfterTextChanged { text ->
            loginViewModel.validateEmail(text)
        }
        txtAddress?.doAfterTextChanged { text ->
            loginViewModel.validateAddress(text)
        }
        txtUsername?.doAfterTextChanged { text ->
            loginViewModel.validateUsername(text)
        }
        txtPassword?.doAfterTextChanged { text ->
            loginViewModel.validatePassword(text)
        }
        txtPhone?.doAfterTextChanged { text ->
            loginViewModel.validatePhone(text)
        }

        btnSignIn?.setOnClickListener {
            val filledState = loginViewModel.isFormFilled(txtFields.map { it?.text.toString() }, selectedCity, selectedRole)
            if(filledState == 1) {
                CoroutineScope(Dispatchers.Main).launch {
                    val id = withContext(Dispatchers.IO) {
                        loginViewModel.insertOrReplaceVolunteer(Volunteer(
                            name = txtName?.text.toString(),
                            surname = txtSurname?.text.toString(),
                            address = txtAddress?.text.toString(),
                            phoneNumber = txtPhone?.text.toString(),
                            email = txtEmail?.text.toString(),
                            username = txtUsername?.text.toString(),
                            password = PasswordUtils.hashPassword(txtPassword?.text.toString()),
                            profilePicture = profileImg?.let { Base64.getEncoder().encodeToString(profileImg).replace("\n", "") },
                            nearestCity = selectedCity!!.zipCode,
                            volunteerRole = selectedRole!!.idVolunteerRole
                        ))
                    }

                    if(id > 0) {
                        Toast.makeText(context, "Signed in successfully!", Toast.LENGTH_SHORT).show()
                        txtFields.forEach { it?.setText("") }
                        callback.newSignInHandling()
                    } else {
                        when {
                            id.toInt() == -1 -> Toast.makeText(context, "Username already exists!", Toast.LENGTH_SHORT).show()
                            id.toInt() == -2 -> Toast.makeText(context, "User with entered email already exists!", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(context, "Sign in was not successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                when(filledState) {
                    -2 -> Toast.makeText(context, "Passwords don't match!", Toast.LENGTH_SHORT).show()
                    -1 -> Toast.makeText(context, "City and role must be selected!", Toast.LENGTH_SHORT).show()
                    0  -> Toast.makeText(context, "All text fields must be filled!", Toast.LENGTH_SHORT).show()
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