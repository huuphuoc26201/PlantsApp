package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapplication.model.listSpeciesData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*
import android.Manifest


class details : AppCompatActivity() {
    var tim:Int=0
    var n:Int=0
    var back=" "
    private lateinit var name: String
    private lateinit var database: FirebaseDatabase
    private lateinit var text: String
    private lateinit var ename: String
    private lateinit var edescription: String
    private lateinit var eevaluate: String
    private lateinit var image: String
    private lateinit var usersRef: DatabaseReference
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

        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            name= user?.key.toString()
                            usersRef = FirebaseDatabase.getInstance().getReference("Favorite").child(name).child("FavoriteSpecies")
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
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xảy ra lỗi trong quá trình đọc dữ liệu

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

        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            name= user?.key.toString()
                            val usersRef1 = FirebaseDatabase.getInstance().getReference("Favorite").child(name).child("FavoriteSpecies")
                            val Species= listSpeciesData(image,ename, eevaluate,edescription)
                            val thatym=findViewById<CircleImageView>(R.id.thatym)
                            tim++
                            if (tim % 2 == 0) {
                                usersRef1.child(ename).removeValue()
                                thatym.setImageResource(R.drawable.icon_favorite_love)
                            } else {

                                val key = usersRef1.push().key
                                key?.let {
                                    val userRef = usersRef1.child(ename)
                                    userRef.setValue(Species)
                                }
                                thatym.setImageResource(R.drawable.icons_love)
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
    fun untym(view: View?) {
        ename= intent.getStringExtra("namespecies").toString()
        eevaluate= intent.getStringExtra("evaluate").toString()
        edescription= intent.getStringExtra("description").toString()
        image = intent.getStringExtra("imageUrl").toString()
        val Species= listSpeciesData(image,ename, eevaluate,edescription)
        val unthatym=findViewById<CircleImageView>(R.id.unthatym)

        n++
        if (n % 2 != 0) {
            val database = Firebase.database.reference
            usersRef.child(ename).removeValue()
            unthatym.setImageResource(R.drawable.icon_favorite_love)
        } else {

            val key = usersRef.push().key
            key?.let {
                val userRef = usersRef.child(ename)
                userRef.setValue(Species)
            }
            unthatym.setImageResource(R.drawable.icons_love)
        }
    }

    fun prev(view: View?){
        back = intent.getStringExtra("prev").toString()
        if(back=="species"){
            startActivity(Intent(this, species::class.java))
            finish()
        }else if(back=="myspeciies"){
            startActivity(Intent(this, myArticles::class.java))
            finish()
        }
        else{
            startActivity(Intent(this, Profile::class.java))
            finish()
        }

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
                                            val intent = Intent(this@details, addingNew::class.java)
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
}

