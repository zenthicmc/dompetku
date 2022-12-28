package com.example.dompetku

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

class KonfirmasiTransferActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtAmount: TextView
    private lateinit var txtCatatan: TextView
    private lateinit var photoProfile: ImageView
    private lateinit var btnSend: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_transfer)

        sessionManager = SessionManager(this)
        btnBack = findViewById(R.id.btnBack)
        txtName = findViewById(R.id.txtName)
        txtPhone = findViewById(R.id.txtPhone)
        txtAmount = findViewById(R.id.txtAmount)
        txtCatatan = findViewById(R.id.txtCatatan)
        photoProfile = findViewById(R.id.photoProfile)
        btnSend = findViewById(R.id.btnSend)

        btnBack.setOnClickListener {
            finish()
        }

        val nohp = intent.getStringExtra("nohp").toString()
        val amount = intent.getStringExtra("amount").toString()
        val catatan = intent.getStringExtra("catatan").toString()
        val decimalFormat = DecimalFormat("#,###")

        txtPhone.text = nohp
        txtAmount.text = "Rp " + decimalFormat.format(amount.toInt()).toString()
        txtCatatan.text = catatan

        btnSend.setOnClickListener {
            transfer(nohp, amount, catatan)
        }

        getUser(nohp)
    }

    private fun getUser(phone: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/phone/${phone}")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("recents")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response detail", response.toString())
                    val data: JSONObject = response.getJSONObject("data")

                    if(response.getString("success").equals("true")) {
                        Picasso.get()
                            .load(data.getString("image"))
                            .into(photoProfile)

                        txtName.text = data.getString("name")
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })
    }

    private fun transfer(nohp: String, amount: String, catatan: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.post("https://dompetku-api.vercel.app/api/transaction/transfer")
            .setTag("transfer")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("receiver", nohp)
            .addBodyParameter("amount", amount)
            .addBodyParameter("catatan", catatan)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())
                    if(response.getString("success").equals("true")) {
                        val intent = Intent(this@KonfirmasiTransferActivity, BerhasilActivity::class.java)
                        intent.putExtra("title", "Transfer Berhasil")
                        intent.putExtra("amount", amount)
                        startActivity(intent)
                    }
                }

                override fun onError(error: ANError) {
                    MaterialAlertDialogBuilder(this@KonfirmasiTransferActivity)
                        .setTitle("Transfer Gagal")
                        .setMessage("Saldo anda tidak cukup")
                        .setPositiveButton("Ok") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
    }
}