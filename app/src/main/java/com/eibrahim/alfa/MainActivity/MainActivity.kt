package com.eibrahim.alfa.MainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.eibrahim.alfa.AdapterClasses.adapter_vb_main
import com.eibrahim.alfa.R
import me.ibrahimsn.lib.SmoothBottomBar


class MainActivity : AppCompatActivity() {

    public lateinit var btn_bar: SmoothBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ///

        val updates = hashMapOf<String, Any>()
        updates["id"] = "mimi"
        updates["userId"] = updates
        updates.remove("id")

        val viewPager : ViewPager = findViewById(R.id.viewpager_ButtonBar)
        btn_bar = findViewById(R.id.bottomBar)

        btn_bar.setOnClickListener { btn_bar.visibility = View.GONE }

        viewPager.adapter = adapter_vb_main(supportFragmentManager)
        viewPager.currentItem = 0


        btn_bar.onItemSelected = {
            viewPager.currentItem = it
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(I: Int) {
                btn_bar.itemActiveIndex = I
            }
        })

    }
}

