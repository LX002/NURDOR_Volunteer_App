package com.example.rma_nurdor_project_v2.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rma_nurdor_project_v2.model.City

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getCities(): List<City>

    @Query("SELECT * FROM city WHERE zipCode = :zipCode")
    fun getCityByZipCode(zipCode: String): City?

    @Insert
    fun insertCity(city: City)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCities(cities: List<City>)

    @Delete
    fun deleteCities(cities: List<City>)
}