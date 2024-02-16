package com.eibrahim.alfa.adapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.alfa.R

@Suppress("DEPRECATION")
class AdapterRecyclerviewStories(private var listOfData: List<String>, private var context : Context)
    : RecyclerView.Adapter<StoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        // Create and return a new ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_stories, parent, false)
        return StoriesViewHolder(view)
    }

    override fun getItemCount(): Int {

        return listOfData.size

    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {


    }
}

class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



}