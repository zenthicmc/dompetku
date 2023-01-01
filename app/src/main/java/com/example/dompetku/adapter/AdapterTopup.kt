package com.example.dompetku.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dompetku.R
import com.example.dompetku.SessionManager
import com.example.dompetku.TopupActivity2
import com.example.dompetku.dataclass.DataHome
import com.example.dompetku.dataclass.DataRiwayat
import com.example.dompetku.dataclass.DataTopup
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

@SuppressLint("RecyclerView")

class AdapterTopup(val context: Context, val topupList: ArrayList<DataTopup>): RecyclerView.Adapter<AdapterTopup.MyViewHolder>() {
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

        holder.itemView.setOnClickListener {
            val intent = Intent(context, TopupActivity2::class.java)
            intent.putExtra("code", currentItem.code)
            intent.putExtra("name", currentItem.name)
            intent.putExtra("type", currentItem.type)
            intent.putExtra("icon", currentItem.icon)

            startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return topupList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val productImg: ImageView = itemView.findViewById(R.id.productImg)
        val productName: TextView = itemView.findViewById(R.id.productName)
    }
}