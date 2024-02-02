package com.eibrahim.alfa.mainActivity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.eibrahim.alfa.dialogs.DialogRequiredUpdate
import com.eibrahim.alfa.R
import com.google.firebase.firestore.FirebaseFirestore
import me.ibrahimsn.lib.SmoothBottomBar


class MainActivity : AppCompatActivity() {

    private lateinit var btnBar: SmoothBottomBar
    private val version = "1.0"

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ///

        FirebaseFirestore.getInstance().collection("app").document("version")
            .get().addOnSuccessListener {

                val data = it.data
                if (data != null){

                    val version = data["version"] as String

                    if (version != this.version){

                        val dialogRequiredUpdate = DialogRequiredUpdate(this)
                        dialogRequiredUpdate.show(supportFragmentManager, "version check")

                    }

                }

            }



        btnBar = findViewById(R.id.bottomBar)
        val fragmentManager: FragmentManager = supportFragmentManager

        val homeFragment = HomeFragment()
        val researcherFragment = ResearcherFragment()
        val chatsFragment = ChatsFragment()
        val profileFragment = ProfileFragment()

        var lastFragment : Fragment = homeFragment


        var initialTransaction: FragmentTransaction?

        for (i in 0..3){

            when (i) {
                0 -> {
                    lastFragment = homeFragment
                }
                1 -> {
                    lastFragment = researcherFragment
                }
                2 -> {
                    lastFragment = chatsFragment
                }
                3  -> {
                    lastFragment = profileFragment
                }
            }

            initialTransaction = fragmentManager.beginTransaction()
            initialTransaction.add(R.id.frame_layout_ButtonBar, lastFragment)
            initialTransaction.commit()

            initialTransaction = fragmentManager.beginTransaction()
            initialTransaction.hide(lastFragment)
            initialTransaction.commit()

        }

        initialTransaction = fragmentManager.beginTransaction()
        initialTransaction.show(homeFragment)
        initialTransaction.commit()

        lastFragment = homeFragment
        btnBar.setOnItemSelectedListener { item ->

            var currentFragment : Fragment? = null
            when (item) {
                0 -> {
                    currentFragment = homeFragment
                }
                1 -> {
                    currentFragment = researcherFragment
                }
                2 -> {
                    currentFragment = chatsFragment
                }
                3  -> {
                    currentFragment = profileFragment
                }
            }
            initialTransaction = fragmentManager.beginTransaction()
            initialTransaction!!.hide(lastFragment)
            initialTransaction!!.commit()

            initialTransaction = fragmentManager.beginTransaction()
            initialTransaction!!.show(currentFragment!!)
            initialTransaction!!.commit()

            lastFragment = currentFragment
        }

    }
}


