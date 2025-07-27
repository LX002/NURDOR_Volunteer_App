package com.example.rma_nurdor_project_v2.viewModel

import androidx.lifecycle.ViewModel
import com.example.rma_nurdor_project_v2.model.Event

class EventDetailsViewModel: ViewModel() {
    var event: Event? = null
    var onItemAdded: ((Event) -> Unit)? = null
    var onItemRemoved: ((Event) -> Unit)? = null
}