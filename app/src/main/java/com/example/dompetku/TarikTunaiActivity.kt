package com.example.dompetku

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.DecimalFormat

class TarikTunaiActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var txtSaldo: TextView
    private lateinit var btnMethod: RelativeLayout
    private lateinit var metodeIcon: ImageView
    private lateinit var metodeName: TextView
    private lateinit var btnBack: ImageView
    private lateinit var editAmount: EditText
    private lateinit var btn10k: TextView
    private lateinit var btn20k: TextView
    private lateinit var btn50k: TextView
    private lateinit var btn100k: TextView
    private lateinit var btn500k: TextView
    private lateinit var btn1000k: TextView
    private lateinit var btnLanjut: RelativeLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarik_tunai)

        sessionManager = SessionManager(this)
        txtSaldo = findViewById(R.id.txtSaldo)

        metodeIcon = findViewById(R.id.metodeIcon)
        metodeName = findViewById(R.id.metodeName)

        // set metode value
        val metode = sessionManager.getMetode()
        metodeName.text = metode["name"]
        Picasso.get().load(metode["icon"]).into(metodeIcon)

        btnMethod = findViewById(R.id.btnMethod)
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

            val intent = Intent(this, KonfirmasiTransferActivity::class.java)
            intent.putExtra("amount", editAmount.text.toString().trim())
            intent.putExtra("name", metode)
            intent.putExtra("icon", metodeIcon)
            startActivity(intent)
        }

        getData()


    }

    private fun getData() {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/getprofile")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("profile")
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
        val decimalFormat = DecimalFormat("#,###")

        txtSaldo.text = decimalFormat.format(user.getInt("saldo")).toString()
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
}