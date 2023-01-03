package com.example.dompetku.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dompetku.R
import com.example.dompetku.SessionManager
import com.example.dompetku.dataclass.DataMetode
import com.squareup.picasso.Picasso


@SuppressLint("RecyclerView")

class AdapterMetode(val context: Context, val metodeList: ArrayList<DataMetode>): RecyclerView.Adapter<AdapterMetode.MyViewHolder>() {
    private lateinit var sessionManager : SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_metode, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        val currentItem = metodeList[position]

        holder.txtName.text = currentItem.name

        // on click
        holder.itemView.setOnClickListener {
            val code = currentItem.code
            val name = currentItem.name
            val icon = currentItem.icon

            sessionManager.setMetode(code, name, icon)
            (context as Activity).finish()
        }

        // load icon
        Picasso.get()
            .load(currentItem.icon)
            .into(holder.icon)
    }

    override fun getItemCount(): Int {
        return metodeList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }
}