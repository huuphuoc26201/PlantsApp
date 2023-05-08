package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val specie= findViewById<Button>(R.id.specise)

        specie.setOnClickListener{
            startActivity(Intent(this, species::class.java))
            finish()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    true
                }
                R.id.person -> {
                    startActivity(Intent(this, details::class.java))
                    true
                }
                else -> false
            }
        }
    }
}