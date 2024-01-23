package com.eibrahim.alfa.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.alfa.R

class AdapterRecyclerviewTags(private var listOfData: ArrayList<String>, private var contect : Context)
    : RecyclerView.Adapter<TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        // Create and return a new ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {

        return listOfData.size

    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {

        if (position == 0)
            holder.spaceMarginStart.visibility = View.VISIBLE

        holder.text_of_tage.setOnClickListener {

            val adapterPosition = holder.adapterPosition

            if (adapterPosition != RecyclerView.NO_POSITION) {
                listOfData.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                Toast.makeText(contect, "the tag removed", Toast.LENGTH_SHORT).show()
            }

        }

        holder.text_of_tage.setText(listOfData[position])

    }
}

class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val text_of_tage: TextView = itemView.findViewById(R.id.text_of_tage)
    val spaceMarginStart : Space = itemView.findViewById(R.id.spaceMarginStart)

}