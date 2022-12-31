package com.example.dompetku

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.adapter.AdapterTopup2
import com.example.dompetku.dataclass.DataTopup
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat

class TopupActivity2 : AppCompatActivity() {
    private lateinit var image: ImageView
    private lateinit var txtName: TextView
    private lateinit var btnBack: ImageView
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataTopup : ArrayList<DataTopup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topup2)

        recyclerView = findViewById(R.id.recyclerTopup)
        dataTopup = ArrayList<DataTopup>()
        image = findViewById(R.id.image)
        txtName = findViewById(R.id.txtName)
        sessionManager = SessionManager(this)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        val name = intent.getStringExtra("name")
        val icon = intent.getStringExtra("icon")
        val type = intent.getStringExtra("type").toString()
        val code = intent.getStringExtra("code").toString()

        txtName.text = name
        Picasso.get().load(icon).into(image)

        requestPrice(this, type, code, icon.toString())

    }

    private fun requestPrice(context: Context, type: String, code: String, icon: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/product/$type/$code")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("pricing")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())

                    val data: JSONArray = response.getJSONArray("data")
                    val decimalFormat = DecimalFormat("#,###")

                    if(response.getString("success").equals("true")) {
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            val nominal = "Rp " + decimalFormat.format(item.getInt("product_price")).toString()
                            dataTopup.add(
                                DataTopup(
                                    item.getString("product_code"),
                                    item.getString("product_nominal") + " ($nominal)" ,
                                    item.getString("product_type"),
                                    icon
                                )
                            )

                            recyclerView.layoutManager = LinearLayoutManager(context)
                            recyclerView.adapter = AdapterTopup2(context, dataTopup)
                        }
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })

    }
}