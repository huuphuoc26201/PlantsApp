package com.example.myapplication.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.favoriteSpeciesAdapter
import com.example.myapplication.R
import com.example.myapplication.model.listSpeciesData
import com.google.firebase.database.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [speciesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class speciesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myRef: DatabaseReference
    private lateinit var userArrayList : ArrayList<listSpeciesData>
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_species, container, false)
        recyclerView = view.findViewById(R.id.RecyclerView)
        recyclerView.setHasFixedSize(true)
        userArrayList = ArrayList()
        userArrayList = arrayListOf<listSpeciesData>()
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Favorite Species")
    }

    override fun onStart() {
        super.onStart()
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val speciesdata = userSnapshot.getValue(listSpeciesData::class.java)
                        userArrayList.add(speciesdata!!)
                    }

                }

                recyclerView.adapter = favoriteSpeciesAdapter(speciesFragment, userArrayList)


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment speciesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            speciesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}