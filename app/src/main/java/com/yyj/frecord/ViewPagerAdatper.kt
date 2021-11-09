package com.yyj.frecord

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdatper (fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    val fgList = listOf<Fragment>(MessageActivity(), RecordActivity(), GraphActivity())

    override fun getItemCount() :Int {
        return fgList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fgList[position]
    }
}