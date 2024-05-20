package com.ntsarenkov.countries.service

import com.ntsarenkov.countries.model.CountryData
import io.reactivex.Observable
import retrofit2.http.GET

interface CountriesAPI {
    @GET("all?fields=name,capital,population,currencies,continents,languages,maps,flags,startOfWeek")
    fun getAllCountries(): Observable<List<CountryData>>
}