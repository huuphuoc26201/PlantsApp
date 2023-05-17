package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView

class detailsArticles : AppCompatActivity() {
    var tim:Int=0
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_articles)
        val title=findViewById<TextView>(R.id.textView17)
        val name=findViewById<TextView>(R.id.textView19)
        val date=findViewById<TextView>(R.id.textView20)
        val description=findViewById<TextView>(R.id.textView13)
        val image=findViewById<ImageView>(R.id.image)
        val imageAvt=findViewById<ImageView>(R.id.imageView)
        builder = AlertDialog.Builder(this)

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

        Glide.with(this).load(intent.getStringExtra("imageUrl"))
            .into(image)
        Glide.with(this).load(intent.getStringExtra("imageAvt"))
            .into(imageAvt)

        title.text = intent.getStringExtra("title")
        name.text = intent.getStringExtra("name")
        date.text = intent.getStringExtra("date")
        description.text = intent.getStringExtra("description")
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

    fun follow(view: View?){
        val fl=findViewById<TextView>(R.id.textView22)
        val unfl=findViewById<TextView>(R.id.textView23)
        fl.setVisibility(View.GONE)
        unfl.setVisibility(View.VISIBLE)
    }

    fun unfollow(view: View?){
        val text: String? =intent.getStringExtra("name");
        val fl=findViewById<TextView>(R.id.textView22)
        val unfl=findViewById<TextView>(R.id.textView23)
        builder.setTitle("Thông báo")
            .setMessage("Bạn có chắc chắn muốn hủy Follow "+text+"?")
            .setCancelable(true) // dialog box in cancellable
            // set positive button
            //take two parameters dialogInterface and an int
            .setPositiveButton("Hủy follow"){dialogInterface,it ->
                unfl.setVisibility(View.GONE)
                fl.setVisibility(View.VISIBLE)
            }
        builder.setNegativeButton("Huỷ") { dialog, which ->
            dialog.cancel()
        }
            // show the builder
            .show()

    }

    fun prev(view: View?){
        startActivity(Intent(this, articles::class.java))
        finish()
    }
}