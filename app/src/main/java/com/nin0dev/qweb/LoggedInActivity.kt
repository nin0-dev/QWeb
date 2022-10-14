package com.nin0dev.qweb

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoggedInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)
        val spLogin: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        val spEdit = spLogin.edit()
        spEdit.putBoolean("loggedIn", true)
        spEdit.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}