package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class splashScreen : AppCompatActivity() {
    lateinit var preference:SharedPreferences
    val pre_show_intro="intro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preference=getSharedPreferences("IntroSlider", Context.MODE_PRIVATE)

        supportActionBar?.hide()

        val handler= Handler(Looper.getMainLooper())
        handler.postDelayed({
            //Kiểm tra ứng dụng chạy lần đầu trên điện thoại
            if(!preference.getBoolean(pre_show_intro,true)){
                netActivity()
            }else{
                GoToDashboard()
            }
        },3000)
    }

    private fun GoToDashboard() {
        val intent = Intent(this, onBoarding::class.java)
        startActivity(intent)
        val editor=preference.edit()
        editor.putBoolean(pre_show_intro,false)
        editor.apply()
    }

    //kiểm tra ứng dụng đã đăng nhập hay chưa
    private fun netActivity() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            //chưa login
            val intent = Intent(this, logIn::class.java)
            startActivity(intent)

        } else {
            //đã login
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        finish()
    }
}