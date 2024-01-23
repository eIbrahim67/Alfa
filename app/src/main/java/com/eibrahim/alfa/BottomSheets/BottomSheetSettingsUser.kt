package com.eibrahim.alfa.BottomSheets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.eibrahim.alfa.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.eibrahim.alfa.FragmentsShowrActivity.*
import com.eibrahim.alfa.MainActivity.MainActivity
import com.google.firebase.auth.FirebaseAuth

class BottomSheetSettingsUser(

    private var context: Context? = null

) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_settings_user, container, false)

        // Reference each view in the layout
        val btnEditProfile: LinearLayout = root.findViewById(R.id.btn_edit_profile)
        val btnBookmarks: LinearLayout = root.findViewById(R.id.btn_bookmarks)
        val btnHiddenPosts: LinearLayout = root.findViewById(R.id.btn_hidden_posts)
        val btnFaqs: LinearLayout = root.findViewById(R.id.btn_faqs)
        val btnCommunity: LinearLayout = root.findViewById(R.id.btn_community)
        val btnLogout: LinearLayout = root.findViewById(R.id.btn_logout)

        val fragmentsShowrActivity = FragmentsViewerActivity()
        val intent = Intent(context, fragmentsShowrActivity::class.java)

        btnEditProfile.setOnClickListener {
            no_page = 5
            startActivity(intent)
        }

        btnBookmarks.setOnClickListener {
            no_page = 2
            startActivity(intent)
        }

        btnHiddenPosts.setOnClickListener {
            //no_page =
            //startActivity(intent)
        }

        btnFaqs.setOnClickListener {
            no_page = 3
            startActivity(intent)
        }

        btnCommunity.setOnClickListener {
            no_page = 4
            startActivity(intent)
        }

        btnLogout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            val intentMainActivity = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentMainActivity)
            activity?.finish()
        }

        return root
    }
}
