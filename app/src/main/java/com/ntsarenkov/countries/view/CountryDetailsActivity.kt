package com.ntsarenkov.countries.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.ntsarenkov.countries.R
import com.ntsarenkov.countries.databinding.ActivityCountryDetailsBinding
import com.ntsarenkov.countries.model.AppDatabase
import com.ntsarenkov.countries.model.CountryEntity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.Locale

class CountryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryDetailsBinding
    private lateinit var database: AppDatabase
    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()
    private var country: CountryEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        country = intent.getSerializableExtra("country") as CountryEntity
        country?.let { setContents(it) }
        binding.favoriteButton.setOnClickListener {
            country?.let {
                it.isFavorite = !it.isFavorite
                updateCountry(it)
            }
        }
        val countryDetails = intent.getSerializableExtra("country") as CountryEntity
        setContents(countryDetails)
    }

    private fun setContents(countryDetails: CountryEntity) {

        Picasso.get().load(countryDetails.flag).into(binding.detailsImage)
        ViewCompat.setTransitionName(binding.detailsImage, "shared_image")
        ActivityCompat.postponeEnterTransition(this)
        binding.detailsImage.let {
            it.doOnPreDraw {
                ActivityCompat.startPostponedEnterTransition(this@CountryDetailsActivity)
            }
        }
        binding.countryNameTxt.text = countryDetails.name
        binding.capitalTxt.text = countryDetails.capital
        binding.languageTxt.text = countryDetails.language
        binding.populationTxt.text = formatNumberWithCommas(countryDetails.population)
        binding.currencyTxt.text = countryDetails.currency
        binding.continentTxt.text = countryDetails.continent
        binding.startOfWeekTxt.text = countryDetails.startOfWeek
        binding.googleMapImg.setOnClickListener {
            val uri = Uri.parse(countryDetails.maps)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun formatNumberWithCommas(number: Long): String {
        val numberFormat = NumberFormat.getInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
    private fun updateCountry(country: CountryEntity) {
        compositeDisposable?.add(
            io.reactivex.Completable.fromAction {
                database.countryDao().insertAll(listOf(country))
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(
                        this,
                        if (country.isFavorite) "Added to Favorites" else "Removed from Favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateFavoriteButton(country.isFavorite)
                }, { throwable ->
                    throwable.printStackTrace()
                })
        )
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.ic_delete)
            binding.favoriteButton.contentDescription = getString(R.string.remove_from_favorites)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.ic_plus)
            binding.favoriteButton.contentDescription = getString(R.string.add_to_favorites)
        }
    }
}