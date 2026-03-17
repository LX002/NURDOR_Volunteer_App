package com.example.rma_nurdor_project_v2.dto

data class VolunteerRoleDto(val id: Int, val roleName: String) {
    override fun toString(): String {
        return roleName
    }
}
