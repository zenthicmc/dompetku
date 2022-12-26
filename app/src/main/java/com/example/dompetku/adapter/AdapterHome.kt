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
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

@SuppressLint("RecyclerView")

class AdapterHome(val context: Context, val homeList: ArrayList<DataHome>): RecyclerView.Adapter<AdapterHome.MyViewHolder>() {
    private lateinit var sessionManager : SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_transaksi_home, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        val currentItem = homeList[position]
        val decimalFormat = DecimalFormat("#,###")

        holder.txtType.text = currentItem.type
        holder.txtAmount.text = "Rp " + decimalFormat.format(currentItem.amount).toString()
        holder.txtDate.text = currentItem.date.substring(8,10) + "-" + currentItem.date.substring(5,7) + "-" + currentItem.date.substring(0,4)
        holder.txtStatus.text = currentItem.status

        // load icon
        Picasso.get()
            .load(currentItem.icon)
            .into(holder.icon)

        // check if status is success
        if(currentItem.status == "Success") {
            holder.txtStatus.setTextColor(context.resources.getColor(R.color.green2))
        } else if(currentItem.status == "Pending") {
            holder.txtStatus.setTextColor(context.resources.getColor(R.color.orange))
        } else {
            holder.txtStatus.setTextColor(context.resources.getColor(R.color.red))
        }
    }

    override fun getItemCount(): Int {
        return homeList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtType: TextView = itemView.findViewById(R.id.txtType)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }
}