package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.articlesAdapter
import com.example.myapplication.Adapter.phoToAdapter
import com.example.myapplication.Adapter.plantTypeAdapter
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.phoToData
import com.example.myapplication.model.plantTypeData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*


class Home : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var Recyclerview1 : RecyclerView
    private lateinit var arrayList : ArrayList<plantTypeData>
    private lateinit var arrayList1 : ArrayList<phoToData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val specie= findViewById<Button>(R.id.specise)
        specie.setOnClickListener{
            startActivity(Intent(this, species::class.java))
            finish()
        }
        val article= findViewById<Button>(R.id.articles)
        article.setOnClickListener{
            startActivity(Intent(this, articles::class.java))
            finish()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    true
                }
                R.id.person -> {
                    startActivity(Intent(this, details::class.java))
                    true
                }
                else -> false
            }
        }

        userRecyclerview = findViewById(R.id.RecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        userRecyclerview.setHasFixedSize(true)
        arrayList = ArrayList()
        arrayList = arrayListOf<plantTypeData>()
        getUserData()

        Recyclerview1 = findViewById(R.id.RecyclerView1)
        Recyclerview1.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        Recyclerview1.setHasFixedSize(true)
        arrayList1 = ArrayList()
        arrayList1 = arrayListOf<phoToData>()
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

                    userRecyclerview.adapter = plantTypeAdapter(this@Home, arrayList)


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

                    Recyclerview1.adapter = phoToAdapter(this@Home, arrayList1)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

}