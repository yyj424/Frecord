package com.yyj.frecord

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private val fgList = listOf<Fragment>(MessageActivity(), RecordActivity(), FCalendarActivity())

    override fun getItemCount() :Int {
        return fgList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fgList[position]
    }
}