package com.eibrahim.alfa.AdapterClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eibrahim.alfa.MainActivity.ChatsFragment
import com.eibrahim.alfa.MainActivity.HomeFragment
import com.eibrahim.alfa.MainActivity.ProfileFragment
import com.eibrahim.alfa.MainActivity.ResearcherFragment


class adapter_vb_main (fm : FragmentManager) : FragmentPagerAdapter(fm){

    private var fragments = listOf<Fragment>(HomeFragment(), ResearcherFragment(), ChatsFragment(), ProfileFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}