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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.*
import com.example.dompetku.adapter.AdapterHome
import com.example.dompetku.dataclass.DataHome
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var shimmer2: ShimmerFrameLayout
    private lateinit var shimmer3: ShimmerFrameLayout
    private lateinit var home: LinearLayout
    private lateinit var welcome: RelativeLayout
    private lateinit var swipe: SwipeRefreshLayout

    // Menu topup & tagihan
    private lateinit var btnListrik: LinearLayout
    private lateinit var btnInternet: LinearLayout
    private lateinit var btnGame: LinearLayout
    private lateinit var btnVoucher: LinearLayout
    private lateinit var btnEmoney: LinearLayout
    private lateinit var btnPulsa: LinearLayout

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
        shimmer = view.findViewById(R.id.shimmer)
        shimmer2 = view.findViewById(R.id.shimmer2)
        shimmer3 = view.findViewById(R.id.shimmer3)
        home = view.findViewById(R.id.home)
        welcome = view.findViewById(R.id.welcome)
        swipe = view.findViewById(R.id.swipe)

        // Menu topup & tagihan
        btnListrik = view.findViewById(R.id.btnListrik)
        btnInternet = view.findViewById(R.id.btnInternet)
        btnGame = view.findViewById(R.id.btnGame)
        btnVoucher = view.findViewById(R.id.btnVoucher)
        btnEmoney = view.findViewById(R.id.btnEmoney)
        btnPulsa = view.findViewById(R.id.btnPulsa)

        btnListrik.setOnClickListener {
            topupTagihan("listrik")
        }

        btnInternet.setOnClickListener {
            topupTagihan("internet")
        }

        btnGame.setOnClickListener {
            topupTagihan("game")
        }

        btnVoucher.setOnClickListener {
            topupTagihan("voucher")
        }

        btnEmoney.setOnClickListener {
            topupTagihan("emoney")
        }

        btnPulsa.setOnClickListener {
            topupTagihan("pulsa")
        }

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

        swipe.setOnRefreshListener {
            dataHome.clear()
            getData()
            swipe.isRefreshing = false
        }

        getData()
        return view
    }

    private fun topupTagihan(category: String) {
        val intent = Intent(activity, TopupActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }


    private fun getData() {
        startShimmer()
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

                        stopShimmer()
                    }
                }

                override fun onError(error: ANError) {
                    val error = error.errorBody
                    val jsonObject = JSONObject(error)

                    if(jsonObject.getString("code").equals("401")) {
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                    }
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

    private fun startShimmer() {
        shimmer.visibility = View.VISIBLE
        shimmer.startShimmer()

        shimmer2.visibility = View.VISIBLE
        shimmer2.startShimmer()

        shimmer3.visibility = View.VISIBLE
        shimmer3.startShimmer()

        home.visibility = View.GONE
        welcome.visibility = View.GONE
    }

    private fun stopShimmer() {
        shimmer.stopShimmer()
        shimmer.visibility = View.GONE

        shimmer2.stopShimmer()
        shimmer2.visibility = View.GONE

        shimmer3.stopShimmer()
        shimmer3.visibility = View.GONE

        home.visibility = View.VISIBLE
        welcome.visibility = View.VISIBLE
    }
}