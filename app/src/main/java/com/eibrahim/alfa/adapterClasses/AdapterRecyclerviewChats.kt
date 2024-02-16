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
class AdapterRecyclerviewChats(private var listOfData: List<String>, private var context : Context)
    : RecyclerView.Adapter<ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Create and return a new ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {

        return listOfData.size

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {


    }
}

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



}