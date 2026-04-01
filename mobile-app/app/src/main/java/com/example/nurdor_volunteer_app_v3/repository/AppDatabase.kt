package com.example.nurdor_volunteer_app_v3.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nurdor_volunteer_app_v3.model.City
import com.example.nurdor_volunteer_app_v3.model.Event
import com.example.nurdor_volunteer_app_v3.model.EventRole
import com.example.nurdor_volunteer_app_v3.model.EventsLog
import com.example.nurdor_volunteer_app_v3.model.Stand
import com.example.nurdor_volunteer_app_v3.model.Volunteer
import com.example.nurdor_volunteer_app_v3.model.VolunteerRole
import com.example.nurdor_volunteer_app_v3.repository.dao.CityDao
import com.example.nurdor_volunteer_app_v3.repository.dao.EventDao
import com.example.nurdor_volunteer_app_v3.repository.dao.EventRoleDao
import com.example.nurdor_volunteer_app_v3.repository.dao.EventsLogDao
import com.example.nurdor_volunteer_app_v3.repository.dao.StandDao
import com.example.nurdor_volunteer_app_v3.repository.dao.VolunteerDao
import com.example.nurdor_volunteer_app_v3.repository.dao.VolunteerRoleDao
import com.example.nurdor_volunteer_app_v3.utils.DateTimeConverter

@TypeConverters(DateTimeConverter::class)
@Database(entities = [City::class, Event::class, EventRole::class, EventsLog::class,
    Stand::class, Volunteer::class, VolunteerRole::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao() : CityDao
    abstract fun eventDao() : EventDao
    abstract fun eventRoleDao() : EventRoleDao
    abstract fun eventsLogDao() : EventsLogDao
    abstract fun standDao() : StandDao
    abstract fun volunteerDao() : VolunteerDao
    abstract fun volunteerRoleDao() : VolunteerRoleDao
}