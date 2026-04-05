package com.example.nurdor_volunteer_app_v3.activity

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.utils.ImageUtils
import com.google.android.material.appbar.MaterialToolbar

class VolunteerProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteer_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val extras = intent.extras
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarProfile)
        val imageViewProfilePicture = findViewById<ImageView>(R.id.profilePicture)
        val txtNameSurname = findViewById<TextView>(R.id.txtNameSurname)
        val txtPhoneNumber = findViewById<TextView>(R.id.txtPhoneNumber)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)
        val txtAddress = findViewById<TextView>(R.id.txtAddress)
        val btnClose = findViewById<Button>(R.id.btnClose)

        toolbar.title = resources.getString(R.string.profile_details)
        extras?.let {
            txtNameSurname.text = extras.getString("nameSurname")
            txtPhoneNumber.text = extras.getString("phoneNumber")
            txtEmail.text = extras.getString("email")
            txtAddress.text = extras.getString("address")
            ImageUtils.loadImageIntoIntoImageView(
                extras.getString("profileImage"),
                R.drawable.unknown_avatar,
                imageViewProfilePicture,
                this
            )
        }

        btnClose.setOnClickListener {
            finish()
        }

    }
}