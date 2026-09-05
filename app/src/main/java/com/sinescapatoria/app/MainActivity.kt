package com.sinescapatoria.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true

        webView.webViewClient = WebViewClient()

        setContentView(webView)

        webView.loadUrl("https://sin-escapatoria.onrender.com/")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.let { uri: Uri ->
            if (uri.scheme == "sinescapatoria" &&
                uri.host == "invite") {

                val invite = uri.getQueryParameter("invite")

                if (!invite.isNullOrEmpty()) {
                    webView.loadUrl(
                        "https://sin-escapatoria.onrender.com/?invite=$invite"
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
