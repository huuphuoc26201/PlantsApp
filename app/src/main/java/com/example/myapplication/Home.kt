package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.phoToAdapter
import com.example.myapplication.Adapter.plantTypeAdapter
import com.example.myapplication.model.phoToData
import com.example.myapplication.model.plantTypeData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.ByteArrayOutputStream
import java.util.*


class Home : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    lateinit var image:String
    lateinit var name:String
    lateinit var search:SearchView
    private lateinit var plantsTypeRecyclerview : RecyclerView
    private lateinit var phoToRecyclerview : RecyclerView
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


        search=findViewById(R.id.searchView)
        val nav_button= findViewById<CoordinatorLayout>(R.id.CoordinatorLayout)

        // Đăng ký listener để theo dõi sự kiện hiển thị/ẩn đi bàn phím
        KeyboardVisibilityEvent.setEventListener(this
        ) { isOpen ->
            if (isOpen) {
                // Nếu bàn phím hiển thị, ẩn đi Bottom Navigation
                nav_button.visibility = View.GONE
            } else {
                // Ngược lại, hiển thị Bottom Navigation
                nav_button.visibility = View.VISIBLE
            }
        }
        // trong hàm onCreate() của Activity đầu tiên
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Tạo intent mới để chuyển sang Activity mới
                val intent = Intent(this@Home, listSpecies::class.java)

                // Đính kèm dữ liệu vào intent
                intent.putExtra("SEARCH_QUERY", query)

                // Chuyển sang Activity mới
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Không làm gì trong trường hợp này
                return true
            }
        })

        // truy cập camera
        addingnew.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }
        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }


        profile()

        //Chuyển tới màn hình profile
        btnprofile.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            finish()

        }
        //Chuyển tới màn hình specie
        specie.setOnClickListener{
            startActivity(Intent(this, species::class.java))
            finish()
        }

        //Chuyển tới màn hình article
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

        plantsTypeRecyclerview = findViewById(R.id.RecyclerView)
        plantsTypeRecyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        plantsTypeRecyclerview.setHasFixedSize(true)
        arrayList = ArrayList()
        arrayList = arrayListOf<plantTypeData>()
        val myadapter=plantTypeAdapter(this@Home, arrayList)
        plantsTypeRecyclerview.adapter = myadapter
        getUserData()

        phoToRecyclerview = findViewById(R.id.RecyclerView1)
        phoToRecyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        phoToRecyclerview.setHasFixedSize(true)
        arrayList1 = ArrayList()
        arrayList1 = arrayListOf<phoToData>()
        val myadapter1=phoToAdapter(this@Home, arrayList1)
        phoToRecyclerview.adapter = myadapter1
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
                    plantsTypeRecyclerview.adapter = plantTypeAdapter(this@Home, arrayList)


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

                    phoToRecyclerview.adapter = phoToAdapter(this@Home, arrayList1)


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
        val eemail = users.email
        val photoUrl: Uri? = users.photoUrl

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
                        Glide.with(this@Home).load(user?.imageAvt.toString()).error(R.drawable.avt)
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
                                            val intent = Intent(this@Home, addingNew::class.java)
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

    override fun onBackPressed() {
        AlertDialog.Builder(this@Home).apply {
            setTitle("Exit the app")
            setMessage("Are you sure you want to exit the application?")
            setPositiveButton("Yes") { _, _ ->
                finish()
            }
            setNegativeButton("No", null)
        }.show()
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