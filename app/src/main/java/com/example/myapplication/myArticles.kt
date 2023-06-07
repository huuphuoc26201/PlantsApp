package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.myArticlesAdapter
import com.example.myapplication.Adapter.mySpeciesAdapter
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.listSpeciesData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*
import android.Manifest;
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class myArticles : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    private lateinit var RcvSpecies : RecyclerView
    private lateinit var RcvArticles : RecyclerView
    private lateinit var speciesArrayList : ArrayList<listSpeciesData>
    private lateinit var articlesArrayList : ArrayList<articlesData>
    lateinit var name:String
    lateinit var species: Button
    lateinit var articles: Button
    lateinit var keyname:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myarticles)

        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }

        species=findViewById(R.id.btnsp)
        articles=findViewById(R.id.btnar)
        RcvSpecies = findViewById(R.id.RecyclerViewSpecies)
        RcvArticles = findViewById(R.id.RecyclerViewArticles)
        RcvSpecies.layoutManager = LinearLayoutManager(this)
        RcvSpecies.setHasFixedSize(true)
        RcvArticles.layoutManager = LinearLayoutManager(this)
        RcvArticles.setHasFixedSize(true)

        speciesArrayList = ArrayList()
        articlesArrayList = ArrayList()
        speciesArrayList = arrayListOf<listSpeciesData>()
        articlesArrayList = arrayListOf<articlesData>()

        // my aricles
        val myadapter=  mySpeciesAdapter(this@myArticles, speciesArrayList)
        RcvSpecies.adapter = myadapter
        val myadapter1=  myArticlesAdapter(this@myArticles, articlesArrayList)
        RcvArticles.adapter = myadapter1

        getSpeciesData()
        getAriclesData()
        species.setOnClickListener {
            species.setBackgroundResource(R.drawable.custom_button_5)
            articles.setBackgroundResource(R.drawable.custom_button_8)
            RcvArticles.visibility=View.GONE
            RcvSpecies.visibility=View.VISIBLE

        }
        articles.setOnClickListener {
            species.setBackgroundResource(R.drawable.custom_button_8)
            articles.setBackgroundResource(R.drawable.custom_button_5)
            RcvSpecies.visibility=View.GONE
            RcvArticles.visibility=View.VISIBLE
        }
        //menu bottom
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
    }

    private fun getAriclesData() {
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    keyname=user?.key.toString()
                    dbref = FirebaseDatabase.getInstance().getReference("AddingNew").child(keyname).child("Articles")
                    dbref.addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.exists()){

                                for (userSnapshot in snapshot.children){
                                    val articlesdata = userSnapshot.getValue(articlesData::class.java)
                                    articlesArrayList.add(articlesdata!!)

                                }

                                RcvArticles.adapter = myArticlesAdapter(this@myArticles, articlesArrayList)
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    private fun getSpeciesData() {
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    keyname=user?.key.toString()
                    dbref = FirebaseDatabase.getInstance().getReference("AddingNew").child(keyname).child("Species")
                    dbref.addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.exists()){

                                for (userSnapshot in snapshot.children){


                                    val speciesdata = userSnapshot.getValue(listSpeciesData::class.java)
                                    if (speciesdata != null) {
                                        speciesArrayList.add(speciesdata!!)
                                    }

                                }

                                RcvSpecies.adapter = mySpeciesAdapter(this@myArticles, speciesArrayList)


                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })
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
                                            val intent = Intent(this@myArticles, addingNew::class.java)
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
    fun addingnew(view: View?){
        startActivity(Intent(this, addingNew::class.java))
        finish()
    }
}
