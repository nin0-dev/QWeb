package com.nin0dev.qweb

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class SeparateActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    var filePath: ValueCallback<Array<Uri>>? = null;

    override fun onBackPressed() {
        val wv = findViewById<WebView>(R.id.wv2)
        if(wv.canGoBack())
        {
            wv.goBack()
        }
        else
        {
            finish()
        }
    }

    fun showDLButton()
    {
        this.runOnUiThread {
            val dlButton = findViewById<ExtendedFloatingActionButton>(R.id.dlButton2)
            dlButton.visibility = VISIBLE
        }
    }

    fun hideDLButton()
    {
        this.runOnUiThread {
            val dlButton = findViewById<ExtendedFloatingActionButton>(R.id.dlButton2)
            dlButton.visibility = GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_separate)
        WebView.setWebContentsDebuggingEnabled(true)
        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
        val wv = findViewById<WebView>(R.id.wv2)
        wv.addJavascriptInterface(ImageDLReceiver(this), "Android")
        findViewById<MaterialToolbar>(R.id.materialToolbar).setOnClickListener {
            wv.loadUrl("javascript:document.getElementsByClassName(\"q-box qu-color--white qu-cursor--pointer\")[0].click()")
        }
        appbar.setOnMenuItemClickListener {
            when (it.getItemId()) {
                R.id.share ->
                {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, wv.url)
                        type = "text/uri-list"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        val dlButton = findViewById<ExtendedFloatingActionButton>(R.id.dlButton2)
        dlButton.setOnClickListener {
            wv.loadUrl("javascript:Android.downloadImage2(document.getElementsByClassName(\"pswp__img\")[1].src)")
        }
        appbar.setNavigationOnClickListener {
            val wv = findViewById<WebView>(R.id.wv2)
            if(wv.canGoBack())
            {
                wv.goBack()
            }
            else
            {
                finish()
            }
        }
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(wv.settings, WebSettingsCompat.FORCE_DARK_ON)
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WebSettingsCompat.setForceDark(wv.settings, WebSettingsCompat.FORCE_DARK_OFF)
                }
            }
        }
        wv.settings.javaScriptEnabled = true
        intent.getStringExtra("url")?.let { wv.loadUrl(it) }
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if(url.indexOf("quora.com") == -1)
                {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                    return true
                }
                else
                {
                    return managePagesSubtitle(url)
                }
            }

            fun managePagesSubtitle(url : String) : Boolean
            {
                Log.d("url", url)

                if(url.indexOf("https://www.quora.com/messages/thread/") != -1)
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                    val wv = findViewById<WebView>(R.id.wv2)
                    return false
                }
                else if(url == ("https://www.quora.com/messages/"))
                {
                    finish()
                    return true
                }
                else if(url.indexOf("https://www.quora.com") == -1)
                {
                    val adminModDashboardRegex = Regex("^https:\\/\\/.+\\.quora\\.com\\/(people|earnings|queue|settings|admin_log|submissions|stats)\\?*.*\$")
                    if(adminModDashboardRegex.containsMatchIn(url))
                    {
                        return false
                    }
                    else
                    {
                        startActivity(Intent(applicationContext, SeparateActivity::class.java).putExtra("url", url))
                        return true
                    }
                }
                else
                {
                    startActivity(Intent(applicationContext, SeparateActivity::class.java).putExtra("url", url))
                    return true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (url != null) {
                    if (url.indexOf("quora.com") == -1) {
                        view?.goBack()
                    }
                }

                view?.evaluateJavascript("(function() { return 'this'; })();",
                    ValueCallback<String?> { s ->
                        Log.d("LogName", s!!) // Prints: "this"

                    })

                view?.loadUrl("javascript:try{document.getElementsByClassName(\"q-relative qu-borderRadius--small qu-bg--white qu-borderAll qu-borderWidth--regular qu-display--flex qu-p--small qu-alignItems--center\")[1].style.visibility = \"none\"; Android.loggedOut2()}catch(e){}")
                val sp = getSharedPreferences("tweaks", Context.MODE_PRIVATE)
                if(sp.getBoolean("adBlock", true))
                {
                    view?.loadUrl("javascript:setInterval(() => {let elems;let contents;let testString;elems = document.querySelectorAll(\".qu-borderTop, .qu-pt--medium, .qu-bg--white, .q-box.qu-hover--bg--darken\"); for (let e of elems) {contents = e.innerHTML;testString = \">Sponsored by\";if (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found 'sponsored by'... \" + contents.indexOf(testString));if (contents.indexOf(testString) > -1 && contents.indexOf(testString) < 1000 && e.style.display!=\"none\") // note, searching for part of the html tag (\">\") to eliminate false positives{e.style.display = \"none\";e.style.backgroundColor=\"blue\";}testString = \">Promoted by\";if (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found 'promoted by'... \" + contents.indexOf(testString));if (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 1000 && e.style.display!=\"none\"){e.style.display = \"none\";e.style.backgroundColor=\"yellow\";}// https://www.quora.com/unanswered/Who-was-the-least-wealthy-man-to-become-the-president-of-the-USAtestString = \">Ad by\";if (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found 'ad by'... \" + contents.indexOf(testString));if (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 1000 && e.style.display!=\"none\") {e.style.display = \"none\";e.style.backgroundColor=\"pink\";}testString = \">Sponsored\";if (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found sponsored... \" + contents.indexOf(testString));if (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 6000 && e.style.display!=\"none\"){e.style.display = \"none\";e.style.backgroundColor=\"orange\";}testString = \">Promoted\";if (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found promoted... \" + contents.indexOf(testString));if (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 6000 && e.style.display!=\"none\"){e.style.display = \"none\";e.style.backgroundColor=\"blue\";}}elems = document.getElementsByTagName(\"iframe\");for (let e of elems) {if (e.id.indexOf('google_ads_iframe_') > -1 && e.style.display!=\"none\") {e.style.display = \"none\";}}elems = document.getElementsByClassName(\"content-monetization-wall\");for (let e of elems) {if (e.style.display!=\"none\") {e.style.display = \"none\";e.style.backgroundColor=\"orange\";}}}, 300);")
                }
                view?.loadUrl("javascript:setInterval(function(){try{if(latestURL != document.getElementsByClassName(\"pswp__img\")[1].src){Android.showDLButton2();latestURL = document.getElementsByClassName(\"pswp__img\")[1].src;}} catch(error) {latestURL = \"\"; Android.hideDLButton2()}}, 1);")
                if (url != null) {
                    if(url == "https://www.quora.com/messages")
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        appbar.title = "Messages"
                        appbar.subtitle = "Inbox"
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'; document.getElementsByClassName('q-box qu-borderBottom qu-px--medium qu-pt--small qu-pb--small qu-bg--white')[0].style.display = 'none'}, 0)")
                    }
                    if(url == "https://www.quora.com/bookmarks")
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        appbar.title = "Bookmarks"
                        appbar.subtitle = ""
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'; document.getElementsByClassName('q-box qu-borderBottom qu-px--medium qu-pt--small qu-pb--small qu-bg--raised')[0].style.display = 'none'}, 0)")
                    }
                    if(url == "https://www.quora.com/stats")
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        appbar.title = "Your content & stats"
                        appbar.subtitle = ""
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'; document.getElementsByClassName('q-box qu-pt--medium qu-pb--medium')[0].style.display = 'none'}, 0)")
                    }
                    else if(url.indexOf("https://www.quora.com/messages/thread/") != -1)
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
                        appbar.title = "Messages"
                        var sub = wv.title
                        var st = sub?.replace(" - Quora", "")
                        if (st != null) {
                            if(st.indexOf("(") == 0) {
                                st = st.removeRange(0, st.indexOf(" ") + 1)
                            }
                        }
                        appbar.subtitle = st
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'; document.getElementsByClassName('q-flex qu-flexDirection--column')[0].style.paddingTop = '0px'}, 0)")
                    }
                    else if(url.indexOf("https://www.quora.com/profile/") != -1)
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        appbar.title = "Profile"
                        var sub = wv.title
                        var st = sub?.replace(" - Quora", "")
                        if (st != null) {
                            if(st.indexOf("(") == 0) {
                                st = st.removeRange(0, st.indexOf(" ") + 1)
                            }
                        }
                        appbar.subtitle = st
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'}, 0)")

                    }
                    else if(url.indexOf("https://www.quora.com") == -1)
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        appbar.title = "Space"
                        appbar.subtitle = ""
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'}, 0)")
                    }
                    else
                    {
                        view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-sticky qu-zIndex--header')[0].style.display = 'none'}, 0)")
                    }
                    if(url.indexOf("https://www.quora.com/messages/thread/") != -1)
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        val appbar2 = findViewById<MaterialToolbar>(R.id.materialToolbar)
                        appbar.visibility = GONE
                        appbar2.visibility = VISIBLE

                        appbar2.setOnMenuItemClickListener {
                            when (it.getItemId()) {
                                R.id.dm_info -> {
                                    val wv = findViewById<WebView>(R.id.wv2)
                                    wv.loadUrl("javascript:document.getElementsByClassName('q-flex qu-alignItems--center qu-justifyContent--center')[1].click()")
                                    return@setOnMenuItemClickListener true
                                }
                                else -> {
                                    return@setOnMenuItemClickListener true
                                }
                            }
                        }

                        appbar.setNavigationOnClickListener {
                            val wv = findViewById<WebView>(R.id.wv2)
                            if(wv.canGoBack())
                            {
                                wv.goBack()
                            }
                            else
                            {
                                finish()
                            }
                        }
                        appbar2.setNavigationOnClickListener {
                            val wv = findViewById<WebView>(R.id.wv2)
                            if(wv.canGoBack())
                            {
                                wv.goBack()
                            }
                            else
                            {
                                finish()
                            }
                        }
                    }
                    else
                    {
                        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar3)
                        val appbar2 = findViewById<MaterialToolbar>(R.id.materialToolbar)
                        appbar.visibility = VISIBLE
                        appbar2.visibility = GONE
                        appbar.setNavigationOnClickListener {
                            val wv = findViewById<WebView>(R.id.wv2)
                            if(wv.canGoBack())
                            {
                                wv.goBack()
                            }
                            else
                            {
                                finish()
                            }
                        }
                    }
                    super.onPageFinished(view, url)
                }
            }
        }
        wv.webChromeClient = FileUploadClient.MyWebChromeClient2(this)
    }
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.dm_info) {
            val wv = findViewById<WebView>(R.id.wv2)
            wv.loadUrl("javascript:document.getElementsByClassName('q-flex qu-alignItems--center qu-justifyContent--center')[1].click()")
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) {
            filePath?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (filePath == null) return

            filePath!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            )
            filePath = null
        }
    }
}