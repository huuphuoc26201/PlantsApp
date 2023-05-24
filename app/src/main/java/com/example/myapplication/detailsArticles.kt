package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.listSpeciesAdapter
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.listSpeciesData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class detailsArticles : AppCompatActivity() {
    var tim:Int=0
    var n:Int=0
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var text: String
    private lateinit var t: String
    private lateinit var t1: String
    private lateinit var t2: String
    private lateinit var t3: String
    private lateinit var t4: String
    private lateinit var t5: String
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
        val thatym=findViewById<CircleImageView>(R.id.thatym)

        text= intent.getStringExtra("name").toString()

        val usersRef = FirebaseDatabase.getInstance().getReference("Favorite Articles")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val childName = childSnapshot.child("name").getValue(String::class.java)
                    if (childName == text) {
                        thatym.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có.
            }
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, Home::class.java))
                    true
                }
                R.id.person -> {
                    startActivity(Intent(this, Profile::class.java))
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

    fun follow(view: View?){
        val fl=findViewById<TextView>(R.id.textView22)
        val unfl=findViewById<TextView>(R.id.textView23)
        fl.visibility = View.GONE
        unfl.visibility = View.VISIBLE
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
                unfl.visibility = View.GONE
                fl.visibility = View.VISIBLE
            }
        builder.setNegativeButton("Huỷ") { dialog, which ->
            dialog.cancel()
        }
            // show the builder
            .show()

    }
    fun tym(view: View?){
        t = intent.getStringExtra("imageUrl").toString()
        t1 = intent.getStringExtra("imageAvt").toString()
        t2 = intent.getStringExtra("title").toString()
        t3 = intent.getStringExtra("name").toString()
        t4 = intent.getStringExtra("date").toString()
        t5 = intent.getStringExtra("description").toString()

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        val articles= articlesData(t,t1, t2,t3,t4,t5)
        val ref = database.getReference("Favorite Articles")

        val thatym=findViewById<CircleImageView>(R.id.thatym)
        tim++
        if (tim % 2 == 0) {
            val database = Firebase.database.reference
            database.child("Favorite Articles").child(t3).removeValue()
            thatym.setImageResource(R.drawable.icon_favorite_love)
        } else {
            val key = ref.push().key
            key?.let {
                val userRef = ref.child(t3)
                userRef.setValue(articles)
            }
            thatym.setImageResource(R.drawable.icons_love)
        }
    }
    fun untym(view: View?){
        t = intent.getStringExtra("imageUrl").toString()
        t1 = intent.getStringExtra("imageAvt").toString()
        t2 = intent.getStringExtra("title").toString()
        t3 = intent.getStringExtra("name").toString()
        t4 = intent.getStringExtra("date").toString()
        t5 = intent.getStringExtra("description").toString()

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        val articles= articlesData(t,t1, t2,t3,t4,t5)
        val ref = database.getReference("Favorite Articles")

        val unthatym=findViewById<CircleImageView>(R.id.unthatym)
        n++
        if (n % 2 != 0) {
            val database = Firebase.database.reference
            database.child("Favorite Articles").child(t3).removeValue()
            unthatym.setImageResource(R.drawable.icon_favorite_love)
        } else {
            val key = ref.push().key
            key?.let {
                val userRef = ref.child(t3)
                userRef.setValue(articles)
            }
            unthatym.setImageResource(R.drawable.icons_love)
        }
    }

    fun prev(view: View?){
        startActivity(Intent(this, articles::class.java))
        finish()
    }
}