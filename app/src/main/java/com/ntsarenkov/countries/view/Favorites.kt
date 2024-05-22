package com.ntsarenkov.countries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ntsarenkov.countries.adapter.CountriesAdapter
import com.ntsarenkov.countries.databinding.FragmentFavoritesBinding
import com.ntsarenkov.countries.model.AppDatabase
import com.ntsarenkov.countries.model.CountryEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class Favorites : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private lateinit var countryAdapter: CountriesAdapter
    private lateinit var database: AppDatabase
    private var favoriteCountries: ArrayList<CountryEntity>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        database = AppDatabase.getDatabase(requireContext())
        loadFavoriteCountries()
        return binding.root
    }
    private fun loadFavoriteCountries() {
        compositeDisposable?.add(
            database.countryDao().getAllCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ countryList ->
                    favoriteCountries = ArrayList(countryList.filter { it.isFavorite })
                    setRecyclerView(favoriteCountries!!)
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

    override fun onDestroy() {
        compositeDisposable?.clear()
        super.onDestroy()
    }
}