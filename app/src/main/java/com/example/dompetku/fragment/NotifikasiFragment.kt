package com.example.dompetku.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.LoginActivity
import com.example.dompetku.R
import com.example.dompetku.SessionManager
import com.example.dompetku.adapter.AdapterNotifikasi
import com.example.dompetku.dataclass.DataNotifikasi
import com.facebook.shimmer.ShimmerFrameLayout
import org.json.JSONArray
import org.json.JSONObject


class NotifikasiFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataNotifikasi : ArrayList<DataNotifikasi>
    private lateinit var sessionManager: SessionManager
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var swipe: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifikasi, container, false)

        sessionManager = SessionManager(activity)
        recyclerView = view.findViewById(R.id.recyclerNotifikasi)
        dataNotifikasi = ArrayList<DataNotifikasi>()
        shimmer = view.findViewById(R.id.shimmer)
        swipe = view.findViewById(R.id.swipe)

        getNotifications()
        swipe.setOnRefreshListener {
            dataNotifikasi.clear()
            getNotifications()
            swipe.isRefreshing = false
        }

        return view
    }

    private fun getNotifications() {
        startShimmer()
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/notification")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("notification")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())
                    val data: JSONArray = response.getJSONArray("data")
                    if(response.getString("success").equals("true")) {
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            dataNotifikasi.add(
                                DataNotifikasi(
                                    item.getString("_id"),
                                    item.getString("user_id"),
                                    item.getString("receiver_id"),
                                    item.getString("title"),
                                    item.getString("desc"),
                                    item.getString("createdAt")
                                )
                            )

                            recyclerView.layoutManager = LinearLayoutManager(activity)
                            recyclerView.adapter = activity?.let { AdapterNotifikasi(it, dataNotifikasi) }
                        }

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

    private fun startShimmer() {
        shimmer.visibility = View.VISIBLE
        shimmer.startShimmer()
    }

    private fun stopShimmer() {
        shimmer.visibility = View.GONE
        shimmer.startShimmer()
    }
}