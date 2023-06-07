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
import com.example.myapplication.model.followData


class followAdapter(userList1: Follow, private val userList: ArrayList<followData>) : RecyclerView.Adapter<followAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_follow,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]
        Glide.with(holder.itemView.context)
            .load(currentitem.imageAvt)
            .into(holder.imageAvt)
        holder.name.text=currentitem.name

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, followChannel::class.java)
            intent.putExtra("imageAvt", currentitem.imageAvt)
            intent.putExtra("name", currentitem.name)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(intent)

        })

    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val imageAvt : ImageView = itemView.findViewById(R.id.imageView)
        val name : TextView = itemView.findViewById(R.id.tvname)


    }

}