package com.sinescapatoria.app

import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                view.evaluateJavascript(
                    """
                    (function () {
                        const REMOTE = 'https://sin-escapatoria.onrender.com';
                        const originalFetch = window.fetch.bind(window);

                        window.fetch = function (input, init) {
                            try {
                                const u = typeof input === 'string'
                                    ? input
                                    : ((input && input.url) || '');

                                if (
                                    u.indexOf('cards.json') !== -1 &&
                                    u.indexOf('onrender.com') === -1
                                ) {
                                    return originalFetch(
                                        REMOTE + '/cards.json?x=' + Date.now(),
                                        init
                                    );
                                }
                            } catch (e) {}

                            return originalFetch(input, init);
                        };

                        const savedA =
                            localStorage.getItem('se_name_A') || '';

                        const savedB =
                            localStorage.getItem('se_name_B') || '';

                        /*
                         * IMPORTANTE:
                         * index.html utiliza las variables let
                         * "profile" y "playerName".
                         * No sirve modificar window.profile/window.playerName.
                         */
                        try {
                            if (!profile) {
                                profile = 'A';
                            }

                            if (!playerName && savedA) {
                                playerName = savedA;
                            }
                        } catch (e) {}

                        const area =
                            document.getElementById('nameArea');

                        const note =
                            document.getElementById('profileNote');

                        const name =
                            document.getElementById('name');

                        if (area && !savedA) {
                            area.classList.remove('hidden');

                            if (name) {
                                name.value = '';
                                name.focus();
                            }

                            if (note) {
                                note.textContent =
                                    'Escribe tu nombre para comenzar.';
                            }
                        }

                        if (savedA) {
                            const btn =
                                document.getElementById('aBtn');

                            if (btn) {
                                btn.innerHTML =
                                    '<b>' +
                                    savedA.replace(
                                        /[&<>"']/g,
                                        ''
                                    ) +
                                    '</b><span>Seleccionado</span>';
                            }

                            if (note) {
                                note.textContent =
                                    'Perfil: ' + savedA;
                            }
                        }

                        if (savedB) {
                            const btn =
                                document.getElementById('bBtn');

                            if (btn) {
                                btn.innerHTML =
                                    '<b>' +
                                    savedB.replace(
                                        /[&<>"']/g,
                                        ''
                                    ) +
                                    '</b><span>Seleccionado</span>';
                            }
                        }
                    })();
                    """.trimIndent(),
                    null
                )
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true

            allowFileAccess = true
            allowContentAccess = true

            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true

            cacheMode = WebSettings.LOAD_DEFAULT
        }

        setContentView(webView)

        loadGame(intent?.data)
    }

    private fun loadGame(uri: Uri?) {

        val invite = uri
            ?.takeIf {
                it.scheme == "sinescapatoria" &&
                it.host == "invite"
            }
            ?.getQueryParameter("invite")
            ?.trim()

        val target =
            if (!invite.isNullOrEmpty()) {
                "file:///android_asset/index.html?invite=${Uri.encode(invite)}"
            } else {
                "file:///android_asset/index.html"
            }

        webView.loadUrl(target)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
