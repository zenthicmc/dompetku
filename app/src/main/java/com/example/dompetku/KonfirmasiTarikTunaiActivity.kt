package com.example.dompetku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class KonfirmasiTarikTunaiActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_tarik_tunai)

        sessionManager = SessionManager(this)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }


    }
}