package com.example.dompetku

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

class PembayaranActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var txtSaldo: TextView
    private lateinit var editReceiver: EditText
    private lateinit var btnBayar: Button
    private lateinit var sessionManager: SessionManager
    private lateinit var txtKet: TextView
    private lateinit var txtHelper: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        btnBack = findViewById(R.id.btnBack)
        txtSaldo = findViewById(R.id.txtSaldo)
        editReceiver = findViewById(R.id.editReceiver)
        btnBayar = findViewById(R.id.btnBayar)
        sessionManager = SessionManager(this)
        txtKet = findViewById(R.id.txtKet)
        txtHelper = findViewById(R.id.txtHelper)

        getSaldo()

        val type = intent.getStringExtra("type")
        if(type.equals("data") || type.equals("pulsa") || type.equals("etoll")) {
            txtKet.text = "Masukkan Nomor Penerima"
            txtHelper.text = "*Nomor penerima harus valid sesuai dengan operator yang dipilih"
        } else {
            txtKet.text = "Masukkan Customer ID"
            txtHelper.text = ""
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnBayar.setOnClickListener {
            val receiver = editReceiver.text.toString().trim()
            val code = intent.getStringExtra("code").toString()

            if(receiver.isEmpty()) {
                editReceiver.error = "Penerima tidak boleh kosong"
                editReceiver.requestFocus()
                return@setOnClickListener
            } else {
                requestTopup(code, receiver, this)
            }

        }
    }

    private fun requestTopup(code: String, receiver: String, context: Context) {
        val token = sessionManager.getToken()

        AndroidNetworking.post("https://dompetku-api.vercel.app/api/transaction/topup")
            .setTag("topup")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("receiver", receiver)
            .addBodyParameter("product_code", code)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val data = response.getJSONObject("data")
                        Log.d("response sukses", response.toString())
                        if(response.getString("success").equals("true")) {
                            val intent = Intent(context, BerhasilActivity::class.java)
                            intent.putExtra("title", "Topup Berhasil")
                            intent.putExtra("amount", data.getString("amount"))
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        Log.d("response gagal", response.toString())
                    }

                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@PembayaranActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
    }

    private fun getSaldo() {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/getprofile")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("profile")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())

                    val getJsonObject: JSONObject = response.getJSONObject("data")
                    val user = getJsonObject.getJSONObject("user")

                    if(response.getString("success").equals("true")) {
                        val decimalFormat = DecimalFormat("#,###")
                        txtSaldo.text = decimalFormat.format(user.getInt("saldo")).toString()

                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })
    }
}