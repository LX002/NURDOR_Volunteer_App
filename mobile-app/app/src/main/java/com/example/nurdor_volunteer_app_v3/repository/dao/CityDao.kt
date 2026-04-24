package com.example.nurdor_volunteer_app_v3.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nurdor_volunteer_app_v3.model.City

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun findAll(): LiveData<List<City>>

    @Query("SELECT * FROM city WHERE zipCode = :zipCode")
    fun findByZipCode(zipCode: String): LiveData<City?>

    @Query("SELECT * FROM city WHERE zipCode = :zipCode")
    fun findCityByZipCode(zipCode: String): City?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCities(cities: List<City>)
}