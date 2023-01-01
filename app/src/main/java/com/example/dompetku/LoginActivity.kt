package com.example.dompetku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var inputHp: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        inputHp = findViewById(R.id.inputHp)
        inputPassword = findViewById(R.id.inputPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val hp = inputHp.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (hp.isEmpty()) {
                inputHp.error = "Nomor HP tidak boleh kosong"
                inputHp.requestFocus()
            } else if (password.isEmpty()) {
                inputPassword.error = "Password tidak boleh kosong"
                inputPassword.requestFocus()
            } else {
                login(hp, password)
            }
        }
    }

    private fun login(nohp: String, password: String) {
        sessionManager = SessionManager(this)
        AndroidNetworking.post("https://dompetku-api.vercel.app/api/auth/login")
            .setTag("register")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("password", password)
            .addBodyParameter("nohp", nohp)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())
                    val getJsonObject: JSONObject = response.getJSONObject("data")
                    if(response.getString("success").equals("true")) {
                        val token = getJsonObject.getString("token")
                        sessionManager.setToken(token)
                        sessionManager.setLogin(true)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@LoginActivity)
                        .setTitle("Login Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
    }
}