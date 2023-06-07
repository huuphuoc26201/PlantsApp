package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.listSpeciesAdapter
import com.example.myapplication.model.articlesData
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
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.model.followData

class detailsArticles : AppCompatActivity() {
    var tim:Int=0
    var n:Int=0
    var back=" "
    lateinit var name:String
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    lateinit var ekey:String
    lateinit var keyrandom:String
    private lateinit var text: String
    private lateinit var t: String
    private lateinit var t1: String
    private lateinit var t2: String
    private lateinit var t3: String
    private lateinit var t4: String
    private lateinit var t5: String

    private lateinit var t6: String
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

        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }
        database = FirebaseDatabase.getInstance()
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            ekey= user?.key.toString()
                            usersRef = FirebaseDatabase.getInstance().getReference("Favorite").child(ekey).child("FavoriteArticles")
                            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (childSnapshot in snapshot.children) {
                                        val childName = childSnapshot.child("name").getValue(String::class.java)
                                        val childTitle = childSnapshot.child("title").getValue(String::class.java)
                                        val childdes = childSnapshot.child("description").getValue(String::class.java)
                                        if (childName == text && childTitle==intent.getStringExtra("title") && childdes==intent.getStringExtra("description")) {
                                            thatym.visibility = View.GONE
                                        }
                                    }
                                }


                                override fun onCancelled(error: DatabaseError) {
                                    // Xử lý lỗi nếu có.
                                }
                            })
                            val usersRef1 =
                                FirebaseDatabase.getInstance().getReference("Follow Channel")
                                    .child(ekey)
                            usersRef1.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (childSnapshot in snapshot.children) {
                                        val childName = childSnapshot.child("name").getValue(String::class.java)
                                        if (childName == text) {
                                            val fl=findViewById<TextView>(R.id.textView22)
                                            fl.visibility = View.GONE
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

    fun unfollow(view: View?){
        val text: String? =intent.getStringExtra("name");
        val fl=findViewById<TextView>(R.id.textView22)
        val unfl=findViewById<TextView>(R.id.textView23)
        builder.setTitle("Notification")
            .setMessage("Are you sure you want to unfollow $text?")
            .setCancelable(true) // dialog box in cancellable
            // set positive button
            //take two parameters dialogInterface and an int
            .setPositiveButton("Unfollow"){ _, _ ->
                database = FirebaseDatabase.getInstance()
                val users = FirebaseAuth.getInstance().currentUser ?: return@setPositiveButton
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
                                    usersRef.child(intent.getStringExtra("name").toString()).removeValue()
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                unfl.visibility = View.GONE
                fl.visibility = View.VISIBLE
            }
        builder.setNegativeButton("No") { dialog, _ ->
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
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            ekey = user?.key.toString()
                            val usersRef1 =
                                FirebaseDatabase.getInstance().getReference("Favorite").child(ekey)
                                    .child("FavoriteArticles")
                            val articles = articlesData(t, t1, t2, t3, t4, t5,intent.getStringExtra("id").toString())
                            val thatym = findViewById<CircleImageView>(R.id.thatym)
                            tim++
                            if (tim % 2 == 0) {
                                usersRef1.child(intent.getStringExtra("id").toString()).removeValue()
                                thatym.setImageResource(R.drawable.icon_favorite_love)
                            } else {
                                val key = usersRef1.push().key
                                key?.let {
                                    val userRef = usersRef1.child(intent.getStringExtra("id").toString())
                                    userRef.setValue(articles)
                                    //usersRef1.child(intent.getStringExtra("id").toString()).child("name").setValue(intent.getStringExtra("id").toString())
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
    fun untym(view: View?){
        t = intent.getStringExtra("imageUrl").toString()
        t1 = intent.getStringExtra("imageAvt").toString()
        t2 = intent.getStringExtra("title").toString()
        t3 = intent.getStringExtra("name").toString()
        t4 = intent.getStringExtra("date").toString()
        t5 = intent.getStringExtra("description").toString()
        t6=intent.getStringExtra("id").toString()

        database = FirebaseDatabase.getInstance()
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            val ekey1 = user?.key.toString()
                            val usersref1 =
                                FirebaseDatabase.getInstance().getReference("Favorite").child(ekey1)
                                    .child("FavoriteArticles")
                            val articles = articlesData(t, t1, t2, t3, t4, t5,t6)
                            val unthatym = findViewById<CircleImageView>(R.id.unthatym)
                            n++
                            if (n % 2 != 0) {
                                usersref1.child(t6).removeValue()
                                unthatym.setImageResource(R.drawable.icon_favorite_love)
                            } else {
                                val key = usersref1.push().key
                                key?.let {
                                    val userRef = usersref1.child(t6)
                                    userRef.setValue(articles)
                                    //usersRef1.child(intent.getStringExtra("id").toString()).child("name").setValue(intent.getStringExtra("id").toString())
                                }
                                unthatym.setImageResource(R.drawable.icons_love)
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

    fun prev(view: View?){
        back = intent.getStringExtra("prev").toString()
        if(back=="articles"){
            startActivity(Intent(this, articles::class.java))
            finish()
        }
        else if(back=="myarticles"){
            startActivity(Intent(this, myArticles::class.java))
            finish()
        }
        else if(back=="follow"){
            startActivity(Intent(this, Follow::class.java))
            finish()
        }
        else if(back=="followchannel"){
            t1 = intent.getStringExtra("imageAvt").toString()
            t3 = intent.getStringExtra("name").toString()
            val intent = Intent(this@detailsArticles, followChannel::class.java)
            intent.putExtra("name", t3)
            intent.putExtra("imageAvt", t1)
            startActivity(intent)
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
                                            val intent = Intent(this@detailsArticles, addingNew::class.java)
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
    fun next(view: View?){
        t1 = intent.getStringExtra("imageAvt").toString()
        t3 = intent.getStringExtra("name").toString()
        val intent = Intent(this@detailsArticles, followChannel::class.java)
        intent.putExtra("name", t3)
        intent.putExtra("imageAvt", t1)
        intent.putExtra("prev", "prev")
        startActivity(intent)
        finish()
    }

}