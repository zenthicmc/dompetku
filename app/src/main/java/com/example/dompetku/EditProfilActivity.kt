package com.example.dompetku

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.fragment.SettingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class EditProfilActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var photoProfil : ImageView
    private lateinit var editName : EditText
    private lateinit var editNomor : EditText
    private lateinit var editEmail : EditText
    private lateinit var btnSimpan : Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        sessionManager = SessionManager(this)
        photoProfil = findViewById(R.id.photoProfile)
        editName = findViewById(R.id.editName)
        editNomor = findViewById(R.id.editNomor)
        editEmail = findViewById(R.id.editEmail)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnSimpan.setOnClickListener{
            val name = editName.text.toString().trim()
            val hp = editNomor.text.toString().trim()
            val email = editEmail.text.toString().trim()

            if (name.isEmpty()) {
                editName.error = "Nomor HP tidak boleh kosong"
                editName.requestFocus()
            } else if (hp.isEmpty()) {
                editNomor.error = "Password tidak boleh kosong"
                editNomor.requestFocus()
            } else if (email.isEmpty()) {
                editEmail.error = "Password tidak boleh kosong"
                editEmail.requestFocus()
            } else {
                updateUser(name, hp, email)
            }
            finish()
        }

        getUser()

    }


    private fun getUser() {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/getprofile")
            .setTag("profile")
            .addHeaders("Authorization", "Bearer $token")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())

                    val getJsonObject: JSONObject = response.getJSONObject("data")
                    val user = getJsonObject.getJSONObject("user")

                    if(response.getString("success").equals("true")) {
                        setProfile(user)
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })
    }

    private fun setProfile(user: JSONObject) {
        editName.setText(user.getString("name"))
        editNomor.setText(user.getString("nohp"))
        editEmail.setText(user.getString("email"))

        // load image
        Picasso.get()
            .load(user.getString("image"))
            .into(photoProfil)
    }

    private fun updateUser(name: String, nohp: String, email: String) {
        sessionManager = SessionManager(this)
        AndroidNetworking.put("https://dompetku-api.vercel.app/api/auth/login")
            .setTag("register")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("name", name)
            .addBodyParameter("nohp", nohp)
            .addBodyParameter("email", email)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response Update User", response.toString())
                    val data = response.getJSONObject("data")
                    if(response.getString("success").equals("true"))
                        intent.putExtra("name", data.getString("name"))
                        intent.putExtra("nohp", data.getString("nohp"))
                        intent.putExtra("email", data.getString("email"))
                        startActivity(intent)

                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@EditProfilActivity)
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