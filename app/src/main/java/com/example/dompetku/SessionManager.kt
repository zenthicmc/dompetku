package com.example.dompetku

import android.content.Context
import android.content.SharedPreferences

class SessionManager(var context: Context?) {
    private val IS_LOGIN = "is_login"

    var pref: SharedPreferences? = context?.getSharedPreferences("GET_TOKEN", Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor? = pref?.edit()

    fun setLogin(isLogin: Boolean) {
        editor?.putBoolean(IS_LOGIN, isLogin)
        editor?.commit()
    }

    fun setEmail(email: String?) {
        editor?.putString("email", email)
        editor?.commit()
    }

    fun setToken(token: String?) {
        editor?.putString("token", token)
        editor?.commit()
    }

    fun setMetode(code: String?, name: String?, icon: String?) {
        editor?.putString("code", code)
        editor?.putString("name", name)
        editor?.putString("icon", icon)
        editor?.commit()
    }

    fun getMetode(): HashMap<String, String?> {
        val metode = HashMap<String, String?>()
        metode["code"] = pref?.getString("code", "BRIVA")
        metode["name"] = pref?.getString("name", "BRI Virtual Account")
        metode["icon"] = pref?.getString("icon", "https://tripay.co.id/images/payment-channel/8WQ3APST5s1579461828.png")
        return metode
    }

    fun getToken(): String? {
        return pref?.getString("token", "")
    }

    fun isLogin(): Boolean? {
        return pref?.getBoolean(IS_LOGIN, false)
    }

    fun getName(): String? {
        return pref?.getString("name", "")
    }

    fun removeData() {
        editor?.clear()
        editor?.commit()
    }
}