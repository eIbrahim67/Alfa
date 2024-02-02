package com.eibrahim.alfa.fragmentsShowrActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.eibrahim.alfa.R

class SettingsFragment : Fragment() {

    private lateinit var backBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val Root = inflater.inflate(R.layout.fragment_settings, container, false)
        ///
        backBtn = Root.findViewById(R.id.back_settings_btn)


        backBtn.setOnClickListener {

            requireActivity().finish()

        }

        return Root
    }

}