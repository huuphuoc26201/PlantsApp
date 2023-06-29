package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
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
    lateinit var avt:String
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
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            name=user?.key.toString()
                            avt=user?.imageAvt.toString()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xảy ra lỗi trong quá trình đọc dữ liệu

            }
        })
        avatar.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        save.setOnClickListener {
            if (!validateemail()or !validateName()) {
            } else {
                builder.setTitle("Notification")
                    .setMessage("Are you sure you want to change??")
                    .setCancelable(true) // dialog box in cancellable
                    // set positive button
                    //take two parameters dialogInterface and an int
                    .setPositiveButton("Yes") { _, _ ->
                        firebaseUser()
                        val intent = Intent(this, Profile::class.java)
                        sfullName = editname.text.toString().trim()
                        intent.putExtra("name", sfullName)
                        startActivity(intent)
                        finish()
                    }
                builder.setNegativeButton("No") { dialog, _ ->
                    val myRef = database.getReference("Users").child(name)
                    myRef.child("imageAvt").setValue(avt)
                    Glide.with(this@Edit_Profile).load(avt).error(R.drawable.avt)
                        .into(avatar)
                    dialog.cancel()
                }
                    // show the builder
                    .show()
            }
        }



    }


    private fun validateemail(): Boolean {
        val mail= editemail.text.toString().trim()
        return if (mail.isEmpty()) {
            editemail.setError("Email can not be blank!")
            false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            editemail.setError("Email address is not valid!")
            false
        }
        else
        {
            editemail.setError(null)
            true
        }
    }

    private fun validateName(): Boolean {
        val sName= editname.text.toString().trim()
        return if (sName.isEmpty()) {
            editname.setError("Name can not be blank")
            false
        }
        else
        {
            editname.setError(null)
            true
        }
    }

    private fun firebaseUser() {
        sEmail = editemail.text.toString().trim()
        sfullName = editname.text.toString().trim()
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(userData::class.java)
                        if (user != null) {
                            name= user?.key.toString()
                            val database = FirebaseDatabase.getInstance()
                            val myRef = database.getReference("Users").child(name)
                            myRef.child("fullName").setValue(sfullName)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xảy ra lỗi trong quá trình đọc dữ liệu

            }
        })
    }

    private fun profile() {
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        val photoUrl: Uri? = users.photoUrl
        editemail.setText(eemail)
        Glide.with(this@Edit_Profile).load(photoUrl).error(R.drawable.avt)
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
                        Glide.with(this@Edit_Profile).load(user?.imageAvt).error(R.drawable.avt)
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

        val users = FirebaseAuth.getInstance().currentUser ?: return
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            // Load image to ImageView
            avatar.setImageURI(imageUri)
            val eemail = users.email
            database.getReference("Users").orderByChild("email").equalTo(eemail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (ds in dataSnapshot.children) {
                            val user = ds.getValue(userData::class.java)
                            if (user != null) {
                                name= user?.key.toString()
                                val storageRef = FirebaseStorage.getInstance().reference.child("imagesAvt").child(name).child("${UUID.randomUUID()}.jpg")
                                val uploadTask = imageUri?.let { storageRef.putFile(it) }

                                uploadTask?.continueWithTask { task ->
                                    if (!task.isSuccessful) {
                                        task.exception?.let { throw it }
                                    }

                                    storageRef.downloadUrl
                                }?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Lưu đường dẫn của hình ảnh vào database Realtime Database
                                        val downloadUri = task.result
                                        Glide.with(this@Edit_Profile).load(downloadUri.toString()).error(R.drawable.avt)
                                            .into(avatar)
                                        val database = FirebaseDatabase.getInstance()
                                        val myRef = database.getReference("Users").child(name)
                                        myRef.child("imageAvt").setValue(downloadUri.toString())
                                    } else {
                                        // Xử lý khi không thể tải lên hình ảnh
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


    fun changepassword(view: View?){
        val intent = Intent(this, changePasswword::class.java)
        startActivity(intent)
        finish()
    }
    fun prev(view: View?){
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
        finish()
    }
}