package com.nin0dev.qweb

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature
import com.google.android.material.appbar.MaterialToolbar

class LoginActivity : AppCompatActivity() {
    var loggedIn = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        WebView.setWebContentsDebuggingEnabled(true)

        if(Build.VERSION.SDK_INT >= 29)
        {
            // Dark mode supported
            when (3) {
                1 ->  {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                3 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
        else
        {
            // Dark mode not supported
            when (1) {
                1 ->  {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

        val wv = findViewById<WebView>(R.id.wv)

        WebView.setWebContentsDebuggingEnabled(true)
        wv.addJavascriptInterface(ImageDLReceiver(this), "Android");

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(wv.settings, FORCE_DARK_ON)
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WebSettingsCompat.setForceDark(wv.settings, FORCE_DARK_OFF)
                }
            }
        }
        wv.settings.javaScriptEnabled = true
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if(url.indexOf("quora.com") == -1) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                    return false
                }
                if(url == "https://www.quora.com/")
                {
                    finish()
                    startActivity(Intent(applicationContext, LoggedInActivity::class.java))
                    return false
                }
                else
                {
                    return false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                    view?.loadUrl("javascript:setTimeout(function(){javascript:try{document.getElementsByClassName(\"q-flex qu-bg--red qu-alignItems--center spacing_log_header_main dom_annotate_site_header\")[0].style.display = \"none\"; Android.loggedIn();}catch(e){}}, 50)")

                super.onPageFinished(view, url)
            }

        }
        wv.loadUrl("https://www.quora.com/login?prevent_redirect=1")

        wv.settings.domStorageEnabled = true
    }
}