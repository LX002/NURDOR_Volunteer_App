package com.example.rma_project_demo_v1.dto

data class VolunteerRoleDto(val id: Int, val roleName: String) {
    override fun toString(): String {
        return roleName
    }
}
