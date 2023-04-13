package com.example.myapplication.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.example.myapplication.onboarding.Firstscreen
import com.example.myapplication.onboarding.Secondscreen
import com.example.myapplication.onboarding.Thirdscreen

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager){

    private val fragments= listOf(
        Firstscreen(),
        Secondscreen(),
        Thirdscreen()
    )
    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return  fragments[position]
    }
}