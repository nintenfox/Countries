package com.ntsarenkov.countries.model

data class CountryData(
    val name: Name,
    val capital: List<String>,
    val region: String,
    val population: Long,
    val currencies: Map<String, Currency>,
    val continents: List<String>,
    val languages: Map<String, String>,
    val maps: Maps,
    val flags: Flags,
    val startOfWeek: String,
    val translations: Map<String, Translation>
) : java.io.Serializable

data class Name(
    val common: String,
    val official: String
)
data class Currency(
    val name: String,
    val symbol: String
)
data class Flags(
    val png: String,
    val svg: String,
    val alt: String
)
data class Maps(
    val googleMaps: String,
    val openStreetMaps: String
)
data class Translation(
    val official: String,
    val common: String
)