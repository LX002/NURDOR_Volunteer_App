package com.example.nurdor_volunteer_app_v3.utils

import org.json.JSONObject
import android.util.Base64
import com.example.nurdor_volunteer_app_v3.model.VolunteerRole

object JwtUtils {

    fun getUsernameFromToken(jwt: String?): String? {
        return try {
            val payload = jwt?.split(".")[1]
            val decodedPayload = String(Base64.decode(payload, Base64.URL_SAFE))
            val json = JSONObject(decodedPayload)
            json.getString("sub")
        } catch (e: Exception) {
            null
        }
    }

    fun getRoleFromToken(jwt: String?): VolunteerRole? {
        return try {
            val payload = jwt?.split(".")[1]
            val decodedPayload = String(Base64.decode(payload, Base64.URL_SAFE))
            val json = JSONObject(decodedPayload)
            val roleName = json.getJSONArray("roles").getString(0)
            val idRole = if(roleName.equals("ROLE_ADMIN")) 1 else 2
            VolunteerRole(idRole, roleName)
        } catch (e: Exception) {
            null
        }
    }
}