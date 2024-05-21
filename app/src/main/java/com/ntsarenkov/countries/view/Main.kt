package com.ntsarenkov.countries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ntsarenkov.countries.adapter.CountriesAdapter
import com.ntsarenkov.countries.databinding.FragmentMainBinding
import com.ntsarenkov.countries.model.AppDatabase
import com.ntsarenkov.countries.model.CountryEntity
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
    private var countryArray: ArrayList<CountryEntity>? = null
    private var compositeDisposable: CompositeDisposable? = null
    private lateinit var countryAdapter: CountriesAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        database = AppDatabase.getDatabase(requireContext())
        compositeDisposable = CompositeDisposable()
        loadData()
        setupSearchView()

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
                        val countryEntities = countryList.map { countryData ->
                            CountryEntity(
                                name = countryData.name.common,
                                capital = countryData.capital.firstOrNull() ?: "N/A",
                                population = countryData.population,
                                currency = countryData.currencies.values.firstOrNull()?.name
                                    ?: "N/A",
                                continent = countryData.continents.firstOrNull() ?: "N/A",
                                language = countryData.languages.values.joinToString { it },
                                maps = countryData.maps.googleMaps,
                                flag = countryData.flags.png,
                                startOfWeek = countryData.startOfWeek
                            )
                        }
                        countryArray = ArrayList(countryEntities.sortedBy { it.name })
                        setRecyclerView(countryArray!!)
                    }
                }, { throwable ->
                    throwable.printStackTrace()
                })
        )
    }

    private fun setRecyclerView(countryList: ArrayList<CountryEntity>) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)

        countryAdapter = context?.let { CountriesAdapter(it, countryList) }!!
        recyclerView.adapter = countryAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCountries(newText)
                return true
            }
        })
    }

    private fun filterCountries(query: String?) {
        val filteredList = countryArray?.filter {
            it.name.contains(query ?: "", ignoreCase = true) ||
                    it.capital.contains(query ?: "", ignoreCase = true) ||
                    it.continent.contains(query ?: "", ignoreCase = true)
        }
        filteredList?.let { ArrayList(it) }?.let { countryAdapter.updateList(it) }
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