package com.example.dompetku.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dompetku.R
import com.example.dompetku.SessionManager
import com.example.dompetku.TopupActivity
import com.example.dompetku.dataclass.DataMenu

@SuppressLint("RecyclerView")

class AdapterMenu(val context: Context, val menuList: ArrayList<DataMenu>): RecyclerView.Adapter<AdapterMenu.MyViewHolder>() {
    private lateinit var sessionManager : SessionManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_menu, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        val currentItem = menuList[position]

        holder.txtTitle.text = currentItem.title
        holder.image.setImageResource(currentItem.image)
        holder.background.setBackgroundResource(currentItem.background)

        holder.itemView.setOnClickListener {
            when(currentItem.type) {
                "listrik" -> {
                    topupTagihan("listrik")
                }
                "internet" -> {
                    topupTagihan("pulsa")
                }
                "game" -> {
                    topupTagihan("game")
                }
                "voucher" -> {
                    topupTagihan("voucher")
                }
                "emoney" -> {
                    topupTagihan("emoney")
                }
                "pulsa" -> {
                    topupTagihan("pulsa")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val background: LinearLayout = itemView.findViewById(R.id.background)
    }

    private fun topupTagihan(category: String) {
        val intent = Intent(context, TopupActivity::class.java)
        intent.putExtra("category", category)
        ContextCompat.startActivity(context, intent, null)
    }
}