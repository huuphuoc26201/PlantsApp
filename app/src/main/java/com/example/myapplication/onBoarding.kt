package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.Adapter.ViewPagerAdapter
import me.relex.circleindicator.CircleIndicator

class onBoarding : AppCompatActivity() {
    private lateinit var circleIndicator:CircleIndicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        circleIndicator=findViewById(R.id.circle_indicator)
        val viewpager=findViewById<ViewPager>(R.id.viewpager)
        val next=findViewById<Button>(R.id.next)
        val signin=findViewById<Button>(R.id.signin)
        val skip=findViewById<TextView>(R.id.textView5)
        viewpager.adapter=ViewPagerAdapter(supportFragmentManager)
        circleIndicator.setViewPager(viewpager)


        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                if(position==2){
                    signin.visibility = View.VISIBLE
                    skip.visibility = View.GONE
                }else{
                    signin.visibility = View.GONE
                    skip.visibility = View.VISIBLE
                }
            }
        })


        if(viewpager.currentItem ==0) {
            signin.visibility = View.GONE
        }

        skip.setOnClickListener{
            viewpager.currentItem = 2
        }

        next.setOnClickListener{
        if(viewpager.currentItem <2) {
            viewpager.currentItem = viewpager.currentItem + 1
        }else{
            val intent = Intent(this, logIn::class.java)
            startActivity(intent)
        }
        }
        signin.setOnClickListener{
            val intent = Intent(this, logIn::class.java)
            startActivity(intent)
        }

    }
}