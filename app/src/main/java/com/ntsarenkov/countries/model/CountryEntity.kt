package com.ntsarenkov.countries.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val name: String,
    val capital: String,
    val population: Long,
    val currency: String?,
    val continent: String,
    val language: String,
    val maps: String,
    val flag: String,
    val startOfWeek: String,
    val translation: String
) : java.io.Serializable