package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.myapplication.model.userData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class Edit_Profile : AppCompatActivity() {
    private lateinit var sEmail: String
    private lateinit var sfullName: String
    private lateinit var name: String
    lateinit var avatar:CircleImageView
    lateinit var editname:EditText
    lateinit var editemail:EditText
    lateinit var save:Button
    private lateinit var database: FirebaseDatabase
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        builder = AlertDialog.Builder(this)
        database = FirebaseDatabase.getInstance()
        avatar=findViewById(R.id.imageAvt)
        editemail=findViewById(R.id.email)
        editname=findViewById(R.id.name)
        save=findViewById(R.id.save)
        profile()

        save.setOnClickListener {
            firebaseUser()
            builder.setTitle("Thông báo")
                .setMessage("Bạn có chắc chắn muốn thay đổi?")
                .setCancelable(true) // dialog box in cancellable
                // set positive button
                //take two parameters dialogInterface and an int
                .setPositiveButton("Có"){dialogInterface,it ->
                    profile()
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    finish()
                }
            builder.setNegativeButton("Không") { dialog, which ->
                dialog.cancel()
            }
                // show the builder
                .show()
        }



        avatar.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }




    }

    private fun firebaseUser() {
        sEmail = editemail.text.toString().trim()
        sfullName = editname.text.toString().trim()
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    if (user != null) {
                        name= user?.key.toString()
                        val databaseRef = FirebaseDatabase.getInstance().reference.child("imagesAvt")
                        databaseRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val imageUrl = snapshot.children.lastOrNull()?.value as? String

                                val User=userData(sfullName,sEmail,imageUrl,name)
                                val ref = database.getReference("Users")
                                val key = ref.push().key
                                key?.let {
                                    val userRef = ref.child(name)
                                    userRef.setValue(User)

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Xử lý khi không thể truy xuất database
                            }
                        })


                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun profile() {
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val ename = users.displayName
        val eemail = users.email
        val photoUrl: Uri? = users.photoUrl
        editname.setText(ename)
        editemail.setText(eemail)
        Glide.with(this@Edit_Profile).load(photoUrl).error(R.drawable.img_2)
            .into(avatar)
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    if (user != null) {
                        editname.setText(user?.fullName)
                        editemail.setText(user?.email)
                        Glide.with(this@Edit_Profile).load(user?.imageAvt).error(R.drawable.img_2)
                            .into(avatar)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            // Load image to ImageView
            avatar.setImageURI(imageUri)

            val storageRef = FirebaseStorage.getInstance().reference.child("imagesAvt/${UUID.randomUUID()}")
            val uploadTask = imageUri?.let { storageRef.putFile(it) }

            if (uploadTask != null) {
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }

                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        // Lưu đường dẫn của hình ảnh vào database Realtime Database
                        val databaseRef = FirebaseDatabase.getInstance().reference.child("imagesAvt")
                        databaseRef.push().setValue(downloadUri.toString())
                    } else {
                        // Xử lý khi không thể tải lên hình ảnh
                    }
                }
            }
        }
    }


}