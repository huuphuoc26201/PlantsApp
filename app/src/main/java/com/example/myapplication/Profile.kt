package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.MyFragmentAdapter
import com.example.myapplication.model.userData
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*

class Profile : AppCompatActivity() {
    lateinit var name:String
    private lateinit var viewpager2:ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: MyFragmentAdapter
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profile()
        val name = intent.getStringExtra("name").toString()
        if(name!=null){
            val fullname=findViewById<TextView>(R.id.tvname)
            fullname.text=name
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.person
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                R.id.person -> {
                    true
                }
                else -> false
            }
        }

        builder = AlertDialog.Builder(this)
        val edit=findViewById<ImageView>(R.id.edit)

        //truy cập camera thực hiện tính năng adding new
        val fab=findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 1)
            }
        }

        //chuyển tới màn hình edit profile
        edit.setOnClickListener {
            val intent = Intent(this, Edit_Profile::class.java)
            startActivity(intent)
            finish()
        }
        //chuyển tới màn hình addingnew
        val addingnew=findViewById<LinearLayout>(R.id.addingnew)
        addingnew.setOnClickListener {
            val intent = Intent(this, addingNew::class.java)
            startActivity(intent)
            finish()
        }
        //đăng xuất khỏi tài khoản
        val sigout=findViewById<LinearLayout>(R.id.signout)
        sigout.setOnClickListener {
            logout()
        }

        viewpager2=findViewById(R.id.viewPager2)
        tabLayout=findViewById(R.id.tabLayout)
        // Thay đổi chiều cao của tab indicator
        tabLayout.setSelectedTabIndicatorHeight(1)

        adapter= MyFragmentAdapter(supportFragmentManager,lifecycle)
        tabLayout.addTab(tabLayout.newTab().setText("Species"))
        tabLayout.addTab(tabLayout.newTab().setText("Articles"))

        viewpager2.adapter=adapter

        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewpager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        viewpager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
        }

    private fun logout() {
        builder.setTitle("Log out")
            .setMessage("Are you sure you want to sign out?")
            .setCancelable(true)
            // dialog box in cancellable
            // set positive button
            //take two parameters dialogInterface and an int
            .setPositiveButton("Yes"){ _, _ ->
                val users = FirebaseAuth.getInstance().currentUser ?: return@setPositiveButton
                // Check if the user is using a Google account
                if (users.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
                    signOut()
                   return@setPositiveButton
                }
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, logIn::class.java)
                startActivity(intent)
                finish()
            }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }
            // show the builder
            .show()
    }

    private fun profile() {
        val image=findViewById<CircleImageView>(R.id.imageAvt)
        val tvname=findViewById<TextView>(R.id.tvname)
        val tvemail=findViewById<TextView>(R.id.tvemail)
        val users = FirebaseAuth.getInstance().currentUser ?: return
        val eemail = users.email
        val photoUrl: Uri? = users.photoUrl
        tvemail.text = eemail
        Glide.with(this@Profile).load(photoUrl).error(R.drawable.avt)
            .into(image)
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("email")
            .equalTo(eemail)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val user = ds.getValue(userData::class.java)
                    if (user != null) {
                        tvname.text = user?.fullName
                        tvemail.text = user?.email
                        Glide.with(this@Profile).load(user?.imageAvt).error(R.drawable.avt)
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
                                            val intent = Intent(this@Profile, addingNew::class.java)
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

    // Phương thức để đăng xuất
    private fun signOut() {
        var mGoogleApiClient: GoogleApiClient? = null
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        mGoogleApiClient?.connect()

        mGoogleApiClient?.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { status ->
                    if (status.isSuccess) {
                        // Đăng xuất thành công
                        Logout()
                    }
                }
            }

            override fun onConnectionSuspended(i: Int) {}
        })
    }

    private fun Logout() {
        val intent = Intent(this, logIn::class.java)
        startActivity(intent)
        finish()
    }

    fun changepassword(view: View?){
        startActivity(Intent(this, changePasswword::class.java))
        finish()
    }
    fun myArticle(view: View?){
        startActivity(Intent(this, myArticles::class.java))
        finish()
    }
    fun follow(view: View?){
        startActivity(Intent(this, Follow::class.java))
        finish()
    }

}

