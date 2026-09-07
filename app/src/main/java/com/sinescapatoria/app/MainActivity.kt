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

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(
                view: WebView,
                url: String
            ) {
                super.onPageFinished(view, url)

                view.evaluateJavascript(
                    """
                    (function () {

                        const REMOTE =
                            'https://sin-escapatoria.onrender.com';

                        const originalFetch =
                            window.fetch.bind(window);


                        /*
                         * ==========================================
                         * UUID COMPATIBLE CON ANDROID WEBVIEW
                         * ==========================================
                         */

                        try {

                            if (
                                window.crypto &&
                                !window.crypto.randomUUID
                            ) {

                                Object.defineProperty(
                                    window.crypto,
                                    'randomUUID',
                                    {
                                        configurable: true,
                                        value: function () {

                                            const bytes =
                                                new Uint8Array(16);

                                            if (
                                                window.crypto
                                                    .getRandomValues
                                            ) {

                                                window.crypto
                                                    .getRandomValues(
                                                        bytes
                                                    );

                                            } else {

                                                for (
                                                    let i = 0;
                                                    i < 16;
                                                    i++
                                                ) {
                                                    bytes[i] =
                                                        Math.floor(
                                                            Math.random() *
                                                            256
                                                        );
                                                }
                                            }

                                            bytes[6] =
                                                (bytes[6] & 0x0f) |
                                                0x40;

                                            bytes[8] =
                                                (bytes[8] & 0x3f) |
                                                0x80;

                                            const h =
                                                Array.from(
                                                    bytes,
                                                    function (b) {
                                                        return b
                                                            .toString(16)
                                                            .padStart(
                                                                2,
                                                                '0'
                                                            );
                                                    }
                                                );

                                            return (
                                                h.slice(0, 4).join('') +
                                                '-' +
                                                h.slice(4, 6).join('') +
                                                '-' +
                                                h.slice(6, 8).join('') +
                                                '-' +
                                                h.slice(8, 10).join('') +
                                                '-' +
                                                h.slice(10, 16).join('')
                                            );
                                        }
                                    }
                                );
                            }

                        } catch (e) {

                            console.log(
                                'UUID fallback:',
                                e
                            );
                        }


                        /*
                         * ==========================================
                         * REDIRECCIÓN DE PETICIONES
                         * ==========================================
                         */

                        window.fetch = function (
                            input,
                            init
                        ) {

                            try {

                                const u =
                                    typeof input === 'string'
                                        ? input
                                        : (
                                            input &&
                                            input.url
                                        ) || '';


                                /*
                                 * cards.json
                                 */

                                if (
                                    u.indexOf('cards.json') !== -1 &&
                                    u.indexOf('onrender.com') === -1
                                ) {

                                    return originalFetch(
                                        REMOTE +
                                        '/cards.json?x=' +
                                        Date.now(),
                                        init
                                    );
                                }


                                /*
                                 * API RELATIVA
                                 */

                                if (
                                    u.indexOf('/api/') === 0
                                ) {

                                    return originalFetch(
                                        REMOTE + u,
                                        init
                                    );
                                }


                                /*
                                 * API desde file://
                                 */

                                if (
                                    u.indexOf('file:///api/') === 0
                                ) {

                                    const path =
                                        u.replace(
                                            'file://',
                                            ''
                                        );

                                    return originalFetch(
                                        REMOTE + path,
                                        init
                                    );
                                }

                            } catch (e) {

                                console.log(
                                    'Fetch Android:',
                                    e
                                );
                            }


                            return originalFetch(
                                input,
                                init
                            );
                        };


                        /*
                         * ==========================================
                         * RECUPERAR NOMBRES
                         * ==========================================
                         */

                        const savedA =
                            localStorage.getItem(
                                'se_name_A'
                            ) || '';

                        const savedB =
                            localStorage.getItem(
                                'se_name_B'
                            ) || '';


                        /*
                         * ==========================================
                         * RESTAURAR PERFIL Y JUGADOR
                         * ==========================================
                         */

                        try {

                            if (!profile) {
                                profile = 'A';
                            }

                            if (
                                !playerName &&
                                savedA
                            ) {
                                playerName = savedA;
                            }

                        } catch (e) {

                            console.log(
                                'Perfil Android:',
                                e
                            );
                        }


                        /*
                         * ==========================================
                         * ELEMENTOS
                         * ==========================================
                         */

                        const area =
                            document.getElementById(
                                'nameArea'
                            );

                        const note =
                            document.getElementById(
                                'profileNote'
                            );

                        const name =
                            document.getElementById(
                                'name'
                            );


                        /*
                         * ==========================================
                         * SI NO HAY NOMBRE
                         * ==========================================
                         */

                        if (
                            area &&
                            !savedA
                        ) {

                            area.classList.remove(
                                'hidden'
                            );

                            if (name) {
                                name.value = '';
                            }

                            if (note) {
                                note.textContent =
                                    'Escribe tu nombre para comenzar.';
                            }
                        }


                        /*
                         * ==========================================
                         * PERFIL A
                         * ==========================================
                         */

                        if (savedA) {

                            const btnA =
                                document.getElementById(
                                    'aBtn'
                                );

                            if (btnA) {

                                btnA.innerHTML =
                                    '<b>' +
                                    savedA.replace(
                                        /[&<>"']/g,
                                        ''
                                    ) +
                                    '</b>' +
                                    '<span>Seleccionado</span>';
                            }

                            if (note) {

                                note.textContent =
                                    'Perfil: ' +
                                    savedA;
                            }
                        }


                        /*
                         * ==========================================
                         * PERFIL B
                         * ==========================================
                         */

                        if (savedB) {

                            const btnB =
                                document.getElementById(
                                    'bBtn'
                                );

                            if (btnB) {

                                btnB.innerHTML =
                                    '<b>' +
                                    savedB.replace(
                                        /[&<>"']/g,
                                        ''
                                    ) +
                                    '</b>' +
                                    '<span>Seleccionado</span>';
                            }
                        }

                    })();
                    """.trimIndent(),
                    null
                )
            }
        }

        setContentView(webView)

        loadGame(intent?.data)
    }


    private fun loadGame(uri: Uri?) {

        val invite =
            uri
                ?.takeIf {
                    it.scheme == "sinescapatoria" &&
                    it.host == "invite"
                }
                ?.getQueryParameter("invite")
                ?.trim()

        val target =
            if (!invite.isNullOrEmpty()) {

                "file:///android_asset/index.html" +
                    "?invite=" +
                    Uri.encode(invite)

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
