package com.example.dompetku.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dompetku.R
import com.example.dompetku.SessionManager
import com.example.dompetku.dataclass.DataNotifikasi

@SuppressLint("RecyclerView")

class AdapterNotifikasi(val context: Context, val notifikasiList: ArrayList<DataNotifikasi>): RecyclerView.Adapter<AdapterNotifikasi.MyViewHolder>() {
    private lateinit var sessionManager : SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_notifikasi, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        val currentItem = notifikasiList[position]

        // format date to dd-mm-yyyy HH:mm
        val date = currentItem.date.substring(8,10) + "-" + currentItem.date.substring(5,7) + "-" + currentItem.date.substring(0,4) + " " + currentItem.date.substring(11,16)

        holder.txtTitle.text = currentItem.title
        holder.txtDesc.text = currentItem.desc
        holder.txtDate.text = date

    }

    override fun getItemCount(): Int {
        return notifikasiList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtDesc: TextView = itemView.findViewById(R.id.txtDesc)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
    }
}