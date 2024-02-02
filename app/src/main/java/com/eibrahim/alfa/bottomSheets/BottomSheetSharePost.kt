package com.eibrahim.alfa.bottomSheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.eibrahim.alfa.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSharePost : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.bottom_sheet_share_post, container, false)

        val shareAsMessage : LinearLayout = root.findViewById(R.id.shareAsMessage)
        val shareVia : LinearLayout = root.findViewById(R.id.shareVia)

        shareAsMessage.setOnClickListener {



        }

        shareVia.setOnClickListener {

            val message = "we did not create a web site yet! sorry\uD83E\uDD79"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            this.startActivity(Intent.createChooser(shareIntent, "Share via"))

        }

        return root
    }



}