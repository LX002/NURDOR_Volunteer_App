package com.example.nurdor_volunteer_app_v3.dto.eventDto

import com.example.nurdor_volunteer_app_v3.dto.standDto.StandDto

data class StartEventResultDto(
    val message: String,
    val stands: List<StandDto>
)