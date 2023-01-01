package com.example.dompetku

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.*

class CheckOutActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var txtDate: TextView
    private lateinit var icon: ImageView
    private lateinit var txtAmount: TextView
    private lateinit var txtMethod: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtRek: TextView
    private lateinit var txtReference: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnPay: Button
    private lateinit var iconCopy: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        sessionManager = SessionManager(this)
        txtDate = findViewById(R.id.txtDate)
        icon = findViewById(R.id.icon)
        txtAmount = findViewById(R.id.txtAmount)
        txtMethod = findViewById(R.id.txtMethod)
        txtStatus = findViewById(R.id.txtStatus)
        txtRek = findViewById(R.id.txtRek)
        txtReference = findViewById(R.id.txtReference)
        btnBack = findViewById(R.id.btnBack)
        btnPay = findViewById(R.id.btnPay)
        iconCopy = findViewById(R.id.iconCopy)

        btnBack.setOnClickListener {
            finish()
        }

        btnPay.setOnClickListener {
            checkStatus()
        }

        iconCopy.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("rek", txtRek.text.toString().trim())
            clipboardManager.setPrimaryClip(clipData)

            MaterialAlertDialogBuilder(this)
                .setTitle("Success")
                .setMessage("No rekening berhasil disalin")
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        getDetailDeposit()

    }

    private fun getDetailDeposit() {
        val id = intent.getStringExtra("id")
        val metode = sessionManager.getMetode()
        val icon_metode = metode["icon"]

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

                    val formated = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    val unixDate = data.getInt("expired_time")
                    val date = Date(unixDate.toLong() * 1000)

                    if(response.getString("success").equals("true")) {
                        txtDate.text = "Bayar sebelum: " + formated.format(date)
                        Picasso.get().load(icon_metode).into(icon)
                        txtAmount.text = "Rp " + decimalFormat.format(data.getInt("amount")).toString()
                        txtRek.text = data.getString("pay_code")
                        txtMethod.text = data.getString("payment_method")
                        txtStatus.text = data.getString("status")
                        if(data.getString("status").equals("PAID")) {
                            txtStatus.setTextColor(resources.getColor(R.color.green))
                        } else {
                            txtStatus.setTextColor(resources.getColor(R.color.red))
                        }
                        txtReference.text = data.getString("reference")
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@CheckOutActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
    }

    private fun checkStatus() {
        val id = intent.getStringExtra("id")
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/transaction/${id}")
            .setTag("detail deposit")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response status", response.toString())
                    val data = response.getJSONObject("data")

                    if(response.getString("success").equals("true")) {
                        if(data.getString("status").equals("PAID")) {
                            txtStatus.text = data.getString("status")
                            txtStatus.setTextColor(resources.getColor(R.color.green))

                            val intent = Intent(this@CheckOutActivity, BerhasilActivity::class.java)
                            intent.putExtra("title", "Deposit Berhasil")
                            intent.putExtra("amount", data.getString("amount"))
                            startActivity(intent)
                        } else {
                            txtStatus.text = data.getString("status")
                            txtStatus.setTextColor(resources.getColor(R.color.red))
                        }
                    }
                }

                override fun onError(error: ANError) {

                }
            })
    }
}