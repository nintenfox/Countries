package com.ntsarenkov.countries.model

data class CountryDetails(
    val countryName: String,
    val capital: String,
    val population: Long,
    val currency: String?,
    val continent: String,
    val language: Map<String, String>,
    val maps: String,
    val flag: String,
    val startOfWeek: String
) : java.io.Serializable
