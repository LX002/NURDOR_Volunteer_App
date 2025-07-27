package com.example.rma_nurdor_project_v2.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rma_nurdor_project_v2.model.VolunteerRole

@Dao
interface VolunteerRoleDao {

    @Query("SELECT * FROM volunteerrole")
    fun getVolunteerRoles(): List<VolunteerRole>

    @Query("SELECT * FROM volunteerrole where idVolunteerRole = :idRole")
    fun getVolunteerRoleBy(idRole: Int): VolunteerRole?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVolunteerRoles(roles: List<VolunteerRole>)

    @Delete
    fun deleteVolunteerRoles(volunteerRoles: List<VolunteerRole>)
}