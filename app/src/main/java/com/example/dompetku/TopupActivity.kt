package com.example.dompetku

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.adapter.AdapterRiwayat
import com.example.dompetku.adapter.AdapterTopup
import com.example.dompetku.dataclass.DataRiwayat
import com.example.dompetku.dataclass.DataTopup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject

class TopupActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var txtCategory: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataTopup : ArrayList<DataTopup>
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topup)

        recyclerView = findViewById(R.id.recyclerTopup)
        sessionManager = SessionManager(this)
        txtCategory = findViewById(R.id.txtCategory)
        dataTopup = ArrayList<DataTopup>()

        val category = intent.getStringExtra("category")
        txtCategory.text = "Pilih " + category?.capitalize()

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        when (category) {
            "internet" -> requestProducts(this, "internet")
            "listrik" -> requestProducts(this, "listrik")
            "game" -> requestProducts(this, "game")
            "voucher" -> requestProducts(this, "voucher")
        }
    }

    private fun requestProducts(context: Context, category: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/product/$category")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("products")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())

                    val data: JSONArray = response.getJSONArray("data")

                    if(response.getString("success").equals("true")) {
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            dataTopup.add(
                                DataTopup(
                                    item.getString("product_code"),
                                    item.getString("product_name"),
                                    item.getString("product_type"),
                                    item.getString("icon_url")
                                )
                            )

                            recyclerView.layoutManager = LinearLayoutManager(context)
                            recyclerView.adapter = AdapterTopup(context, dataTopup)
                        }
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@TopupActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })

    }
}