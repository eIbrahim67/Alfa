package com.eibrahim.alfa.mainActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.alfa.R
import com.eibrahim.alfa.adapterClasses.AdapterRecyclerviewChats


class ChatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        val recyclerViewChats :RecyclerView = root.findViewById(R.id.recyclerview_chats)


        val dataList : List<String> = listOf("1","2","3","4","5","6","7","5","6","7","5","6","7")

        val adapterChats = AdapterRecyclerviewChats(dataList)

        recyclerViewChats.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerViewChats.adapter = adapterChats



        return root
    }




}