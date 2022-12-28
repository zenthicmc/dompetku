package com.example.dompetku

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.adapter.AdapterMetode
import com.example.dompetku.adapter.AdapterRecentUser
import com.example.dompetku.dataclass.DataMetode
import com.example.dompetku.dataclass.DataRecentUser
import org.json.JSONArray
import org.json.JSONObject

class Transfer1Activity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var dataRecentUser: ArrayList<DataRecentUser>
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var btnLanjut: Button
    private lateinit var editHp: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer1)

        sessionManager = SessionManager(this)
        dataRecentUser = ArrayList<DataRecentUser>()
        recyclerView = findViewById(R.id.recyclerRecent)
        editHp = findViewById(R.id.editHp)

        getRecents(this)

        btnLanjut = findViewById(R.id.btnLanjut)
        btnLanjut.setOnClickListener {
            if (editHp.query.toString().isNotEmpty()) {
                val intent = Intent(this, Transfer2Activity::class.java)
                intent.putExtra("nohp", editHp.query.toString())
                startActivity(intent)
            }
        }

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getRecents(context: Context) {
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/recents")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("recents")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response recent", response.toString())
                    val data: JSONArray = response.getJSONArray("data")

                    if(response.getString("success").equals("true")) {
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            dataRecentUser.add(
                                DataRecentUser(
                                    item.getString("_id"),
                                    item.getString("name"),
                                    item.getString("email"),
                                    item.getString("nohp"),
                                    item.getString("image")
                                )
                            )

                            recyclerView.layoutManager = GridLayoutManager(context, 4)
                            recyclerView.adapter = AdapterRecentUser(context, dataRecentUser)
                        }
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })
    }
}