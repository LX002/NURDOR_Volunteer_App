package com.example.rma_nurdor_project_v2.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.viewModel.ProfileDetailsViewModel

class ProfileDetailsDialog : DialogFragment() {

    private lateinit var profileDetailsViewModel: ProfileDetailsViewModel
    private lateinit var volunteer: Volunteer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        profileDetailsViewModel = ViewModelProvider(requireActivity())[ProfileDetailsViewModel::class.java]
        profileDetailsViewModel.volunteer?.let { volunteer = profileDetailsViewModel.volunteer!! }
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_dialog_profile, null)

        val txtNameSurname = dialogView.findViewById<TextView>(R.id.txtNameSurname)
        val txtRole = dialogView.findViewById<TextView>(R.id.txtRole)
        val txtMail = dialogView.findViewById<TextView>(R.id.txtMail)
        val btnCall = dialogView.findViewById<Button>(R.id.btnCallVolunteer)
        val btnSendMsg = dialogView.findViewById<Button>(R.id.btnSendMsg)
        val profileImageView = dialogView.findViewById<ImageView>(R.id.profileImageView)

        txtNameSurname.text = "${volunteer.name } ${volunteer.surname}"
        txtRole.text = if(volunteer.volunteerRole == 1) "administrator" else "volunteer"
        txtMail.text = volunteer.email
        if(volunteer.profilePicture.isNullOrBlank()) {
            profileImageView.setImageResource(R.drawable.ic_launcher_background)
        } else {
            val mimeType = getMimeType(volunteer.profilePicture!!)
            mimeType?.let {
                Glide.with(requireActivity())
                    .asBitmap()
                    .load("data:$mimeType;base64," + volunteer.profilePicture)
                    .override(250, 250)
                    .centerCrop()
                    .into(profileImageView)
            }
        }

        btnCall.setOnClickListener {
            Log.i("profileDialog", "Calling ${volunteer.name} ${volunteer.surname}")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${volunteer.phoneNumber}"))
            if(intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
            dismiss()
        }

        btnSendMsg.setOnClickListener {
            Log.i("profileDialog", "Messaging ${volunteer.name} ${volunteer.surname}")
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${volunteer.phoneNumber}"))
            if(intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
            dismiss()
        }
        builder.setView(dialogView)
        return builder.create()
    }

    private fun getMimeType(profileImg: String): String? {
        val typeIdentifier = profileImg.take(20)
        return when {
            typeIdentifier.startsWith("iVBORw0KGgo") -> "image/png"
            typeIdentifier.startsWith("/9j/") -> "image/jpeg"
            typeIdentifier.startsWith("R0lGODdh") -> "image/gif"
            else -> null
        }
    }
}