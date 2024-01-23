package com.eibrahim.alfa.FragmentsShowrActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eibrahim.alfa.MainActivity.ProfileFragment
import com.eibrahim.alfa.PostFragments.AddPostFragment
import com.eibrahim.alfa.PostFragments.ShowImageFragment
import com.eibrahim.alfa.PostFragments.ShowPostFragment
import com.eibrahim.alfa.R

public var no_page : Int = 0
public var ShowedImageUrl : String = ""

class FragmentsViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val ProfileFragment = ProfileFragment()
        val SettingsFragment = SettingsFragment()
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
            1 -> fragmentTransaction.replace(R.id.container_MA2, SettingsFragment)
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