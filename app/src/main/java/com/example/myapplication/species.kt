package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.speciesAdapter
import com.example.myapplication.model.speciesData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class species : AppCompatActivity() {
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<speciesData>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_species)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
        userRecyclerview = findViewById(R.id.RecyclerView)
        searchView = findViewById(R.id.searchView)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)
        searchView.clearFocus()
        userArrayList = ArrayList()
        userArrayList = arrayListOf<speciesData>()
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

        dbref = FirebaseDatabase.getInstance().getReference("Species")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val speciesdata = userSnapshot.getValue(speciesData::class.java)
                        userArrayList.add(speciesdata!!)

                    }
                    userRecyclerview.adapter = speciesAdapter(this@species, userArrayList)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
    private fun searchList(text: String) {
        val searchList = java.util.ArrayList<speciesData>()
        for (speciesdata in userArrayList) {
            if (speciesdata.species?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(speciesdata)
            }
        }
        userRecyclerview.adapter = speciesAdapter(this@species, searchList)

    }
    fun prev(view: View?){
        startActivity(Intent(this, Home::class.java))
        finish()
    }
}