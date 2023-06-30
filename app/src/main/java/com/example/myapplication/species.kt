@file:Suppress("UNCHECKED_CAST")

package com.example.myapplication

import `in`.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView
import android.Manifest.permission_group.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.RecyclerViewAdapter
import com.example.myapplication.Adapter.speciesAdapter
import com.example.myapplication.model.speciesData
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*
import android.Manifest
import androidx.coordinatorlayout.widget.CoordinatorLayout
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class species : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    lateinit var name:String

    private lateinit var Recyclerview: IndexFastScrollRecyclerView
    private lateinit var speciesArrayList : ArrayList<speciesData>
    private lateinit var searchView: SearchView

    //private val mData by lazy {DataHelper.getAlphabetFullData() }
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_species)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
        Recyclerview = findViewById(R.id.RecyclerView)
        searchView = findViewById(R.id.searchView)
        Recyclerview.layoutManager = LinearLayoutManager(this)
        Recyclerview.setHasFixedSize(true)
        searchView.clearFocus()
        speciesArrayList = ArrayList()
        speciesArrayList = arrayListOf()
        val myadapter=  speciesAdapter(this@species, speciesArrayList)
        Recyclerview.adapter = myadapter
        getUserData()
        initialiseUI()
        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }

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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
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
    }

    private fun getUserData() {

        dbref = FirebaseDatabase.getInstance().getReference("Species")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val speciesData = userSnapshot.getValue(speciesData::class.java)
                        speciesArrayList.add(speciesData!!)

                    }
                    initialiseUI()
                    Recyclerview.adapter = speciesAdapter(this@species, speciesArrayList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    private fun searchList(text: String) {
        val searchList = ArrayList<speciesData>()
        for (speciesData in speciesArrayList) {
            if (speciesData.species?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(speciesData)
            }
        }

        val adapter = speciesAdapter(this@species, searchList)
        Recyclerview.adapter = adapter
    }
    private fun initialiseUI() {
        dbref = FirebaseDatabase.getInstance().getReference("Species")
        Recyclerview.apply {
            Recyclerview.layoutManager = LinearLayoutManager(this@species)
            adapter = RecyclerViewAdapter(speciesArrayList.map { it.species } as ArrayList<String>)

            setIndexbarMargin(-4F)
            setIndexTextSize(12)
            setIndexBarColor("#FFFFFFFF")
            setIndexBarCornerRadius(0)
            setIndexBarTransparentValue(0.4.toFloat())
            setPreviewPadding(0)
            setIndexBarTextColor("#6A6F7D")
            setPreviewTextSize(60)
            setPreviewColor("#FFFFFFFF")
            setPreviewTextColor("#2DDA93")
            setPreviewTransparentValue(0.6f)
            setIndexBarVisibility(true)
            setIndexBarStrokeVisibility(true)
            setIndexBarStrokeWidth(1)
            setIndexBarStrokeColor("#FFFFFFFF")
            setIndexBarHighLightTextVisibility(true)
        }
        Objects.requireNonNull<RecyclerView.LayoutManager>(Recyclerview.layoutManager)
            .scrollToPosition(0)
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
                                            val intent = Intent(this@species, addingNew::class.java)
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
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}