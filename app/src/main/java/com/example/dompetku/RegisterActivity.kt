package com.example.dompetku

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnLogin: TextView
    private lateinit var inputNama: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputHp: EditText
    private lateinit var jenisKelamin: String
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnNext = findViewById(R.id.btnNext)
        inputNama = findViewById(R.id.inputNama)
        inputEmail = findViewById(R.id.inputEmail)
        inputHp = findViewById(R.id.inputHp)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val kelamin = resources.getStringArray(R.array.Kelamin)
        val spinner = findViewById<Spinner>(R.id.inputKelamin)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, kelamin)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    jenisKelamin = kelamin[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action

                }
            }
        }

        btnNext.setOnClickListener {
            if(inputNama.text.toString().isEmpty()) {
                inputNama.error = "Nama tidak boleh kosong"
                inputNama.requestFocus()
            } else if(inputEmail.text.toString().isEmpty()) {
                inputEmail.error = "Email tidak boleh kosong"
                inputEmail.requestFocus()
            } else if(inputHp.text.toString().isEmpty()) {
                inputHp.error = "No HP tidak boleh kosong"
                inputHp.requestFocus()
            } else {
                nextStep()
            }
        }
    }

    private fun nextStep() {
        val nama = inputNama.text.toString().trim()
        val email = inputEmail.text.toString().trim()
        val hp = inputHp.text.toString().trim()

        val bundle = Bundle()
        bundle.putString("nama", nama)
        bundle.putString("email", email)
        bundle.putString("nohp", hp)
        bundle.putString("kelamin", jenisKelamin)

        val intent = Intent(this, CreatePasswordActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}