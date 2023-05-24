package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.ImageAdapter
import com.example.myapplication.model.ImageData
import com.google.firebase.storage.FirebaseStorage
import com.example.myapplication.RecyclerItemClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class addingNew : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addingnew)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
        storage = FirebaseStorage.getInstance()

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

        // Get list of images from Firebase Storage
        val imagesRef = storage.reference.child("images")
        imagesRef.listAll().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imagesList = mutableListOf<ImageData>()
                for (item in task.result?.items!!) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val image = ImageData(imageUrl)
                        imagesList.add(image)

                        // Set up RecyclerView with ImageAdapter
                        val adapter = ImageAdapter(imagesList)
                        recyclerView = findViewById(R.id.RecyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
                        recyclerView.setHasFixedSize(true)
                        recyclerView.adapter = adapter

                        // Add long click listener to RecyclerView items
                        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this, recyclerView,
                            object : RecyclerItemClickListener.OnItemClickListener {
                                override fun onItemLongClick(view: View?, position: Int) {
                                    // Show dialog to confirm deleting the selected image
                                    val builder = AlertDialog.Builder(this@addingNew)
                                    builder.setTitle("Xác nhận xóa")
                                        .setMessage("Bạn chắc chắn muốn xóa ảnh này?")
                                        .setCancelable(true) // dialog box in cancellable
                                        // set positive button
                                        //take two parameters dialogInterface and an int
                                        .setPositiveButton("Có"){dialogInterface,it ->
                                            val selectedImage = imagesList[position]
                                            val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(selectedImage.url)
                                            imageRef.delete()
                                                .addOnSuccessListener {
                                                    // If deleting the image is successful, remove the corresponding item from RecyclerView and update it
                                                    imagesList.removeAt(position)
                                                    adapter.notifyItemRemoved(position)
                                                    Toast.makeText(this@addingNew, "Đã xóa ảnh", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener {
                                                    // If deleting the image fails, show error message
                                                    Toast.makeText(this@addingNew, "Lỗi khi xóa ảnh", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    builder.setNegativeButton("Không") { dialog, which ->
                                        dialog.cancel()
                                    }
                                        // show the builder
                                        .show()
                                }

                                override fun onItemClick(view: View?, position: Int) {
                                    // Handle click event on RecyclerView items
                                }
                            }))
                    }
                }
            }
        }
    }


    fun prev(view: View?){
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}
