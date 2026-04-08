package com.example.nurdor_volunteer_app_v3.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nurdor_volunteer_app_v3.model.Stand

@Dao
interface StandDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceStands(stands: List<Stand>)

    @Query("SELECT * FROM stand")
    fun findAll(): LiveData<List<Stand>>

    @Query("UPDATE stand SET event = :idEvent WHERE idStand IN (:ids)")
    fun updateIdEventByStandIds(idEvent: Int?, ids: List<Int>): Int
}