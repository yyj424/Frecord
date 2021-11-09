package com.yyj.frecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewpagerFragmentAdapter = ViewPagerAdatper(this)
        viewPager.adapter = viewpagerFragmentAdapter
        viewPager.setCurrentItem(1, false)
    }
}