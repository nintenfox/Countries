package com.ntsarenkov.countries.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ntsarenkov.countries.model.CountryEntity
import io.reactivex.Single

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries ORDER BY name")
    fun getAllCountries(): Single<List<CountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(countries: List<CountryEntity>)
}