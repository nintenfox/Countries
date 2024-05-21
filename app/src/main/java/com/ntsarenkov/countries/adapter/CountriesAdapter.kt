package com.ntsarenkov.countries.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.ntsarenkov.countries.databinding.ItemCountryBinding
import com.ntsarenkov.countries.model.CountryEntity
import com.ntsarenkov.countries.view.CountryDetailsActivity
import com.squareup.picasso.Picasso

class CountriesAdapter(
    private var context: Context, private var countryList: ArrayList<CountryEntity>
) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countryList[position]
        holder.binding.tvCountryName.text = country.name

        Picasso.get().load(country.flag).into(holder.binding.flagImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CountryDetailsActivity::class.java)
            intent.putExtra("country", country)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                holder.binding.flagImage,
                ViewCompat.getTransitionName(holder.binding.flagImage) ?: ""
            )
            context.startActivity(intent, options.toBundle())
        }
    }

    fun updateList(newCountryList: ArrayList<CountryEntity>) {
        countryList = newCountryList
        notifyDataSetChanged()
    }
}