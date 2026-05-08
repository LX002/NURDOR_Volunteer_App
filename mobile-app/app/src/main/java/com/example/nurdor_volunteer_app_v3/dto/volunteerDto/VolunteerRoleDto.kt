package com.example.nurdor_volunteer_app_v3.dto.volunteerDto

data class VolunteerRoleDto(val id: Int, val roleName: String) {
    override fun toString(): String {
        return roleName
    }
}
