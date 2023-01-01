package com.example.dompetku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject


class CreatePasswordActivity : AppCompatActivity() {
    private lateinit var btnLogin: TextView
    private lateinit var inputPassword: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var btnFinish: Button
    private lateinit var contextView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)

        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        btnFinish = findViewById(R.id.btnFinish)
        btnLogin = findViewById(R.id.btnLogin)
        contextView = findViewById(R.id.view)

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnFinish.setOnClickListener {
            val password = inputPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            // check if password and confirm password is same
            if (password != confirmPassword) {
                inputConfirmPassword.error = "Password tidak sama"
                inputConfirmPassword.requestFocus()
            } else if(password.length < 8) {
                inputPassword.error = "Password minimal 8 karakter"
                inputPassword.requestFocus()
            } else {
                register(password)
            }
        }
    }

    private fun register(password: String) {
        // get data from intent
        val bundle = intent.extras
        val nama = bundle?.get("nama").toString()
        val email = bundle?.get("email").toString()
        val nohp = bundle?.get("nohp").toString()
        val jenisKelamin = bundle?.get("kelamin").toString()

        // send data to server
        AndroidNetworking.post("https://dompetku-api.vercel.app/api/auth/register")
            .setTag("register")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("name", nama)
            .addBodyParameter("email", email)
            .addBodyParameter("password", password)
            .addBodyParameter("nohp", nohp)
            .addBodyParameter("kelamin", jenisKelamin)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())
                    if(response.getString("success").equals("true")) {
                        MaterialAlertDialogBuilder(this@CreatePasswordActivity)
                            .setTitle("Registrasi Berhasil")
                            .setMessage("Silahkan login menggunakan akun anda untuk melanjutkan")
                            .setPositiveButton("Login") { dialog, which ->
                                val intent = Intent(this@CreatePasswordActivity, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            .show()
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@CreatePasswordActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
    }
}