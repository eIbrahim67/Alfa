@file:Suppress("DEPRECATION")

package com.eibrahim.alfa.adapterClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eibrahim.alfa.postFragments.PostsUserFragment
import com.eibrahim.alfa.postFragments.RepliesUserFragment


class AdapterViewPagerPostsAndReplies (fm : FragmentManager) : FragmentPagerAdapter(fm){

    private var fragments = listOf(PostsUserFragment(), RepliesUserFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}