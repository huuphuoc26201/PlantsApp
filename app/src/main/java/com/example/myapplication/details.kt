package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.model.listSpeciesData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class details : AppCompatActivity() {
    var tim:Int=0
    var n:Int=0
    private lateinit var database: FirebaseDatabase
    private lateinit var text: String
    private lateinit var ename: String
    private lateinit var edescription: String
    private lateinit var eevaluate: String
    private lateinit var image: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val namespecies = findViewById<TextView>(R.id.textView17)
        val description = findViewById<TextView>(R.id.textView13)
        val evaluate = findViewById<TextView>(R.id.textView18)
        val image = findViewById<ImageView>(R.id.image)
        val ratingbar = findViewById<RatingBar>(R.id.ratingBar)
        database = FirebaseDatabase.getInstance()
        val thatym=findViewById<CircleImageView>(R.id.thatym)
        val unthatym=findViewById<CircleImageView>(R.id.unthatym)


        val usersRef = FirebaseDatabase.getInstance().getReference("Favorite Species")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val childName = childSnapshot.child("namespecies").getValue(String::class.java)
                    if (childName == intent.getStringExtra("namespecies")) {
                        thatym.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu có.
            }
        })


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
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

        text = intent.getStringExtra("evaluate").toString()
        val f: Float = text.toFloat()
        namespecies.text = intent.getStringExtra("namespecies")
        description.text = intent.getStringExtra("description")
        evaluate.text = intent.getStringExtra("evaluate")
        ratingbar.rating = f
        Glide.with(this).load(intent.getStringExtra("imageUrl"))
            .into(image)

    }

    fun tym(view: View?) {
        ename= intent.getStringExtra("namespecies").toString()
        eevaluate= intent.getStringExtra("evaluate").toString()
        edescription= intent.getStringExtra("description").toString()
        image = intent.getStringExtra("imageUrl").toString()
        val Species= listSpeciesData(image,ename, eevaluate,edescription)
        val ref = database.getReference("Favorite Species")
        val thatym=findViewById<CircleImageView>(R.id.thatym)

        tim++
        if (tim % 2 == 0) {
            val database = Firebase.database.reference
            database.child("Favorite Species").child(ename).removeValue()
            thatym.setImageResource(R.drawable.icon_favorite_love)
        } else {

            val key = ref.push().key
            key?.let {
                val userRef = ref.child(ename)
                userRef.setValue(Species)
            }
            thatym.setImageResource(R.drawable.icons_love)
        }
    }
    fun untym(view: View?) {
        ename= intent.getStringExtra("namespecies").toString()
        eevaluate= intent.getStringExtra("evaluate").toString()
        edescription= intent.getStringExtra("description").toString()
        image = intent.getStringExtra("imageUrl").toString()
        val Species= listSpeciesData(image,ename, eevaluate,edescription)
        val ref = database.getReference("Favorite Species")
        val unthatym=findViewById<CircleImageView>(R.id.unthatym)

        n++
        if (n % 2 != 0) {
            val database = Firebase.database.reference
            database.child("Favorite Species").child(ename).removeValue()
            unthatym.setImageResource(R.drawable.icon_favorite_love)
        } else {

            val key = ref.push().key
            key?.let {
                val userRef = ref.child(ename)
                userRef.setValue(Species)
            }
            unthatym.setImageResource(R.drawable.icons_love)
        }
    }

    fun prev(view: View?){
        startActivity(Intent(this, species::class.java))
        finish()
    }
}

