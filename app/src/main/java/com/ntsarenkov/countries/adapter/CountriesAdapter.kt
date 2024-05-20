package com.ntsarenkov.countries.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.ntsarenkov.countries.databinding.ItemCountryBinding
import com.ntsarenkov.countries.model.CountryData
import com.ntsarenkov.countries.model.CountryDetails
import com.ntsarenkov.countries.view.CountryDetailsActivity
import com.squareup.picasso.Picasso

class CountriesAdapter(
    private var context: Context, private var countryList: ArrayList<CountryData>
) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(filteredList: ArrayList<CountryData>) {
        this.countryList = filteredList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var countryName = countryList[position].name.common
        holder.binding.tvCountryName.text = countryName

        Picasso.get().load(
            countryList[position].flags.png
        ).into(holder.binding.flagImage)

        holder.itemView.setOnClickListener {
            val country = countryList[position]

            val intent = Intent(context, CountryDetailsActivity::class.java)
            intent.putExtra(
                "country", CountryDetails(
                    country.name.common,
                    country.capital[0],
                    country.population,
                    country.currencies[country.currencies.keys.first()]?.name,
                    country.continents[0],
                    country.languages,
                    country.maps.googleMaps,
                    country.flags.png,
                    country.startOfWeek
                )
            )

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                holder.binding.flagImage,
                ViewCompat.getTransitionName(holder.binding.flagImage) ?: ""
            )
            context.startActivity(intent, options.toBundle())
        }
    }
}