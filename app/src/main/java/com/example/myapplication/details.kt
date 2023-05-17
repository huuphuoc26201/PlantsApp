package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView

class details : AppCompatActivity() {
    var tim:Int=0
    private lateinit var text: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val namespecies=findViewById<TextView>(R.id.textView17)
        val description=findViewById<TextView>(R.id.textView13)
        val evaluate=findViewById<TextView>(R.id.textView18)
        val image=findViewById<ImageView>(R.id.image)
        val ratingbar=findViewById<RatingBar>(R.id.ratingBar)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, Home::class.java))
                    true
                }
                R.id.person -> {
                    startActivity(Intent(this, details::class.java))
                    true
                }
                else -> false
            }
        }

        text= intent.getStringExtra("evaluate").toString()
        val f: Float = text.toFloat()
        namespecies.text = intent.getStringExtra("namespecies")
        description.text = intent.getStringExtra("description")
        evaluate.text = intent.getStringExtra("evaluate")
        ratingbar.rating = f
        Glide.with(this).load(intent.getStringExtra("imageUrl"))
            .into(image)
        
    }
    fun tym(view: View?) {
        val thatym=findViewById<CircleImageView>(R.id.thatym)
        tim++
        if (tim % 2 === 0) {
            thatym.setImageResource(R.drawable.icon_favorite_love)
        } else {
            thatym.setImageResource(R.drawable.icons_love)
        }
    }
    fun prev(view: View?){
        startActivity(Intent(this, species::class.java))
        finish()
    }
}

