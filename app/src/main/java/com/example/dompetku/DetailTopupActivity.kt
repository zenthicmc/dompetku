package com.example.dompetku

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.DecimalFormat

class DetailTopupActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var photoProfile: ImageView
    private lateinit var txtAmount: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtId: TextView
    private lateinit var txtProduct: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_topup)

        btnBack = findViewById(R.id.btnBack)
        photoProfile = findViewById(R.id.photoProfile)
        txtAmount = findViewById(R.id.txtAmount)
        txtStatus = findViewById(R.id.txtStatus)
        txtDate = findViewById(R.id.txtDate)
        txtId = findViewById(R.id.txtId)
        txtProduct = findViewById(R.id.txtProduct)
        sessionManager = SessionManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        requestDetailTopup(this)
    }

    private fun requestDetailTopup(context: Context) {
        val id = intent.getStringExtra("id")
        val token = sessionManager.getToken()
        val decimalFormat = DecimalFormat("#,###")

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/transaction/${id}")
            .setTag("detail withdraw")
            .setPriority(Priority.LOW)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response topup", response.toString())
                    val data = response.getJSONObject("data")
                    val receiver = data.getJSONObject("receiver")
                    val date = data.getString("createdAt")

                    if(response.getString("success").equals("true")) {
                        // format date to dd-mm-yyyy HH:mm
                        val formatedDate = date.substring(8,10) + "-" + date.substring(5,7) + "-" + date.substring(0,4) + " " + date.substring(11,16)

                        txtId.text = data.getString("reference")
                        txtProduct.text = data.getString("product_code")
                        txtAmount.text = "Rp " + decimalFormat.format(data.getInt("amount")).toString()
                        txtStatus.text = data.getString("status")
                        txtDate.text = formatedDate

                        Picasso.get()
                            .load(receiver.getString("image"))
                            .into(photoProfile)
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@DetailTopupActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

                    if(jsonObject.getString("code").equals("401")) {
                        val intent = Intent(this@DetailTopupActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
    }
}