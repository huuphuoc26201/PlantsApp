package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.MyFragmentAdapter
import com.example.myapplication.model.userData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class Profile : AppCompatActivity() {
    private lateinit var viewpager2:ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: MyFragmentAdapter
    private lateinit var builder : AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.person
        val image=findViewById<CircleImageView>(R.id.imageAvt)
        val tvname=findViewById<TextView>(R.id.tvname)
        val tvemail=findViewById<TextView>(R.id.tvemail)
        builder = AlertDialog.Builder(this)
        val edit=findViewById<ImageView>(R.id.edit)
        edit.setOnClickListener {
            val intent = Intent(this, Edit_Profile::class.java)
            startActivity(intent)
            finish()
        }
        val sigout=findViewById<LinearLayout>(R.id.signout)
        sigout.setOnClickListener {
            logout()
        }
        profile()


        viewpager2=findViewById(R.id.viewPager2)
        tabLayout=findViewById(R.id.tabLayout)
        adapter= MyFragmentAdapter(supportFragmentManager,lifecycle)
        tabLayout.addTab(tabLayout.newTab().setText("Species"))
        tabLayout.addTab(tabLayout.newTab().setText("Articles"))

        viewpager2.adapter=adapter
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
        builder.setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setCancelable(true) // dialog box in cancellable
            // set positive button
            //take two parameters dialogInterface and an int
            .setPositiveButton("Có"){dialogInterface,it ->
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, logIn::class.java)
                startActivity(intent)
                finish()
            }
        builder.setNegativeButton("Không") { dialog, which ->
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
        val name = users.displayName
        val eemail = users.email
        val photoUrl: Uri? = users.photoUrl
        tvname.text = name
        tvemail.text = eemail
        Glide.with(this@Profile).load(photoUrl).error(R.drawable.img_2)
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
                        Glide.with(this@Profile).load(user?.imageAvt).error(R.drawable.img_2)
                            .into(image)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

}