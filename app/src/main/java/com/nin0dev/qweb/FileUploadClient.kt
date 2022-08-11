package com.nin0dev.qweb

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class FileUploadClient {


    class MyWebChromeClient(val a: MainActivity) : WebChromeClient(){
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            a.filePath = filePathCallback

            val contentIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentIntent.type = "*/*"
            contentIntent.addCategory(Intent.CATEGORY_OPENABLE)

            a.startActivityForResult(contentIntent, 1)
            return true
        }
    }

    class MyWebChromeClient2(val a: SeparateActivity) : WebChromeClient(){
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            a.filePath = filePathCallback

            val contentIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentIntent.type = "*/*"
            contentIntent.addCategory(Intent.CATEGORY_OPENABLE)

            a.startActivityForResult(contentIntent, 1)
            return true
        }
    }
}