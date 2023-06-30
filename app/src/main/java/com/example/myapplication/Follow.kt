package com.example.myapplication

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.articlesAdapter
import com.example.myapplication.Adapter.followAdapter
import com.example.myapplication.Adapter.followArticlesAdapter
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.followData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class Follow : AppCompatActivity() {
    lateinit var ekey:String
    lateinit var name:String
    lateinit var text:String
    private lateinit var dbref : DatabaseReference
    private lateinit var followRecyclerview : RecyclerView
    private lateinit var followArrayList : ArrayList<followData>
    private lateinit var articlesRecyclerview : RecyclerView
    private lateinit var articlesArrayList : ArrayList<articlesData>
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.person
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.person -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.home -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        followRecyclerview = findViewById(R.id.RecyclerViewFollow)
        followRecyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        followRecyclerview.setHasFixedSize(true)
        followArrayList = ArrayList()
        followArrayList = arrayListOf<followData>()
        val myadapter1=followAdapter(this@Follow, followArrayList)
        followRecyclerview.adapter = myadapter1
        getfollowData()


        articlesRecyclerview = findViewById(R.id.RecyclerViewArticles)
        articlesRecyclerview.layoutManager = LinearLayoutManager(this)
        articlesRecyclerview.setHasFixedSize(true)
        articlesArrayList = ArrayList()
        articlesArrayList = arrayListOf<articlesData>()
        val myadapter= followArticlesAdapter(this@Follow, articlesArrayList)
        articlesRecyclerview.adapter = myadapter
        getarrticlesData()


        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }

    }

    private fun getarrticlesData() {
        database = FirebaseDatabase.getInstance()
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            ekey = user?.key.toString()
                            val usersRef =
                                FirebaseDatabase.getInstance().getReference("Follow Channel")
                                    .child(ekey)
                            usersRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (childSnapshot in dataSnapshot.children) {
                                       // Lấy tên của nút
                                        val key = childSnapshot.key

                                        val dbref1 = FirebaseDatabase.getInstance().getReference("Articles")

                                        dbref1.addValueEventListener(object : ValueEventListener {

                                            override fun onDataChange(snapshot: DataSnapshot) {

                                                if (snapshot.exists()){
                                                    for (userSnapshot in snapshot.children){
                                                        val articlesdata = userSnapshot.getValue(articlesData::class.java)
                                                        if (articlesdata != null) {
                                                            if (key != null) {
                                                                if (articlesdata.name?.lowercase()
                                                                        ?.contains(key.lowercase(Locale.getDefault())) == true
                                                                )
                                                                    articlesArrayList.add(articlesdata!!)
                                                            }
                                                        }
                                                    }

                                                    articlesRecyclerview.adapter = followArticlesAdapter(this@Follow, articlesArrayList)


                                                }

                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })

                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Xử lý khi có lỗi xảy ra
                                    Log.w(TAG, "Failed to read value.", error.toException())
                                }
                            })

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getfollowData() {
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database = FirebaseDatabase.getInstance()
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            ekey = user?.key.toString()
                            dbref =
                                FirebaseDatabase.getInstance().getReference("Follow Channel")
                                    .child(ekey)
                            dbref.addValueEventListener(object : ValueEventListener {

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    if (snapshot.exists()){
                                        for (userSnapshot in snapshot.children){
                                            val followdata = userSnapshot.getValue(followData::class.java)
                                            followArrayList.add(followdata!!)

                                        }

                                        followRecyclerview.adapter = followAdapter(this@Follow, followArrayList)


                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }


                            })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Tạo reference tới file ảnh trên FirebaseStorage
            val storageRef = Firebase.storage.reference
            val users = FirebaseAuth.getInstance().currentUser ?: return
            val eemail = users.email
            val database = FirebaseDatabase.getInstance()
            database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (ds in dataSnapshot.children) {
                            val user = ds.getValue(userData::class.java)
                            if (user != null) {
                                name= user?.key.toString()
                                val imagesRef = storageRef.child("PostArticle").child(name).child("${UUID.randomUUID()}.jpg")
                                // Chuyển đổi bitmap thành byte array để upload lên FirebaseStorage
                                val baos = ByteArrayOutputStream()
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                val data = baos.toByteArray()

                                // Upload ảnh lên FirebaseStorage
                                val uploadTask = imagesRef.putBytes(data)

                                // Lắng nghe sự kiện hoàn thành upload
                                if (uploadTask != null) {
                                    uploadTask.continueWithTask { task ->
                                        if (!task.isSuccessful) {
                                            task.exception?.let { throw it }
                                        }

                                        imagesRef.downloadUrl
                                    }.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Lưu đường dẫn của hình ảnh vào database Realtime Database
                                            val downloadUri = task.result
                                            val intent = Intent(this@Follow, addingNew::class.java)
                                            intent.putExtra("message", downloadUri.toString())
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            // Xử lý khi không thể tải lên hình ảnh
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Xảy ra lỗi trong quá trình đọc dữ liệu

                }
            })



        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
            } else {
                // Permission has been denied
            }
        }
    }
    fun prev(view: View?){
        startActivity(Intent(this, Profile::class.java))
        finish()
    }
    fun addfollow(view: View?){
        startActivity(Intent(this, articles::class.java))
        finish()
    }
}