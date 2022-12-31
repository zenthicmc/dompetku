package com.example.dompetku

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class KonfirmasiTarikTunaiActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var txtAmount : TextView
    private lateinit var metodeIcon : ImageView
    private lateinit var metodeName : TextView
    private lateinit var txtJumlahAmount : TextView
    private lateinit var txtBiayaAdmin : TextView
    private lateinit var txtTotal : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_tarik_tunai)

        sessionManager = SessionManager(this)
        btnBack = findViewById(R.id.btnBack)

        txtAmount = findViewById(R.id.txtAmount)
        metodeIcon = findViewById(R.id.metodeIcon)
        metodeName = findViewById(R.id.metodeName)
        txtJumlahAmount = findViewById(R.id.txtjumlahAmount)
        txtBiayaAdmin = findViewById(R.id.txtBiayaAdmin)
        txtTotal = findViewById(R.id.txtTotal)

        btnBack.setOnClickListener {
            finish()
        }


    }
}