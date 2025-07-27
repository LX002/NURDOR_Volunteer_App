package com.example.rma_nurdor_project_v2

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rma_nurdor_project_v2.model.City
import com.example.rma_nurdor_project_v2.model.Event
import com.example.rma_nurdor_project_v2.model.EventRole
import com.example.rma_nurdor_project_v2.model.EventsLog
import com.example.rma_nurdor_project_v2.model.Stand
import com.example.rma_nurdor_project_v2.model.Volunteer
import com.example.rma_nurdor_project_v2.model.VolunteerRole
import com.example.rma_nurdor_project_v2.repository.dao.CityDao
import com.example.rma_nurdor_project_v2.repository.dao.EventDao
import com.example.rma_nurdor_project_v2.repository.dao.EventRoleDao
import com.example.rma_nurdor_project_v2.repository.dao.EventsLogDao
import com.example.rma_nurdor_project_v2.repository.dao.StandDao
import com.example.rma_nurdor_project_v2.repository.dao.VolunteerDao
import com.example.rma_nurdor_project_v2.repository.dao.VolunteerRoleDao

@Database(entities = [City::class, Event::class, EventRole::class, EventsLog::class,
                      Stand::class, Volunteer::class, VolunteerRole::class], version = 30)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao() : CityDao
    abstract fun eventDao() : EventDao
    abstract fun eventRoleDao() : EventRoleDao
    abstract fun eventsLogDao() : EventsLogDao
    abstract fun standDao() : StandDao
    abstract fun volunteerDao() : VolunteerDao
    abstract fun volunteerRoleDao() : VolunteerRoleDao
}