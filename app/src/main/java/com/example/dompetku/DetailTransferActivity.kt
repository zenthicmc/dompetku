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
import java.text.SimpleDateFormat
import java.util.*

class DetailTransferActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var photoProfile: ImageView
    private lateinit var txtAmount: TextView
    private lateinit var txtSender: TextView
    private lateinit var txtSenderPhone: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtDate: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transfer)

        btnBack = findViewById(R.id.btnBack)
        photoProfile = findViewById(R.id.photoProfile)
        txtAmount = findViewById(R.id.txtAmount)
        txtSender = findViewById(R.id.txtSender)
        txtSenderPhone = findViewById(R.id.txtSenderPhone)
        txtStatus = findViewById(R.id.txtStatus)
        txtDate = findViewById(R.id.txtDate)
        sessionManager = SessionManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        requestDetailTransfer(this)
    }

    private fun requestDetailTransfer(context: Context) {
        val id = intent.getStringExtra("id")
        val token = sessionManager.getToken()
        val decimalFormat = DecimalFormat("#,###")

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/transaction/${id}")
            .setTag("detail deposit")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response deposit", response.toString())
                    val data = response.getJSONObject("data")
                    val sender = data.getJSONObject("sender")
                    val receiver = data.getJSONObject("receiver")
                    val date = data.getString("createdAt")

                    if(response.getString("success").equals("true")) {
                        txtAmount.text = "Rp " + decimalFormat.format(data.getInt("amount")).toString()
                        txtSender.text = receiver.getString("name")
                        txtSenderPhone.text = receiver.getString("nohp")
                        txtStatus.text = data.getString("status")
                        txtDate.text = date.substring(8,10) + "-" + date.substring(5,7) + "-" + date.substring(0,4)

                        Picasso.get()
                            .load(receiver.getString("image"))
                            .into(photoProfile)
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@DetailTransferActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

                    if(jsonObject.getString("code").equals("401")) {
                        val intent = Intent(this@DetailTransferActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
    }


}