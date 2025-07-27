package com.example.rma_nurdor_project_v2.repository

import android.content.Context
import android.util.Log
import com.example.rma_nurdor_project_v2.AppDatabase
import com.example.rma_nurdor_project_v2.DatabaseClient
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.VolunteerRole
import com.example.rma_project_demo_v1.dto.VolunteerRoleDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class VolunteerRoleRepository(db: AppDatabase) {
    private val retrofitApi = RetrofitInstance.instance
    private val mVolunteerRoleDao = db.volunteerRoleDao()

    suspend fun getVolunteerRoles(): List<VolunteerRole> {
        return withContext(Dispatchers.IO) {
            fetchVolunteerRoles()
            Log.i("after_inserting1", "returning v. roles ${mVolunteerRoleDao.getVolunteerRoles()}")
            val rolesList = mVolunteerRoleDao.getVolunteerRoles()
            Log.i("actualReturn", "actual return of v. roles: $rolesList")
            rolesList
        }
    }

    private fun getRetrofitRoles() {
        retrofitApi.getRoles().enqueue(object: Callback<List<VolunteerRoleDto>> {
            override fun onResponse(call: Call<List<VolunteerRoleDto>>, response: Response<List<VolunteerRoleDto>>) {
                if(response.isSuccessful && response.body() != null) {
                    val roles = response.body()!!.map { volunteerRoleDto ->
                        VolunteerRole(volunteerRoleDto.id, volunteerRoleDto.roleName)
                    }
                    Log.i("retrofitApi1", "Volunteer roles dto list fetched!")
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.i("inserting1", "Inserting data in local database...")
                        Log.i("insertingList", "$roles")
                        mVolunteerRoleDao.insertVolunteerRoles(roles)
                        if(roles.size < mVolunteerRoleDao.getVolunteerRoles().size) {
                            mVolunteerRoleDao.deleteVolunteerRoles(mVolunteerRoleDao.getVolunteerRoles().filter { it !in roles } )
                        }
                    }
                } else {
                    Log.i("retrofiApi1", "City dto list is empty....")
                }
            }

            override fun onFailure(call: Call<List<VolunteerRoleDto>>, t: Throwable) {
                Log.e("retrofitApi1", "V. role list is empty and error occurred: ${t.message}\n${t.stackTrace}")
            }
        })
    }

    private suspend fun fetchVolunteerRoles() {
        try {
            val response = retrofitApi.getRoles().awaitResponse()
            if (response.isSuccessful) {
                val roles = response.body()!!.map { volunteerRoleDto ->
                    VolunteerRole(volunteerRoleDto.id, volunteerRoleDto.roleName)
                }
                Log.i("retrofitApi1", "Volunteer roles dto list fetched!")
                withContext(Dispatchers.IO) {
                    Log.i("inserting1", "Inserting data in local database...")
                    Log.i("insertingList", "$roles")
                    mVolunteerRoleDao.insertVolunteerRoles(roles)
                    if(roles.size < mVolunteerRoleDao.getVolunteerRoles().size) {
                        mVolunteerRoleDao.deleteVolunteerRoles(mVolunteerRoleDao.getVolunteerRoles().filter { it !in roles } )
                    }
                }
            } else {
                Log.e("retrofitApi1", "Error: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("retrofitApi1", "Network error: ${e.message}")
        }
    }

    suspend fun getRoleById(idRole: Int): VolunteerRole? {
        return withContext(Dispatchers.IO) { mVolunteerRoleDao.getVolunteerRoleBy(idRole) }
    }

    suspend fun getLoadedRoles(): List<VolunteerRole> {
        return withContext(Dispatchers.IO) { mVolunteerRoleDao.getVolunteerRoles() }
    }
}