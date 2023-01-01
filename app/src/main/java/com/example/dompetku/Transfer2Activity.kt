package com.example.dompetku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.adapter.AdapterRecentUser
import com.example.dompetku.dataclass.DataRecentUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class Transfer2Activity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var photoProfile: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtHp: TextView
    private lateinit var editAmount: EditText
    private lateinit var editCatatan: EditText
    private lateinit var btn10k: TextView
    private lateinit var btn20k: TextView
    private lateinit var btn50k: TextView
    private lateinit var btn100k: TextView
    private lateinit var btn500k: TextView
    private lateinit var btn1000k: TextView
    private lateinit var btnLanjut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer2)

        // inisialisasi variable
        sessionManager = SessionManager(this)
        btnBack = findViewById(R.id.btnBack)
        photoProfile = findViewById(R.id.photoProfile)
        txtName = findViewById(R.id.txtName)
        txtHp = findViewById(R.id.txtHp)
        editAmount = findViewById(R.id.editAmount)
        editCatatan = findViewById(R.id.editCatatan)
        btn10k = findViewById(R.id.btn10k)
        btn20k = findViewById(R.id.btn20k)
        btn50k = findViewById(R.id.btn50k)
        btn100k = findViewById(R.id.btn100k)
        btn500k = findViewById(R.id.btn500k)
        btn1000k = findViewById(R.id.btn1000k)
        btnLanjut = findViewById(R.id.btnLanjut)

        val phone = intent.getStringExtra("nohp").toString()
        getUser(phone)

        btnBack.setOnClickListener {
            finish()
        }

        btn10k.setOnClickListener {
            setAmount("10000")
        }

        btn20k.setOnClickListener {
            setAmount("20000")
        }

        btn50k.setOnClickListener {
            setAmount("50000")
        }

        btn100k.setOnClickListener {
            setAmount("100000")
        }

        btn500k.setOnClickListener {
            setAmount("500000")
        }

        btn1000k.setOnClickListener {
            setAmount("1000000")
        }

        btnLanjut.setOnClickListener {
            val catatan: String

            // check if amount is empty
            if(editAmount.text.toString().isEmpty()){
                editAmount.error = "Jumlah tidak boleh kosong"
                editAmount.requestFocus()
                return@setOnClickListener
            }
            // check if amount is < 0
            else if(editAmount.text.toString().toInt() <= 0){
                editAmount.error = "Jumlah tidak boleh kurang dari 0"
                editAmount.requestFocus()
                return@setOnClickListener
            }
            // check if amount is > 10000000
            else if(editAmount.text.toString().toInt() > 10000000){
                editAmount.error = "Jumlah tidak boleh lebih dari 10.000.000"
                editAmount.requestFocus()
                return@setOnClickListener
            }
            // check if amount is < 10000
            else if(editAmount.text.toString().toInt() < 10000){
                editAmount.error = "Jumlah tidak boleh kurang dari 10.000"
                editAmount.requestFocus()
                return@setOnClickListener
            }

            // chek if cataatan is empty
            if(editCatatan.text.toString().isEmpty()){
                catatan = "-"
            } else {
                catatan = editCatatan.text.toString().trim()
            }

            val intent = Intent(this, KonfirmasiTransferActivity::class.java)
            intent.putExtra("nohp", phone)
            intent.putExtra("amount", editAmount.text.toString().trim())
            intent.putExtra("catatan", catatan)
            startActivity(intent)
        }
    }

    private fun getUser(phone: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/phone/${phone}")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("recents")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response detail", response.toString())
                    val data: JSONObject = response.getJSONObject("data")

                    if(response.getString("success").equals("true")) {
                        Picasso.get()
                            .load(data.getString("image"))
                            .into(photoProfile)

                        txtName.text = data.getString("name")
                        txtHp.text = data.getString("nohp")
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@Transfer2Activity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            })
    }

    private fun setAmount(amount: String) {
        editAmount.setText(amount)
    }
}