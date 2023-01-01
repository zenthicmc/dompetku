package com.example.dompetku

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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

class KonfirmasiTarikTunaiActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var metodeIcon : ImageView
    private lateinit var metodeName : TextView
    private lateinit var txtRekening : TextView
    private lateinit var txtSubtotal : TextView
    private lateinit var txtTotal : TextView
    private lateinit var btnLanjut: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_tarik_tunai)

        sessionManager = SessionManager(this)
        btnBack = findViewById(R.id.btnBack)

        metodeIcon = findViewById(R.id.metodeIcon)
        metodeName = findViewById(R.id.metodeName)
        txtRekening = findViewById(R.id.txtRekening)
        txtSubtotal = findViewById(R.id.txtSubtotal)
        txtTotal = findViewById(R.id.txtTotal)
        btnLanjut = findViewById(R.id.btnLanjut)

        setData()

        btnBack.setOnClickListener {
            finish()
        }

        btnLanjut.setOnClickListener {
            val amount = (intent.getStringExtra("amount")?.toInt()?.minus(2500)).toString()
            val rekening = intent.getStringExtra("rekening")

            requestWithdraw(rekening.toString(), amount.toString())
        }
    }

    private fun setData() {
        val decimalFormat = DecimalFormat("#,###")

        // set metode pembayaran
        val metode = sessionManager.getMetode()
        metodeName.text = metode["name"]
        Picasso.get().load(metode["icon"]).into(metodeIcon)

        // set detail
        val amount = intent.getStringExtra("amount")
        val rekening = intent.getStringExtra("rekening")

        txtRekening.text = rekening
        txtSubtotal.text = "Rp " + decimalFormat.format(amount?.toInt()).toString()
        txtTotal.text = "Rp " + decimalFormat.format(amount?.toInt()?.minus(2500)).toString()
    }

    private fun requestWithdraw(rekening: String, amount: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.post("https://dompetku-api.vercel.app/api/transaction/withdraw")
            .setTag("withdraw")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("amount", amount)
            .addBodyParameter("rekening", rekening)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())
                    if (response.getString("success").equals("true")) {
                        val intent = Intent(this@KonfirmasiTarikTunaiActivity, BerhasilActivity::class.java)
                        intent.putExtra("title", "Withdraw Berhasil")
                        intent.putExtra("amount", amount)
                        startActivity(intent)
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@KonfirmasiTarikTunaiActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

                    if(jsonObject.getString("code").equals("401")) {
                        val intent = Intent(this@KonfirmasiTarikTunaiActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
    }
}