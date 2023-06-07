package com.example.myapplication.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.details
import com.example.myapplication.listSpecies
import com.example.myapplication.model.listSpeciesData


class listSpeciesAdapter(userList1: listSpecies, private val userList: ArrayList<listSpeciesData>) : RecyclerView.Adapter<listSpeciesAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.dong_species,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        Glide.with(holder.itemView.context)
            .load(currentitem.imageUrl)
            .into(holder.image)
        holder.name.text=currentitem.namespecies
        holder.description.text=currentitem.description

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, details::class.java)
            intent.putExtra("imageUrl", currentitem.imageUrl)
            intent.putExtra("namespecies", currentitem.namespecies)
            intent.putExtra("evaluate", currentitem.evaluate)
            intent.putExtra("description", currentitem.description)
            intent.putExtra("prev", "species")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(intent)

        })

    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val image : ImageView = itemView.findViewById(R.id.imageView2)
        val name : TextView = itemView.findViewById(R.id.textView7)
        val description : TextView = itemView.findViewById(R.id.textView13)

    }

}