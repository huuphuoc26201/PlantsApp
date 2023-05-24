package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.listSpeciesAdapter
import com.example.myapplication.model.listSpeciesData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class listSpecies : AppCompatActivity() {
    private lateinit var text: String
    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<listSpeciesData>
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_species)
        val namespecies=findViewById<TextView>(R.id.textView14)
        val enamespecies=findViewById<TextView>(R.id.textView15)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        searchView = findViewById(R.id.searchView)
        val ename:String=intent.getStringExtra("species").toString()
        val formattedData = ename.toLowerCase().capitalize()
        namespecies.text = formattedData
        enamespecies.text = formattedData
        userRecyclerview = findViewById(R.id.RecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)
        searchView.clearFocus()
        userArrayList = ArrayList()
        userArrayList = arrayListOf<listSpeciesData>()
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
        text= intent.getStringExtra("species").toString()
        dbref = FirebaseDatabase.getInstance().getReference("ListSpecies")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val speciesdata = userSnapshot.getValue(listSpeciesData::class.java)
                        if (speciesdata != null) {
                            if (speciesdata.namespecies?.lowercase()
                                    ?.contains(text.lowercase(Locale.getDefault())) == true
                            )
                                userArrayList.add(speciesdata!!)
                        }

                    }

                    userRecyclerview.adapter = listSpeciesAdapter(this@listSpecies, userArrayList)


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
        userRecyclerview.adapter = listSpeciesAdapter(this@listSpecies,searchList)

    }
    fun prev(view: View?){
        startActivity(Intent(this, species::class.java))
        finish()
    }
}