package com.example.dompetku

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TentangKamiActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang_kami)

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}