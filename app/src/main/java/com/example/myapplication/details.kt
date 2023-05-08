package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
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
        startActivity(Intent(this, listSpecies::class.java))
        finish()
    }
}

