package com.example.dompetku.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.*
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.DecimalFormat


class SettingFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var photoProfile: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtHp: TextView
    private lateinit var txtUangMasuk: TextView
    private lateinit var txtUangKeluar: TextView
    private lateinit var btnEditProdil: RelativeLayout
    private lateinit var btnTentangKami: RelativeLayout
    private lateinit var btnLogout: RelativeLayout
    private lateinit var shimmer1: ShimmerFrameLayout
    private lateinit var shimmer2: ShimmerFrameLayout
    private lateinit var profile: LinearLayout
    private lateinit var stats: RelativeLayout
    private lateinit var swipe: SwipeRefreshLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        sessionManager = SessionManager(activity)
        photoProfile = view.findViewById(R.id.photoProfile)
        txtName = view.findViewById(R.id.txtName)
        txtHp = view.findViewById(R.id.txtHp)
        txtUangMasuk = view.findViewById(R.id.txtUangMasuk)
        txtUangKeluar = view.findViewById(R.id.txtUangKeluar)

        shimmer1 = view.findViewById(R.id.shimmer1)
        shimmer2 = view.findViewById(R.id.shimmer2)
        profile = view.findViewById(R.id.profile)
        stats = view.findViewById(R.id.stats)
        swipe = view.findViewById(R.id.swipe)

        getUserProfile()
        getUserStats()

        swipe.setOnRefreshListener {
            getUserProfile()
            getUserStats()
            swipe.isRefreshing = false
        }

        btnEditProdil = view.findViewById(R.id.btnEditProfil)
        btnTentangKami = view.findViewById(R.id.btnTentangKami)
        btnLogout = view.findViewById(R.id.btnLogout)

        btnEditProdil.setOnClickListener{
            val intent = Intent(activity, EditProfilActivity::class.java)
            startActivity(intent)
        }
        btnTentangKami.setOnClickListener{
            val intent = Intent(activity, TentangKamiActivity::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            sessionManager.removeData()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        getUserProfile()
        getUserStats()
    }

    private fun getUserProfile() {
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

                    if(response.getString("success").equals("true")) {
                        setProfile(user)

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

    private fun getUserStats() {
        startShimmer()
        val token = sessionManager.getToken()

        AndroidNetworking.get("https://dompetku-api.vercel.app/api/user/getstats")
            .addHeaders("Authorization", "Bearer $token")
            .setTag("profile")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("response", response.toString())
                    val data: JSONObject = response.getJSONObject("data")

                    if(response.getString("success").equals("true")) {
                        val decimalFormat = DecimalFormat("#,###")
                        txtUangMasuk.text = "Rp. ${decimalFormat.format(data.getInt("uangMasuk"))}"
                        txtUangKeluar.text = "Rp. ${decimalFormat.format(data.getInt("uangKeluar"))}"

                        stopShimmer()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("error", error.toString())
                }
            })
    }

    private fun setProfile(user: JSONObject) {
        txtName.text = user.getString("name")
        txtHp.text = user.getString("nohp")

        // load image
        Picasso.get()
            .load(user.getString("image"))
            .into(photoProfile)
    }

    private fun startShimmer() {
        shimmer1.visibility = View.VISIBLE
        shimmer1.startShimmer()

        shimmer2.visibility = View.VISIBLE
        shimmer2.startShimmer()

        profile.visibility = View.GONE
        stats.visibility = View.GONE
    }

    private fun stopShimmer() {
        shimmer1.visibility = View.GONE
        shimmer1.stopShimmer()

        shimmer2.visibility = View.GONE
        shimmer2.stopShimmer()

        profile.visibility = View.VISIBLE
        stats.visibility = View.VISIBLE
    }
}