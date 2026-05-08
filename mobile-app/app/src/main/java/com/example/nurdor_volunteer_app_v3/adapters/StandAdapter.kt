package com.example.nurdor_volunteer_app_v3.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nurdor_volunteer_app_v3.R
import com.example.nurdor_volunteer_app_v3.fragment.dialog.AddDonationDialog
import com.example.nurdor_volunteer_app_v3.model.Stand

class StandAdapter(
    var stands: MutableList<Stand>,
    val idEvent: Int,
    val isAdmin: Boolean
): RecyclerView.Adapter<StandAdapter.StandViewHolder>() {

    inner class StandViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(stand: Stand) {
            val txtStandName = itemView.findViewById<TextView>(R.id.txtStandName)
            val txtCurrentDonations = itemView.findViewById<TextView>(R.id.txtCurrentDonations)
            val btnAddDonation = itemView.findViewById<ImageButton?>(R.id.btnAddDonation)

            txtStandName.text = stand.standName
            txtCurrentDonations.text = stand.totalDonations.toString()
            btnAddDonation?.let { it.setOnClickListener {
                AddDonationDialog
                    .newInstance(idEvent, stand.idStand)
                    .show((itemView.context as AppCompatActivity).supportFragmentManager, "AddDonationDialog")
            }}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandViewHolder {
        val layouts = Pair(R.layout.item_stand_admin, R.layout.item_stand_volunteer)
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(
                if(isAdmin) layouts.first else layouts.second,
                parent,
                false
            )
        return StandViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StandViewHolder, position: Int) {
        holder.bind(stands[position])
    }

    override fun getItemCount(): Int = stands.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateStands(newStands: List<Stand>) {
        stands = newStands as MutableList
        notifyDataSetChanged()
    }
}