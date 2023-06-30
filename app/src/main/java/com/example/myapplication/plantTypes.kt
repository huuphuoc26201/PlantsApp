package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.listPlantsAdapter
import com.example.myapplication.Adapter.listSpeciesAdapter
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
import kotlin.collections.ArrayList
import android.Manifest;
import android.content.pm.PackageManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class plantTypes : AppCompatActivity() {
    private lateinit var text: String
    lateinit var name:String
    private lateinit var dbref : DatabaseReference
    private lateinit var plantTypesRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<listSpeciesData>
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planttypes)
        val namespecies=findViewById<TextView>(R.id.textView14)
        val enamespecies=findViewById<TextView>(R.id.textView15)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
        searchView = findViewById(R.id.searchView)
        val ename:String=intent.getStringExtra("Plants").toString()
        val formattedData = ename.toLowerCase().capitalize()
        namespecies.text = formattedData
        enamespecies.text = formattedData
        plantTypesRecyclerview = findViewById(R.id.RecyclerView)
        plantTypesRecyclerview.layoutManager = LinearLayoutManager(this)
        plantTypesRecyclerview.setHasFixedSize(true)
        searchView.clearFocus()
        userArrayList = ArrayList()
        userArrayList = arrayListOf<listSpeciesData>()

        val myadapter=  listPlantsAdapter(this@plantTypes, userArrayList)
        plantTypesRecyclerview.adapter = myadapter
        getPlantTypesData()

        //truy cập camera thực hiện tính năng addingnew
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
    }

    private fun getPlantTypesData() {
        text= intent.getStringExtra("Plants").toString()
        dbref = FirebaseDatabase.getInstance().getReference("ListSpecies")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val speciesdata = userSnapshot.getValue(listSpeciesData::class.java)
                        if (speciesdata != null) {
                           if (speciesdata.plants?.lowercase()
                                    ?.contains(text.lowercase(Locale.getDefault())) == true
                            )
                                userArrayList.add(speciesdata!!)
                        }
                    }
                    plantTypesRecyclerview.adapter = listPlantsAdapter(this@plantTypes, userArrayList)
                    val itemCount = userArrayList.size
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("Plants Types").child(text)
                    myRef.child("Number").setValue("$itemCount Types of Plants")

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
    private fun searchList(text: String) {
        val searchList = java.util.ArrayList<listSpeciesData>()
        for (speciesdata in userArrayList) {
            if (speciesdata.namespecies?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(speciesdata)
            }
        }
        plantTypesRecyclerview.adapter = listPlantsAdapter(this@plantTypes,searchList)

    }
    fun prev(view: View?){
        startActivity(Intent(this, Home::class.java))
        finish()
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
                                            val intent = Intent(this@plantTypes, addingNew::class.java)
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