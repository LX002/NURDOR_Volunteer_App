package com.example.rma_nurdor_project_v2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.rma_nurdor_project_v2.R
import com.example.rma_nurdor_project_v2.model.EventsLog
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.example.rma_nurdor_project_v2.viewModel.EventViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QrFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QrFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        val btnSend: Button = view.findViewById(R.id.btnSend)
        val txtNote: EditText = view.findViewById(R.id.editTxtNote)
        val btnLeave: Button = view.findViewById(R.id.btnLeave)

        val context = requireContext()
        val activity = requireActivity()

//        CoroutineScope(Dispatchers.Main).launch {
//            val rowsUpdated = withContext(Dispatchers.IO) {
//                eventViewModel.markAsPresent(PreferenceHelper.getIdVolunteer(context), activity.intent.getIntExtra("idEvent", 0), 1)
//            }
//            if(rowsUpdated != 1) {
//                Toast.makeText(context, "Something went wrong with updating presence value!", Toast.LENGTH_SHORT).show()
//                activity.finish()
//            }
//        }

        btnSend.setOnClickListener {
            if(txtNote.text.isNotBlank()) {
                val log = EventsLog(
                    volunteer = PreferenceHelper.getIdVolunteer(context),
                    event = activity.intent.getIntExtra("idEvent", 0),
                    isPresent = 1, note = txtNote.text.toString()
                )
                CoroutineScope(Dispatchers.Main).launch {
                    eventViewModel.loadEventsLogs()
                    val id = withContext(Dispatchers.IO) {
                        eventViewModel.insertLog(log)
                    }
                    when {
                        id > 0 -> {
                            Log.i("insertEventsLog", "success, note sent!")
                            txtNote.setText("")
                        }
                        id == (0).toLong()  -> Toast.makeText(context, "Unable to send note (constraint violated, abort)!", Toast.LENGTH_SHORT).show()
                        id == (-1).toLong() -> Toast.makeText(context, "Unable to send note (unknown error)!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnLeave.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val rowsUpdated = withContext(Dispatchers.IO) {
                    eventViewModel.markAsPresent(PreferenceHelper.getIdVolunteer(context), activity.intent.getIntExtra("idEvent", 0), 0)
                }
                if(rowsUpdated != 1) {
                    Toast.makeText(context, "Something went wrong with updating presence value!", Toast.LENGTH_SHORT).show()
                } else {
                    activity.finish()
                }
            }
        }
    }

}