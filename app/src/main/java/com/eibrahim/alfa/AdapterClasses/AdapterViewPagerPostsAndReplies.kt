package com.eibrahim.alfa.AdapterClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eibrahim.alfa.PostFragments.PostsUserFragment
import com.eibrahim.alfa.PostFragments.RepliesUserFragment


class AdapterViewPagerPostsAndReplies (fm : FragmentManager) : FragmentPagerAdapter(fm){

    private var fragments = listOf<Fragment>(PostsUserFragment(), RepliesUserFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}