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


class followArticlesAdapter(userList1: Follow, private val userList: ArrayList<articlesData>) : RecyclerView.Adapter<followArticlesAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.dong_articles,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        Glide.with(holder.itemView.context)
            .load(currentitem.imageUrl)
            .into(holder.image)
        Glide.with(holder.itemView.context)
            .load(currentitem.imageAvt)
            .into(holder.imageAvt)
        holder.title.text=currentitem.title
        holder.name.text=currentitem.name
        holder.date.text=currentitem.date

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(holder.itemView.context, detailsArticles::class.java)
            intent.putExtra("imageUrl", currentitem.imageUrl)
            intent.putExtra("imageAvt", currentitem.imageAvt)
            intent.putExtra("title", currentitem.title)
            intent.putExtra("name", currentitem.name)
            intent.putExtra("date", currentitem.date)
            intent.putExtra("description", currentitem.description)
            intent.putExtra("id", currentitem.id)
            intent.putExtra("prev", "follow")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(intent)

        })
        var tim:Int=0
        var i:Int=0
        holder.like.setOnClickListener(View.OnClickListener {
            tim++
            if (tim % 2 === 0) {
                holder.like.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                holder.like.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        })
        holder.img.setOnClickListener(View.OnClickListener {
            i++
            if (i % 2 === 0) {
                holder.img.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
            } else {
                holder.img.setImageResource(R.drawable.ic_baseline_bookmark_24)
            }
        })

    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val image : ImageView = itemView.findViewById(R.id.image)
        val imageAvt : ImageView = itemView.findViewById(R.id.imageView)
        val title : TextView = itemView.findViewById(R.id.textView7)
        val name : TextView = itemView.findViewById(R.id.textView19)
        val date : TextView = itemView.findViewById(R.id.textView20)
        val like : ImageView = itemView.findViewById(R.id.imageView3)
        val img : ImageView = itemView.findViewById(R.id.imageView4)

    }

}