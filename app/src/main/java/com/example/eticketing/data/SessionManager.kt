package com.example.eticketing.data

import android.content.Context
import android.content.Intent

object SessionManager {

    fun getUserId(context: Context): Long {
        return context.getSharedPreferences("session", Context.MODE_PRIVATE)
            .getLong("userId", -1L)
    }

    fun getUserRole(context: Context): String? {
        return context.getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("userRole", null)
    }

    fun getUserName(context: Context): String {
        return context.getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("userName", "Pengguna") ?: "Pengguna"
    }

    fun isLoggedIn(context: Context): Boolean {
        return getUserRole(context) != null
    }

    fun logout(context: Context, loginActivityClass: Class<*>) {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
            .edit().clear().apply()
        val intent = Intent(context, loginActivityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}