package com.example.nexum

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.nexum.EventFragment.Companion.newEventInstance
import com.example.nexum.OpportunitiesFragment.Companion.newOppoInstance
import com.example.nexum.SharedFragment.Companion.newSharedInstance
import com.example.nexum.adapter.ProfileTabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TabsFragment() : Fragment() {

    private val events = mutableListOf<Fragment>(newEventInstance<EventFragment>("0"),newEventInstance<EventFragment>("1"))
    private val opportunities=mutableListOf<Fragment>(newOppoInstance<OpportunitiesFragment>("0"), newOppoInstance<OpportunitiesFragment>("1"))
    private val shared=mutableListOf<Fragment>(newSharedInstance<SharedFragment>("0"),newSharedInstance<SharedFragment>("1"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = when(arguments?.getString("fragment")){
            "events"-> events
            "opportunities"->opportunities
            "shared"->shared
            else-> events
        }
        val tabsViewPager: ViewPager2 = view.findViewById(R.id.viewPager)

        val tabsAdapter = ProfileTabAdapter(this,fragment)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        tabsViewPager.adapter=tabsAdapter

        val tabs= mutableListOf("Posts","Your Posts")
        TabLayoutMediator(tabLayout,tabsViewPager) {tab,position->
            tab.text = tabs[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })

    }

}