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
import android.widget.RadioButton
import android.widget.RadioGroup
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
    private lateinit var radioMale : RadioButton
    private lateinit var radioFemale : RadioButton
    private lateinit var radioGroup : RadioGroup
    private lateinit var idUser : String
    private lateinit var jenisKelamin : String


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
        radioMale = findViewById(R.id.radioMale)
        radioFemale = findViewById(R.id.radioFemale)
        radioGroup = findViewById(R.id.radioKelamin)

        jenisKelamin = ""


        btnSimpan.setOnClickListener{
            val name = editName.text.toString().trim()
            val hp = editNomor.text.toString().trim()
            val email = editEmail.text.toString().trim()


            val kelamin = radioGroup.checkedRadioButtonId.toString()
            if (kelamin == "radioFemale"){
                 jenisKelamin = "Female"
            } else if(kelamin == "radioMale"){
                 jenisKelamin = "Male"
            }

            if (name.isEmpty()) {
                editName.error = "Nomor HP tidak boleh kosong"
                editName.requestFocus()
            } else if (hp.isEmpty()) {
                editNomor.error = "No hp tidak boleh kosong"
                editNomor.requestFocus()
            } else if (email.isEmpty()) {
                editEmail.error = "Email tidak boleh kosong"
                editEmail.requestFocus()
            } else {
                updateUser(name, hp, email, jenisKelamin)
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
                        idUser = user.getString("_id")
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
        val kelamin = user.getString("kelamin")

        if (kelamin == "Female"){
            radioGroup.check(R.id.radioFemale)
        } else if(kelamin == "Male"){
            radioGroup.check(R.id.radioMale)
        }

        // load image
        Picasso.get()
            .load(user.getString("image"))
            .into(photoProfil)
    }

    private fun updateUser(name: String, nohp: String, email: String,kelamin : String ) {
        sessionManager = SessionManager(this)
        AndroidNetworking.put("https://dompetku-api.vercel.app/api/user/{$idUser}")
            .setTag("register")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("name", name)
            .addBodyParameter("nohp", nohp)
            .addBodyParameter("email", email)
            .addBodyParameter("kelamin",kelamin)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response Update User", response.toString())
                    val data = response.getJSONObject("data")
                    if(response.getString("success").equals("true")) {
                        MaterialAlertDialogBuilder(this@EditProfilActivity)
                            .setTitle("Login Gagal")
                            .setMessage("Update data berhasil!!")
                            .setPositiveButton("OK") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

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