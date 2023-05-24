package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.articlesAdapter
import com.example.myapplication.Adapter.listSpeciesAdapter
import com.example.myapplication.model.articlesData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class articles : AppCompatActivity() {
    private lateinit var text: String
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<articlesData>
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        searchView = findViewById(R.id.searchView)
        userRecyclerview = findViewById(R.id.RecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)
        searchView.clearFocus()
        userArrayList = ArrayList()
        userArrayList = arrayListOf<articlesData>()
        getUserData()
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

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Articles")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val articlesdata = userSnapshot.getValue(articlesData::class.java)
                        userArrayList.add(articlesdata!!)

                    }

                    userRecyclerview.adapter = articlesAdapter(this@articles, userArrayList)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
    private fun searchList(text: String) {
        val searchList = java.util.ArrayList<articlesData>()
        for (articlesdata in userArrayList) {
            if (articlesdata.title?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true||articlesdata.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(articlesdata)
            }
        }
        userRecyclerview.adapter = articlesAdapter(this@articles,searchList)

    }
    fun prev(view: View?){
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}