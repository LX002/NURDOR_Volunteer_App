package com.example.nurdor_volunteer_app_v3.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nurdor_volunteer_app_v3.model.City

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun findAll(): List<City>

    @Query("SELECT * FROM city WHERE zipCode = :zipCode")
    fun getCityByZipCode(zipCode: String): City?

    @Insert
    fun insertCity(city: City)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCities(cities: List<City>)

    @Delete
    fun deleteCities(cities: List<City>)
}