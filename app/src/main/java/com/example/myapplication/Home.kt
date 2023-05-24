package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.articlesAdapter
import com.example.myapplication.Adapter.phoToAdapter
import com.example.myapplication.Adapter.plantTypeAdapter
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.phoToData
import com.example.myapplication.model.plantTypeData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class Home : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var Recyclerview1 : RecyclerView
    private lateinit var arrayList : ArrayList<plantTypeData>
    private lateinit var arrayList1 : ArrayList<phoToData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
        val specie= findViewById<Button>(R.id.specise)
        val btnprofile=findViewById<CircleImageView>(R.id.profile_image)
        val addingnew=findViewById<Button>(R.id.adding_new)

        addingnew.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
        }


        profile()
        btnprofile.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            finish()

        }
        specie.setOnClickListener{
            startActivity(Intent(this, species::class.java))
            finish()
        }
        val article= findViewById<Button>(R.id.articles)
        article.setOnClickListener{
            startActivity(Intent(this, articles::class.java))
            finish()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.person -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.home -> {
                    true
                }
                else -> false
            }
        }

        userRecyclerview = findViewById(R.id.RecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        userRecyclerview.setHasFixedSize(true)
        arrayList = ArrayList()
        arrayList = arrayListOf<plantTypeData>()
        getUserData()

        Recyclerview1 = findViewById(R.id.RecyclerView1)
        Recyclerview1.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        Recyclerview1.setHasFixedSize(true)
        arrayList1 = ArrayList()
        arrayList1 = arrayListOf<phoToData>()
        getUserData1()
    }


    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Plants Types")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val planttypedata = userSnapshot.getValue(plantTypeData::class.java)
                        arrayList.add(planttypedata!!)

                    }

                    userRecyclerview.adapter = plantTypeAdapter(this@Home, arrayList)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
    private fun getUserData1() {
        dbref = FirebaseDatabase.getInstance().getReference("Photography")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val photoypedata = userSnapshot.getValue(phoToData::class.java)
                        arrayList1.add(photoypedata!!)

                    }

                    Recyclerview1.adapter = phoToAdapter(this@Home, arrayList1)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
    private fun profile() {
        val image=findViewById<CircleImageView>(R.id.profile_image)
        val tvname=findViewById<TextView>(R.id.tvname)
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val name = users.displayName
        val eemail = users.email
        val photoUrl: Uri? = users.photoUrl
        tvname.text = "Hello "+name+","
        Glide.with(this@Home).load(photoUrl).error(R.drawable.img_2)
            .into(image)
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    if (user != null) {
                        tvname.text = "Hello "+user?.fullName+","
                        Glide.with(this@Home).load(user?.imageAvt).error(R.drawable.img_2)
                            .into(image)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Tạo reference tới file ảnh trên FirebaseStorage
            val storageRef = Firebase.storage.reference
            val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

            // Chuyển đổi bitmap thành byte array để upload lên FirebaseStorage
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Upload ảnh lên FirebaseStorage
            val uploadTask = imagesRef.putBytes(data)

            // Lắng nghe sự kiện hoàn thành upload
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, addingNew::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, Home::class.java))
                    finish()
                }
            }
        }
    }

}