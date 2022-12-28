package com.example.dompetku.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.*
import com.example.dompetku.adapter.AdapterHome
import com.example.dompetku.dataclass.DataHome
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat


class HomeFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var txtName: TextView
    private lateinit var txtSaldo: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataHome : ArrayList<DataHome>
    private lateinit var photoProfile: ImageView
    private lateinit var btnDeposit: LinearLayout
    private lateinit var btnWithdraw: LinearLayout
    private lateinit var btnTransfer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        sessionManager = SessionManager(activity)
        txtName = view.findViewById(R.id.txtName)
        txtSaldo = view.findViewById(R.id.txtSaldo)
        recyclerView = view.findViewById(R.id.recyclerHome)
        dataHome = ArrayList<DataHome>()
        photoProfile = view.findViewById(R.id.photoProfile)

        getData()

        btnDeposit = view.findViewById(R.id.btnDeposit)
        btnDeposit.setOnClickListener {
            val intent = Intent(activity, DepositActivity::class.java)
            startActivity(intent)
        }

        btnWithdraw = view.findViewById(R.id.btnWithdraw)
        btnWithdraw.setOnClickListener {
            val intent = Intent(activity, TarikTunaiActivity::class.java)
            startActivity(intent)
        }

        btnTransfer = view.findViewById(R.id.btnTransfer)
        btnTransfer.setOnClickListener {
            val intent = Intent(activity, Transfer1Activity::class.java)
            startActivity(intent)
        }

        return view
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
                    val transactions = getJsonObject.getJSONArray("transactions")

                    if(response.getString("success").equals("true")) {
                        setProfile(user)
                        setTransactions(transactions)
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })
    }

    private fun setProfile(user: JSONObject) {
        val decimalFormat = DecimalFormat("#,###")

        txtName.text = "Halo, " + user.getString("name") + "!"
        txtSaldo.text = decimalFormat.format(user.getInt("saldo")).toString()

        // load image
        Picasso.get()
            .load(user.getString("image"))
            .into(photoProfile)
    }

    private fun setTransactions(transactions: JSONArray) {
        for (i in 0 until transactions.length()) {
            val transaction = transactions.getJSONObject(i)

            dataHome.add(
                DataHome(
                    transaction.getString("_id"),
                    transaction.getString("type"),
                    transaction.getInt("amount"),
                    transaction.getString("createdAt"),
                    transaction.getString("status"),
                    transaction.getString("icon")
                )
            )

            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = activity?.let { AdapterHome(it, dataHome) }
        }
    }
}