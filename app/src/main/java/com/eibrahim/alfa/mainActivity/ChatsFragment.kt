package com.eibrahim.alfa.mainActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.alfa.R
import com.eibrahim.alfa.adapterClasses.AdapterRecyclerviewChats
import com.eibrahim.alfa.adapterClasses.AdapterRecyclerviewStories


class ChatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        val recyclerViewChats :RecyclerView = root.findViewById(R.id.recyclerview_chats)


        val dataList : List<String> = listOf("1","1","1","1","1","1","1")

        val adapterChats = AdapterRecyclerviewChats(dataList, requireContext())

        recyclerViewChats.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerViewChats.adapter = adapterChats



        return root
    }




}