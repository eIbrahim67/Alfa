package com.eibrahim.alfa.fragmentsShowrActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eibrahim.alfa.mainActivity.ProfileFragment
import com.eibrahim.alfa.postFragments.AddPostFragment
import com.eibrahim.alfa.postFragments.ShowImageFragment
import com.eibrahim.alfa.postFragments.ShowPostFragment
import com.eibrahim.alfa.R

var no_page : Int? = null
var ShowedImageUrl : String = ""
var ShowedUserAccount : String = ""
var myAccount : Boolean = true

class FragmentsViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val ProfileFragment = ProfileFragment()
        val BookmarksFragment = BookmarksFragment()
        val SupportFragment = SupportFragment()
        val CommunityFragment = CommunityFragment()
        val EditPersonalDataFragment = EditPersonalDataFragment()
        val AddPostFragment = AddPostFragment()
        val showImageFragment = ShowImageFragment()
        val showPostFragment = ShowPostFragment()

        ///



        when (no_page) {
            0 -> fragmentTransaction.replace(R.id.container_MA2, ProfileFragment)
            //1 -> fragmentTransaction.replace(R.id.container_MA2, SettingsFragment)
            2 -> fragmentTransaction.replace(R.id.container_MA2, BookmarksFragment)
            3 -> fragmentTransaction.replace(R.id.container_MA2, SupportFragment)
            4 -> fragmentTransaction.replace(R.id.container_MA2, CommunityFragment)
            5 -> fragmentTransaction.replace(R.id.container_MA2, EditPersonalDataFragment)
            6 -> fragmentTransaction.replace(R.id.container_MA2, showImageFragment)
            7 -> fragmentTransaction.replace(R.id.container_MA2, AddPostFragment)
            8 -> fragmentTransaction.replace(R.id.container_MA2, showPostFragment)

        }

        fragmentTransaction.commit()
    }
}