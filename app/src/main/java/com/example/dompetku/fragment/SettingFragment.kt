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
import android.widget.RelativeLayout
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.dompetku.*
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

        getUserProfile()
        getUserStats()

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

    private fun getUserProfile() {
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

}