package com.ntsarenkov.countries.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.ntsarenkov.countries.databinding.ActivityCountryDetailsBinding
import com.ntsarenkov.countries.model.CountryDetails
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class CountryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countryDetails = intent.getSerializableExtra("country") as CountryDetails

        setContents(countryDetails)
    }

    private fun setContents(countryDetails: CountryDetails) {

        Picasso.get().load(countryDetails.flag).into(binding.detailsImage)

        ViewCompat.setTransitionName(binding.detailsImage, "shared_image")

        ActivityCompat.postponeEnterTransition(this)
        binding.detailsImage.let {
            it.doOnPreDraw {
                ActivityCompat.startPostponedEnterTransition(this@CountryDetailsActivity)
            }
        }

        binding.countryNameTxt.text = countryDetails.countryName
        binding.capitalTxt.text = countryDetails.capital
        binding.languageTxt.text = languageConvert(countryDetails.language)
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

    private fun languageConvert(languages: Map<String, String>): String {
        return languages.values.joinToString { it }
    }
}