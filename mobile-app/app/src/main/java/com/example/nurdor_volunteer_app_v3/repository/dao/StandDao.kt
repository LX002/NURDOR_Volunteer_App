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

    @Query("SELECT * FROM stand WHERE idStand = :idStand")
    fun findById(idStand: Int): Stand

    @Query("SELECT * FROM stand WHERE event = :idEvent")
    fun findByIdEvent(idEvent: Int): LiveData<List<Stand>>

    @Query("UPDATE stand SET event = :idEvent WHERE idStand IN (:ids)")
    fun updateIdEventByStandIds(idEvent: Int?, ids: List<Int>): Int

    @Query("UPDATE stand SET totalDonations = :totalDonations WHERE idStand = :idStand")
    fun updateTotalDonations(totalDonations: Int, idStand: Int): Int
}