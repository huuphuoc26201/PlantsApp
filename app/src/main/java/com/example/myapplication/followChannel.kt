package com.example.myapplication

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.*
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.followData
import com.example.myapplication.model.listSpeciesData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class followChannel : AppCompatActivity() {
    private lateinit var articlesRecyclerview : RecyclerView
    private lateinit var articlesArrayList : ArrayList<articlesData>
    private lateinit var dbref : DatabaseReference
    private lateinit var database: FirebaseDatabase
    lateinit var text:String
    lateinit var name:String
    lateinit var ekey:String
    private lateinit var builder : AlertDialog.Builder
    lateinit var follow:TextView
    lateinit var unfollow:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_channel)
        val namespecies=findViewById<TextView>(R.id.tvname)
        val enamespecies=findViewById<TextView>(R.id.tvname1)
        val image=findViewById<CircleImageView>(R.id.imageAvt)
        val ename:String=intent.getStringExtra("name").toString()
        builder = AlertDialog.Builder(this)
        val formattedData = ename.toLowerCase().capitalize()
        namespecies.text = formattedData
        enamespecies.text = formattedData+"  "
        Glide.with(this).load(intent.getStringExtra("imageAvt"))
            .into(image)

        follow=findViewById(R.id.follow)
        unfollow=findViewById(R.id.unfollow)
        follow.visibility=View.VISIBLE
        unfollow.visibility=View.GONE
        follow.setOnClickListener {
            val text: String? = intent.getStringExtra("name");
            builder.setTitle("Notification")
                .setMessage("Are you sure you want to unfollow $text?")
                .setCancelable(true) // dialog box in cancellable
                // set positive button
                //take two parameters dialogInterface and an int
                .setPositiveButton("Unfollow") { _, _ ->
                    follow.visibility=View.GONE
                    unfollow.visibility=View.VISIBLE
                    database = FirebaseDatabase.getInstance()
                    val users = FirebaseAuth.getInstance().currentUser ?: return@setPositiveButton
                    val eemail = users.email
                    database.getReference("Users").orderByChild("email").equalTo(eemail)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (ds in dataSnapshot.children) {
                                        val user = ds.getValue(userData::class.java)
                                        if (user != null) {
                                            ekey = user?.key.toString()
                                            val usersRef =
                                                FirebaseDatabase.getInstance()
                                                    .getReference("Follow Channel")
                                                    .child(ekey)
                                            usersRef.child(intent.getStringExtra("name").toString())
                                                .removeValue()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
                // show the builder
                .show()

        }
        unfollow.setOnClickListener {
            follow.visibility=View.VISIBLE
            unfollow.visibility=View.GONE
            database = FirebaseDatabase.getInstance()
            val users = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            val eemail = users.email
            database.getReference("Users").orderByChild("email").equalTo(eemail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (ds in dataSnapshot.children) {
                                val user = ds.getValue(userData::class.java)
                                if (user != null) {
                                    ekey = user?.key.toString()
                                    val usersRef =
                                        FirebaseDatabase.getInstance()
                                            .getReference("Follow Channel")
                                            .child(ekey)
                                    val followdata = followData(
                                        intent.getStringExtra("imageAvt").toString(),
                                        intent.getStringExtra("name").toString()
                                    )
                                    val key = usersRef.push().key
                                    key?.let {
                                        val userRef =
                                            usersRef.child(intent.getStringExtra("name").toString())
                                        userRef.setValue(followdata)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

        }

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
        articlesRecyclerview = findViewById(R.id.RecyclerViewArticles)
        articlesRecyclerview.layoutManager = LinearLayoutManager(this)
        articlesRecyclerview.setHasFixedSize(true)
        articlesArrayList = ArrayList()
        articlesArrayList = arrayListOf<articlesData>()

        val myadapter= followChannelAdapter(this@followChannel, articlesArrayList)
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
        text= intent.getStringExtra("name").toString()
        dbref = FirebaseDatabase.getInstance().getReference("Articles")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val articlesdata = userSnapshot.getValue(articlesData::class.java)
                        if (articlesdata != null) {
                            if (articlesdata.name?.lowercase()
                                    ?.contains(text.lowercase(Locale.getDefault())) == true
                            )
                                articlesArrayList.add(articlesdata!!)

                        }

                    }

                    articlesRecyclerview.adapter = followChannelAdapter(this@followChannel, articlesArrayList)
                    val itemCount = articlesArrayList.size
                    val database = FirebaseDatabase.getInstance()
                    val users = FirebaseAuth.getInstance().currentUser ?: return
                    val eemail = users.email
                    database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (ds in dataSnapshot.children) {
                                    val user = ds.getValue(userData::class.java)
                                    if (user != null) {
                                        ekey = user?.key.toString()
                                        dbref =
                                            FirebaseDatabase.getInstance().getReference("Follow Channel")
                                                .child(ekey).child(text)
                                        var n=0
                                        val usersRef =
                                            FirebaseDatabase.getInstance().getReference("Follow Channel")

                                        usersRef.addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                for (childSnapshot in dataSnapshot.children) {
                                                    // Lấy tên của nút
                                                    val key = childSnapshot.key
                                                    val usersRef1 =
                                                        FirebaseDatabase.getInstance().getReference("Follow Channel").child(key.toString())

                                                    usersRef1.addValueEventListener(object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            for (childSnapshot in dataSnapshot.children) {
                                                                // Lấy tên của nút
                                                                val name = childSnapshot.key
                                                                if (name != null) {
                                                                    if (name == text) {
                                                                        n++
                                                                    }
                                                                }
                                                            }
                                                            val tvtitle=findViewById<TextView>(R.id.tvtitle)
                                                            val tvname=text.replace(" ", "")
                                                            tvtitle.text="@$tvname  $n followres  $itemCount Articles"
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            // Xử lý khi có lỗi xảy ra
                                                            Log.w(TAG, "Failed to read value.", error.toException())
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
                                            val intent = Intent(this@followChannel, addingNew::class.java)
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
            startActivity(Intent(this, Follow::class.java))
            finish()
    }
}