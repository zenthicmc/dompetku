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