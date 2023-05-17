package com.example.myapplication.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.*
import com.example.myapplication.model.articlesData
import com.example.myapplication.model.phoToData


class phoToAdapter(userList1: Home, private val userList: ArrayList<phoToData>) : RecyclerView.Adapter<phoToAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.dong_photo,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        Glide.with(holder.itemView.context)
            .load(currentitem.imageUrl)
            .into(holder.image)
        holder.number.text=currentitem.Characteristic


        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, detailsArticles::class.java)
            intent.putExtra("imageUrl", currentitem.imageUrl)
            intent.putExtra("Characteristic", currentitem.Characteristic)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(intent)

        })

    }

    override fun getItemCount(): Int {

        return userList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val image : ImageView = itemView.findViewById(R.id.image)
        val number : TextView = itemView.findViewById(R.id.textView)

    }

}