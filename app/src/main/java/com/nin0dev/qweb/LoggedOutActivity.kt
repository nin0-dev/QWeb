package com.nin0dev.qweb

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LoggedOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_out)
        val spLogin: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        val spEdit = spLogin.edit()
        spEdit.putBoolean("loggedIn", false)
        spEdit.apply()
        finish()
    }
}