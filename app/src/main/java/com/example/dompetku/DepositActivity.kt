package com.example.dompetku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DepositActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var btnBack: ImageView
    private lateinit var editAmount: EditText
    private lateinit var btn10k: TextView
    private lateinit var btn20k: TextView
    private lateinit var btn50k: TextView
    private lateinit var btn100k: TextView
    private lateinit var btn500k: TextView
    private lateinit var btn1000k: TextView
    private lateinit var btnLanjut: Button
    private lateinit var btnMethod: RelativeLayout
    private lateinit var metodeIcon: ImageView
    private lateinit var metodeName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)

        sessionManager = SessionManager(this)
        metodeIcon = findViewById(R.id.metodeIcon)
        metodeName = findViewById(R.id.metodeName)

        // set metode value
        val metode = sessionManager.getMetode()
        metodeName.text = metode["name"]
        Picasso.get().load(metode["icon"]).into(metodeIcon)

        btnBack = findViewById(R.id.btnBack)
        editAmount = findViewById(R.id.editAmount)
        btn10k = findViewById(R.id.btn10k)
        btn20k = findViewById(R.id.btn20k)
        btn50k = findViewById(R.id.btn50k)
        btn100k = findViewById(R.id.btn100k)
        btn500k = findViewById(R.id.btn500k)
        btn1000k = findViewById(R.id.btn1000k)
        btnMethod = findViewById(R.id.btnMethod)
        btnLanjut = findViewById(R.id.btnLanjut)

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

        btnMethod.setOnClickListener {
            val intent = Intent(this, MetodeActivity::class.java)
            startActivity(intent)
        }

        btnLanjut.setOnClickListener {
            val metode = sessionManager.getMetode()
            val code = metode["code"]

            // check if editAmount is empty
            if(editAmount.text.toString().isEmpty()) {
                editAmount.error = "Amount is required"
                editAmount.requestFocus()
                return@setOnClickListener
            } else if (editAmount.text.toString().trim().length > 1000000) {
                editAmount.error = "Maximum amount is 1.000.000"
                editAmount.requestFocus()
                return@setOnClickListener
            } else {
                requestDeposit(code, editAmount.text.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // set metode value
        val metode = sessionManager.getMetode()
        metodeName.text = metode["name"]
        Picasso.get().load(metode["icon"]).into(metodeIcon)
    }

    private fun setAmount(amount: String) {
        editAmount.setText(amount)
    }

    private fun requestDeposit(method: String?, amount: String) {
        val token = sessionManager.getToken()

        AndroidNetworking.post("https://dompetku-api.vercel.app/api/transaction/deposit")
            .setTag("deposit")
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("method", method)
            .addBodyParameter("amount", amount)
            .addHeaders("Authorization", "Bearer $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response deposit", response.toString())
                    val data = response.getJSONObject("data")

                    if(response.getString("success").equals("true")) {
                        val intent = Intent(this@DepositActivity, CheckOutActivity::class.java)
                        intent.putExtra("id", data.getString("_id"))
                        intent.putExtra("reference", data.getString("reference"))
                        intent.putExtra("merchant_ref", data.getString("merchant_ref"))
                        startActivity(intent)
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    MaterialAlertDialogBuilder(this@DepositActivity)
                        .setTitle("Gagal")
                        .setMessage(jsonObject.getString("message"))
                        .setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()

                    if(jsonObject.getString("code").equals("401")) {
                        val intent = Intent(this@DepositActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
    }
}