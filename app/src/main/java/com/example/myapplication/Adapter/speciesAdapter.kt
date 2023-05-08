package com.example.myapplication.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.listSpecies
import com.example.myapplication.model.speciesData
import com.example.myapplication.species

class speciesAdapter(userList1: species, private var userList: ArrayList<speciesData>) : RecyclerView.Adapter<speciesAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_species,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        holder.Species.text = currentitem.species

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, listSpecies::class.java)
            intent.putExtra("species", currentitem.species)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(intent)

        })

    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val Species : TextView = itemView.findViewById(R.id.Species)

    }

}