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

class DetailWithdrawActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var photoProfile: ImageView
    private lateinit var txtAmount: TextView
    private lateinit var txtStatus: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtRek: TextView
    private lateinit var txtName: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_withdraw)

        btnBack = findViewById(R.id.btnBack)
        photoProfile = findViewById(R.id.photoProfile)
        txtAmount = findViewById(R.id.txtAmount)
        txtStatus = findViewById(R.id.txtStatus)
        txtDate = findViewById(R.id.txtDate)
        txtRek = findViewById(R.id.txtRek)
        txtName = findViewById(R.id.txtName)
        sessionManager = SessionManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        requestDetailWithdraw(this)
    }

    private fun requestDetailWithdraw(context: Context) {
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
                    Log.d("response deposit", response.toString())
                    val data = response.getJSONObject("data")
                    val receiver = data.getJSONObject("receiver")
                    val date = data.getString("createdAt")

                    if(response.getString("success").equals("true")) {
                        // format date to dd-mm-yyyy HH:mm
                        val formatedDate = date.substring(8,10) + "-" + date.substring(5,7) + "-" + date.substring(0,4) + " " + date.substring(11,16)

                        txtName.text = receiver.getString("name")
                        txtAmount.text = "Rp " + decimalFormat.format(data.getInt("amount")).toString()
                        txtStatus.text = data.getString("status")
                        txtDate.text = formatedDate
                        txtRek.text = data.getString("rekening")

                        Picasso.get()
                            .load(receiver.getString("image"))
                            .into(photoProfile)
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@DetailWithdrawActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

                    if(jsonObject.getString("code").equals("401")) {
                        val intent = Intent(this@DetailWithdrawActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
    }
}