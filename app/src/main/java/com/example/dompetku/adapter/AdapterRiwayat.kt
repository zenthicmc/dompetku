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
import com.example.dompetku.*
import com.example.dompetku.dataclass.DataHome
import com.example.dompetku.dataclass.DataRiwayat
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

@SuppressLint("RecyclerView")

class AdapterRiwayat(val context: Context, val riwayatList: ArrayList<DataRiwayat>): RecyclerView.Adapter<AdapterRiwayat.MyViewHolder>() {
    private lateinit var sessionManager : SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_riwayat_transaksi, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        val currentItem = riwayatList[position]
        val decimalFormat = DecimalFormat("#,###")

        // format date to dd-mm-yyyy HH:mm
        val date = currentItem.date.substring(8,10) + "-" + currentItem.date.substring(5,7) + "-" + currentItem.date.substring(0,4) + " " + currentItem.date.substring(11,16)

        holder.txtType.text = currentItem.type
        holder.txtAmount.text = "Rp " + decimalFormat.format(currentItem.amount).toString()
        holder.txtDate.text = date
        holder.txtStatus.text = currentItem.status

        // load icon
        Picasso.get()
            .load(currentItem.icon)
            .into(holder.icon)

        // check if status is success
        when(currentItem.status) {
            "Success" -> holder.txtStatus.setTextColor(context.resources.getColor(R.color.green2))
            "Pending" -> holder.txtStatus.setTextColor(context.resources.getColor(R.color.orange))
            else -> holder.txtStatus.setTextColor(context.resources.getColor(R.color.red))
        }

        holder.itemView.setOnClickListener {
            if(currentItem.type == "Deposit") {
                val intent = Intent(context, CheckOutActivity::class.java)
                intent.putExtra("id", currentItem.id)
                startActivity(context, intent, null)
            }
            else if(currentItem.type == "Transfer") {
                val intent = Intent(context, DetailTransferActivity::class.java)
                intent.putExtra("id", currentItem.id)
                startActivity(context, intent, null)
            }
            else if(currentItem.type == "Withdraw") {
                val intent = Intent(context, DetailWithdrawActivity::class.java)
                intent.putExtra("id", currentItem.id)
                startActivity(context, intent, null)
            }
            else if(currentItem.type == "Topup") {
                val intent = Intent(context, DetailTopupActivity::class.java)
                intent.putExtra("id", currentItem.id)
                startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return riwayatList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val txtType: TextView = itemView.findViewById(R.id.txtType)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }
}