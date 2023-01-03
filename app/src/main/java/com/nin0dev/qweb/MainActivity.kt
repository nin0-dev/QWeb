package com.nin0dev.qweb

import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Context
import android.content.DialogInterface
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
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    var filePath: ValueCallback<Array<Uri>>? = null;
    @SuppressLint("MissingInflatedId")

    override fun onBackPressed() {
        val wv = findViewById<WebView>(R.id.wv)
        if(wv.canGoBack())
        {
            wv.goBack()
        }
        else {
            finish()
        }
    }

    fun showDLButton()
    {
        this.runOnUiThread {
            val dlButton = findViewById<ExtendedFloatingActionButton>(R.id.dlButton)
            dlButton.visibility = VISIBLE
        }
    }

    fun hideDLButton()
    {
        this.runOnUiThread {
            val dlButton = findViewById<ExtendedFloatingActionButton>(R.id.dlButton)
            dlButton.visibility = GONE
        }
    }

    fun checkUpdates()
    {
        val queue = Volley.newRequestQueue(this)
        val url = "https://raw.githubusercontent.com/nin0-dev/QWeb/master/version.txt"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                if(response != "1")
                {
                    val madb = MaterialAlertDialogBuilder(this)
                    madb.setTitle("Update available for QWeb")
                    madb.setPositiveButton("Update", DialogInterface.OnClickListener { dialogInterface, i ->
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nin0-dev/QWeb/releases/latest/download/QWeb.apk"))
                        Toast.makeText(this, "Accept any warnings for the install.", Toast.LENGTH_LONG).show()
                        startActivity(browserIntent)
                    })
                    madb.setNegativeButton("Later", DialogInterface.OnClickListener { dialogInterface, i ->  })
                    madb.setOnCancelListener {
                        checkUpdates()
                    }
                    madb.show()
                }
            },
            { })
        stringRequest.setShouldCache(false);
        queue.add(stringRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spLogin = getSharedPreferences("login", Context.MODE_PRIVATE)
        if(!spLogin.getBoolean("loggedIn", false))
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        WebView.setWebContentsDebuggingEnabled(true)
        val spTheme = getSharedPreferences("theme", Context.MODE_PRIVATE)
        val spPrivacy = getSharedPreferences("privacy", Context.MODE_PRIVATE)

        if(spPrivacy.getString("pinCode", "") != "" || spPrivacy.getBoolean("biometricUnlock", false)) {
            if(!intent.getBooleanExtra("authorized", false))
            {
                finish()
                startActivity(Intent(this, PinActivity::class.java))
            }
        }

        findViewById<MaterialToolbar>(R.id.materialToolbar2).setOnClickListener {
            val wv = findViewById<WebView>(R.id.wv)
            wv.loadUrl("https://quora.com?prevent_redirect=1")
        }
        if(Build.VERSION.SDK_INT >= 29)
        {
            // Dark mode supported
            when (spTheme.getInt("theme", 3)) {
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
            when (spTheme.getInt("theme", 1)) {
                1 ->  {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

        val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
        val wv = findViewById<WebView>(R.id.wv)
        val dlButton = findViewById<ExtendedFloatingActionButton>(R.id.dlButton)
        dlButton.setOnClickListener {
            wv.loadUrl("javascript:Android.downloadImage(document.getElementsByClassName(\"pswp__img\")[1].src)")
        }

        WebView.setWebContentsDebuggingEnabled(true)
        wv.addJavascriptInterface(ImageDLReceiver(this), "Android");
        appbar.setOnMenuItemClickListener {
            when (it.getItemId()) {
                R.id.search -> {
                    wv.loadUrl("javascript:document.getElementsByClassName(\"CssComponent__CssInlineComponent-sc-1oskqb9-1 Icon___StyledCssInlineComponent-sc-11tmcw7-0\")[0].click()")
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            wv.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName(\"q-flex qu-alignItems--center qu-borderRadius--small qu-borderColor--gray qu-px--small qu-mr--small qu-color--white\")[0].style.backgroundColor = \"#404040\"; document.getElementsByClassName(\"q-input qu-fontSize--small qu-lineHeight--regular qu-px--small qu-mr--small qu-color--white TextInput___StyledInput-sc-9srrla-0 eBgXUH MobileLookupOverlay___StyledTextInput-ypvaz0-0 indRYL\")[0].style.backgroundColor = \"#404040\"; document.getElementsByClassName(\"q-flex qu-py--small\")[0].style.backgroundColor = \"#202020\";}, 0)")
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        appbar.subtitle = "Home"
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
                if(url == wv.url)
                {
                    return true
                }
                if(url == "https://www.quora.com/")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Home"
                    return false
                }
                if(url == "https://www.quora.com/?prevent_redirect=1")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Home"
                    return false
                }
                else if(url == "https://www.quora.com/settings")
                {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    return true
                }
                else if(url == wv.url)
                {
                    wv.reload()
                    return true
                }
                else if(url == "https://www.quora.com/following")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Following"
                    return false
                }
                else if(url == "https://www.quora.com/answer")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Answer"
                    return false
                }
                else if(url == "https://www.quora.com/spaces")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Spaces"
                    return false
                }
                else if(url == "https://www.quora.com/notifications")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Notifications"
                    return false
                }
                else
                {
                    startActivity(Intent(applicationContext, SeparateActivity::class.java).putExtra("url", url))
                    return true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (url != null) {
                    if(url.indexOf("quora.com") == -1) {
                        view?.goBack()
                    }
                }
               // view?.loadUrl("javascript:try{document.getElementsByClassName(\"q-relative qu-borderRadius--small qu-bg--white qu-borderAll qu-borderWidth--regular qu-display--flex qu-p--small qu-alignItems--center\")[1].style.visibility = \"none\"; Android.loggedOut()}catch(e){}")
               // view?.loadUrl("javascript:try{document.getElementsByClassName(\"q-relative qu-borderRadius--small qu-bg--white qu-borderAll qu-borderWidth--regular qu-display--flex qu-p--small qu-alignItems--center\")[1].style.visibility = \"none\"; Android.loggedOut()}catch(e){}")

                if(url == "https://www.quora.com/")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Home"
                }
                else if(url == "https://www.quora.com/following")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Following"
                }
                else if(url == "https://www.quora.com/answer")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Answer"
                }
                else if(url == "https://www.quora.com/spaces")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Spaces"
                }
                else if(url == "https://www.quora.com/notifications")
                {
                    val appbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)
                    appbar.subtitle = "Notifications"
                }
                val sp = getSharedPreferences("tweaks", Context.MODE_PRIVATE)
                if(sp.getBoolean("adBlock", true))
                {
                    view?.loadUrl("javascript:let block_counter = 0;\tsetInterval(() => {\t\tlet elems;\tlet contents;\tlet testString;\telems = document.querySelectorAll(\".qu-borderTop, .qu-pt--medium, .qu-bg--white\");\t \tfor (let e of elems) \t{\t\tcontents = e.innerHTML;\t\t\t\ttestString = \">Sponsored by\";\t\t\t\tif (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found 'sponsored by'... \" + contents.indexOf(testString));\t\tif (contents.indexOf(testString) > -1 && contents.indexOf(testString) < 1000 && e.style.display!=\"none\") \t\t{\t\t\te.style.display = \"none\";\t\t\te.style.backgroundColor=\"blue\";\t\t\tblock_counter += 1;\t\t\tconsole.log(\"hiding a sponsored by post (spot2)\" + contents.indexOf(testString));\t\t\tconsole.log(e.textContent); console.log(\"-----\");\t\t}\t\t\t\ttestString = \">Promoted by\";\t\t\t\tif (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found 'promoted by'... \" + contents.indexOf(testString));\t\tif (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 1000 && e.style.display!=\"none\")\t\t{\t\t\te.style.display = \"none\";\t\t\te.style.backgroundColor=\"yellow\";\t\t\tblock_counter += 1;\t\t\tconsole.log(\"hiding a promoted by post (spot2) \" + contents.indexOf(testString));\t\t\tconsole.log(e.textContent); console.log(\"-----\");\t\t}\t\t\t\t\t\t\ttestString = \">Ad by\";\t\t\tif (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found 'ad by'... \" + contents.indexOf(testString));\t\tif (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 1000 && e.style.display!=\"none\") \t\t{\t\t\te.style.display = \"none\";\t\t\te.style.backgroundColor=\"pink\";\t\t\tblock_counter += 1;\t\t\tconsole.log(\"hiding an 'ad by' post (spot2) \" + contents.indexOf(testString));\t\t\tconsole.log(e.textContent); console.log(\"-----\");\t\t}\t\t\t\ttestString = \">Sponsored\";\t\tif (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found sponsored... \" + contents.indexOf(testString));\t\tif (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 6000 && e.style.display!=\"none\")\t\t{\t\t\te.style.display = \"none\";\t\t\te.style.backgroundColor=\"orange\";\t\t\tblock_counter += 1;\t\t\tconsole.log(\"hiding a sponsored post (spot1) \" + contents.indexOf(testString));\t\t\tconsole.log(e.textContent); console.log(\"-----\");\t\t}\t\ttestString = \">Promoted\";\t\tif (contents.indexOf(testString)>-1 && e.style.display!=\"none\") console.log(\"Found promoted... \" + contents.indexOf(testString));\t\tif (contents.indexOf(testString) > -1  && contents.indexOf(testString) < 6000 && e.style.display!=\"none\")\t\t{\t\t\te.style.display = \"none\";\t\t\te.style.backgroundColor=\"blue\";\t\t\tblock_counter += 1;\t\t\tconsole.log(\"hiding a promoted post (spot1) \" + contents.indexOf(testString));\t\t\tconsole.log(e.textContent); console.log(\"-----\");\t\t}\t\t\t\t\t\t\t}  \t\t\telems = document.getElementsByTagName(\"iframe\");\tfor (let e of elems) \t{\t\tif (e.id.indexOf('google_ads_iframe_') > -1 && e.style.display!=\"none\") \t\t{\t\t\tconsole.log(\"hiding a google iframe\");\t\t\te.style.display = \"none\";\t\t\tblock_counter += 1;\t\t}\t}\t\t\telems = document.getElementsByClassName(\"content-monetization-wall\");\tfor (let e of elems) \t{\t\tif (e.style.display!=\"none\") \t\t{\t\t\tconsole.log(\"hiding a premium answer (might not work correctly)...\");\t\t\te.style.display = \"none\";\t\t\te.style.backgroundColor=\"orange\";\t\t\tblock_counter += 1;\t\t}\t}}, 300);")
                }
                if(sp.getBoolean("oldProfile", true))
                {
                    view?.loadUrl("javascript:setInterval(function(){try{document.getElementsByClassName(\"q-box qu-py--tiny qu-borderBottom\")[0].innerHTML = '<div class=\"q-box\" style=\"box-sizing: border-box;\"></div><a class=\"q-box qu-display--block qu-cursor--pointer Link___StyledBox-t2xg9c-0 lpjmgU\" target=\"_top\" href=\"https://www.quora.com/messages\" style=\"box-sizing: border-box; border-radius: inherit;\"><div class=\"q-box qu-px--medium qu-tapHighlight--none\" style=\"box-sizing: border-box; position: relative; transition-property: background-color; transition-duration: 180ms; transition-timing-function: ease-in-out;\"><div class=\"q-flex qu-alignItems--center qu-py--small qu-overflow--hidden\" style=\"box-sizing: border-box; display: flex; min-height: 44px;\"><div class=\"q-box qu-flex--none qu-display--inline-flex qu-mr--small\" style=\"box-sizing: border-box;\"><span class=\"q-inlineBlock\" width=\"28px\" name=\"Chat\" style=\"box-sizing: border-box; display: inline-block; width: 28px; height: 28px; flex-shrink: 0; line-height: 28px;\"><span class=\"CssComponent__CssInlineComponent-sc-1oskqb9-1 Icon___StyledCssInlineComponent-sc-11tmcw7-0  eaXQVd\"><svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"none\" fill-rule=\"evenodd\"><path d=\"M7 4.5h8a3 3 0 0 1 3 3v6a3 3 0 0 1-3 3h-3l-3.5 4v-4H7a3 3 0 0 1-3-3v-6a3 3 0 0 1 3-3Zm13 8a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1h-2v2l-2-2h-2\" class=\"icon_svg-stroke\" stroke-width=\"1.5\" stroke=\"#666\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path><g class=\"icon_svg-fill_as_stroke\" fill=\"#666\"><circle cx=\"8\" cy=\"10.5\" r=\"1\"></circle><circle cx=\"11\" cy=\"10.5\" r=\"1\"></circle><circle cx=\"14\" cy=\"10.5\" r=\"1\"></circle></g></g></svg></span></span></div><div class=\"q-box qu-flex--auto qu-overflow--hidden\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-color--gray_dark\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-dynamicFontSize--regular\" style=\"box-sizing: border-box;\">Messages</div></div></div></div></div></a><div class=\"q-click-wrapper qu-display--block qu-tapHighlight--white qu-cursor--pointer ClickWrapper___StyledClickWrapperBox-zoqi4f-0 daLTSH\" tabindex=\"0\" style=\"box-sizing: border-box; font: inherit; padding: 0px; color: inherit; text-align: inherit;\"><div class=\"q-box qu-px--medium qu-tapHighlight--none\" style=\"box-sizing: border-box; position: relative; transition-property: background-color; transition-duration: 180ms; transition-timing-function: ease-in-out;\"><div class=\"q-flex qu-alignItems--center qu-py--small qu-overflow--hidden\" style=\"box-sizing: border-box; display: flex; min-height: 44px;\"><div class=\"q-box qu-flex--none qu-display--inline-flex qu-mr--small\" style=\"box-sizing: border-box;\"><span class=\"q-inlineBlock\" width=\"28px\" name=\"Money\" style=\"box-sizing: border-box; display: inline-block; width: 28px; height: 28px; flex-shrink: 0; line-height: 28px;\"><span class=\"CssComponent__CssInlineComponent-sc-1oskqb9-1 Icon___StyledCssInlineComponent-sc-11tmcw7-0  eaXQVd\"><svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M11.5 4v16m3.75-13H9.625C8.175 7 7 8.12 7 9.5S8.175 12 9.625 12h3.75C14.825 12 16 13.12 16 14.5S14.825 17 13.375 17H7\" class=\"icon_svg-stroke\" stroke=\"#666\" stroke-width=\"1.5\" fill=\"none\" fill-rule=\"evenodd\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg></span></span></div><div class=\"q-box qu-flex--auto qu-overflow--hidden\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-color--gray_dark\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-dynamicFontSize--regular\" style=\"box-sizing: border-box;\">Monetization</div></div></div></div></div></div><a class=\"q-box qu-display--block qu-cursor--pointer Link___StyledBox-t2xg9c-0 lpjmgU\" target=\"_top\" href=\"https://www.quora.com/stats\" style=\"box-sizing: border-box; border-radius: inherit;\"><div class=\"q-box qu-px--medium qu-tapHighlight--none\" style=\"box-sizing: border-box; position: relative; transition-property: background-color; transition-duration: 180ms; transition-timing-function: ease-in-out;\"><div class=\"q-flex qu-alignItems--center qu-py--small qu-overflow--hidden\" style=\"box-sizing: border-box; display: flex; min-height: 44px;\"><div class=\"q-box qu-flex--none qu-display--inline-flex qu-mr--small\" style=\"box-sizing: border-box;\"><span class=\"q-inlineBlock\" width=\"28px\" name=\"Stats\" style=\"box-sizing: border-box; display: inline-block; width: 28px; height: 28px; flex-shrink: 0; line-height: 28px;\"><span class=\"CssComponent__CssInlineComponent-sc-1oskqb9-1 Icon___StyledCssInlineComponent-sc-11tmcw7-0  eaXQVd\"><svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M5 12h3v8H5v-8Zm5.5-8h3v16h-3V4ZM16 7h3v13h-3V7Z\" class=\"icon_svg-stroke icon_svg-fill\" stroke-width=\"1.5\" stroke=\"#666\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg></span></span></div><div class=\"q-box qu-flex--auto qu-overflow--hidden\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-color--gray_dark\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-dynamicFontSize--regular\" style=\"box-sizing: border-box;\">Your content &amp; stats</div></div></div></div></div></a><a class=\"q-box qu-display--block qu-cursor--pointer Link___StyledBox-t2xg9c-0 lpjmgU\" target=\"_top\" href=\"https://www.quora.com/bookmarks\" style=\"box-sizing: border-box; border-radius: inherit;\"><div class=\"q-box qu-px--medium qu-tapHighlight--none\" style=\"box-sizing: border-box; position: relative; transition-property: background-color; transition-duration: 180ms; transition-timing-function: ease-in-out;\"><div class=\"q-flex qu-alignItems--center qu-py--small qu-overflow--hidden\" style=\"box-sizing: border-box; display: flex; min-height: 44px;\"><div class=\"q-box qu-flex--none qu-display--inline-flex qu-mr--small\" style=\"box-sizing: border-box;\"><span class=\"q-inlineBlock\" width=\"28px\" name=\"Bookmarks\" style=\"box-sizing: border-box; display: inline-block; width: 28px; height: 28px; flex-shrink: 0; line-height: 28px;\"><span class=\"CssComponent__CssInlineComponent-sc-1oskqb9-1 Icon___StyledCssInlineComponent-sc-11tmcw7-0  eaXQVd\"><svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\"><g class=\"icon_svg-stroke\" stroke-width=\"1.5\" stroke=\"#666\" fill=\"none\" fill-rule=\"evenodd\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path class=\"icon_svg-fill\" d=\"m10.501 16-5.499 4L5 8h11v12z\"></path><path d=\"M8 5.923V5h11v12l-.997-.725\"></path></g></svg></span></span></div><div class=\"q-box qu-flex--auto qu-overflow--hidden\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-color--gray_dark\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-dynamicFontSize--regular\" style=\"box-sizing: border-box;\">Bookmarks</div></div></div></div></div></a><a class=\"q-box qu-display--block qu-cursor--pointer Link___StyledBox-t2xg9c-0 lpjmgU\" target=\"_top\" href=\"https://www.quora.com/drafts\" style=\"box-sizing: border-box; border-radius: inherit;\"><div class=\"q-box qu-px--medium qu-tapHighlight--none\" style=\"box-sizing: border-box; position: relative; transition-property: background-color; transition-duration: 180ms; transition-timing-function: ease-in-out;\"><div class=\"q-flex qu-alignItems--center qu-py--small qu-overflow--hidden\" style=\"box-sizing: border-box; display: flex; min-height: 44px;\"><div class=\"q-box qu-flex--none qu-display--inline-flex qu-mr--small\" style=\"box-sizing: border-box;\"><span class=\"q-inlineBlock\" width=\"28px\" name=\"Draft\" style=\"box-sizing: border-box; display: inline-block; width: 28px; height: 28px; flex-shrink: 0; line-height: 28px;\"><span class=\"CssComponent__CssInlineComponent-sc-1oskqb9-1 Icon___StyledCssInlineComponent-sc-11tmcw7-0  eaXQVd\"><svg width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M20.743 10.757h0a1.5 1.5 0 0 1 0 2.122l-5.728 5.727-2.756.638.635-2.76 5.727-5.727a1.5 1.5 0 0 1 2.122 0Zm-3.182 1.061 2.121 2.121M9 19H5V5h13v3M8 9h7m-7 3h5.5M8 15h2.5\" class=\"icon_svg-stroke\" stroke-width=\"1.5\" stroke=\"#666\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\"></path></svg></span></span></div><div class=\"q-box qu-flex--auto qu-overflow--hidden\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-color--gray_dark\" style=\"box-sizing: border-box;\"><div class=\"q-text qu-dynamicFontSize--regular\" style=\"box-sizing: border-box;\">Drafts</div></div></div><div class=\"q-box qu-flex--none qu-display--inline-flex\" style=\"box-sizing: border-box; margin-top: 0px;\"><div class=\"q-text qu-dynamicFontSize--small qu-color--gray_light\" style=\"box-sizing: border-box;\"></div></div></div></div></a>'}catch(error){}}, 1)")
                }

                view?.loadUrl("javascript:setInterval(function(){try {document.getElementsByClassName(\"q-box qu-borderRadius--small qu-overflow--hidden\")[0].style.display=\"none\";document.getElementsByClassName(\"q-text qu-ellipsis qu-whiteSpace--nowrap\")[0].click();window.location.href = \"https://www.quora.com/settings\";}catch(error) {}}, 1)")
                view?.loadUrl("javascript:setInterval(function(){try{if(latestURL != document.getElementsByClassName(\"pswp__img\")[1].src){Android.showDLButton();latestURL = document.getElementsByClassName(\"pswp__img\")[1].src;}} catch(error) {latestURL = \"\"; Android.hideDLButton()}}, 1);")
                view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName('q-flex qu-bg--red qu-alignItems--center spacing_log_header_main dom_annotate_site_header')[0].style.display = 'none'}, 0)")
                view?.loadUrl("javascript:setTimeout(function(){document.getElementsByClassName(\"q-click-wrapper qu-flex--1 qu-display--block qu-color--gray qu-tapHighlight--white qu-cursor--pointer\")[1].remove()}, 150)")
                super.onPageFinished(view, url)
            }
        }

        wv.webChromeClient = FileUploadClient.MyWebChromeClient(this)
        wv.loadUrl("https://quora.com?prevent_redirect=1")

        wv.settings.domStorageEnabled = true
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