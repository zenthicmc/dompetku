package com.example.dompetku.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dompetku.R
import com.example.dompetku.SessionManager
import com.example.dompetku.dataclass.DataHome
import com.example.dompetku.dataclass.DataRiwayat
import com.example.dompetku.dataclass.DataTopup
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

@SuppressLint("RecyclerView")

class AdapterTopup2(val context: Context, val topupList: ArrayList<DataTopup>): RecyclerView.Adapter<AdapterTopup2.MyViewHolder>() {
    private lateinit var sessionManager : SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_topup, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        val currentItem = topupList[position]

        holder.productName.text = currentItem.name

        // load icon
        Picasso.get()
            .load(currentItem.icon)
            .into(holder.productImg)

    }

    override fun getItemCount(): Int {
        return topupList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val productImg: ImageView = itemView.findViewById(R.id.productImg)
        val productName: TextView = itemView.findViewById(R.id.productName)
    }
}