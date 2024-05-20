package com.ntsarenkov.countries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ntsarenkov.countries.adapter.CountriesAdapter
import com.ntsarenkov.countries.databinding.FragmentMainBinding
import com.ntsarenkov.countries.model.CountryData
import com.ntsarenkov.countries.service.CountriesAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Main : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var countryArray: ArrayList<CountryData>? = null
    private var compositeDisposable: CompositeDisposable? = null
    private lateinit var countryAdapter: CountriesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)

        compositeDisposable = CompositeDisposable()
        loadData()

        return binding.root
    }

    private fun loadData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CountriesAPI::class.java)

        compositeDisposable?.add(
            retrofit.getAllCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ countryList ->
                    countryList.let {
                        countryArray = java.util.ArrayList(countryList)
                        setRecyclerView(countryArray!!)
                    }
                }, { throwable ->
                    throwable.printStackTrace()
                })
        )
    }

    private fun setRecyclerView(countryList: ArrayList<CountryData>) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)

        countryAdapter = context?.let { CountriesAdapter(it, countryList) }!!
        recyclerView.adapter = countryAdapter
    }

    override fun onDestroy() {
        compositeDisposable?.clear()
        super.onDestroy()
    }

    companion object {
        const val BASE_URL = "http://restcountries.com/v3.1/"
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(0, TimeUnit.MINUTES)
            .readTimeout(0, TimeUnit.MINUTES)
            .writeTimeout(0, TimeUnit.MINUTES)
            .build()
    }
}