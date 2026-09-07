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

                        const REMOTE =
                            'https://sin-escapatoria.onrender.com';

                        const originalFetch =
                            window.fetch.bind(window);
                                                    /*
                         * =====================================================
                         * COMPATIBILIDAD ANDROID WEBVIEW
                         * =====================================================
                         */

                        try {

                            if (!window.crypto) {
                                window.crypto = {};
                            }

                            if (!window.crypto.randomUUID) {

                                window.crypto.randomUUID = function () {

                                    const bytes =
                                        new Uint8Array(16);

                                    if (
                                        window.crypto.getRandomValues
                                    ) {

                                        window.crypto.getRandomValues(
                                            bytes
                                        );

                                    } else {

                                        for (
                                            let i = 0;
                                            i < bytes.length;
                                            i++
                                        ) {

                                            bytes[i] =
                                                Math.floor(
                                                    Math.random() * 256
                                                );
                                        }
                                    }
                                                                        /*
                                     * UUID v4
                                     */

                                    bytes[6] =
                                        (bytes[6] & 0x0f) | 0x40;

                                    bytes[8] =
                                        (bytes[8] & 0x3f) | 0x80;

                                    const h =
                                        Array.from(
                                            bytes,
                                            b =>
                                                b.toString(16)
                                                 .padStart(2, '0')
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
                                };
                            }

                        } catch (e) {

                            console.log(
                                'Compatibilidad UUID:',
                                e
                            );
                        }
                                                    } catch (e) {

                                console.log(
                                    'Error redirección fetch:',
                                    e
                                );
                            }

                            return originalFetch(
                                input,
                                init
                            );
                        };

                        /*
                         * =====================================================
                         * RECUPERAR NOMBRES GUARDADOS
                         * =====================================================
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
                         * =====================================================
                         * IMPORTANTE
                         *
                         * index.html utiliza:
                         *
                         * let profile
                         * let playerName
                         *
                         * Por eso hay que modificar esas variables
                         * directamente y NO window.profile.
                         * =====================================================
                         */

                        try {

                            if (!profile) {
                                profile = 'A';
                            }

                            if (
                                !playerName &&
                                savedA
                            ) {

                                playerName =
                                    savedA;
                            }

                        } catch (e) {

                            console.log(
                                'Error recuperando perfil:',
                                e
                            );
                        }
                                                /*
                         * =====================================================
                         * ELEMENTOS DE LA INTERFAZ
                         * =====================================================
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
                         * =====================================================
                         * SI NO EXISTE NOMBRE, MOSTRAR FORMULARIO
                         * =====================================================
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

                                name.focus();
                            }

                            if (note) {

                                note.textContent =
                                    'Escribe tu nombre para comenzar.';
                            }
                        }

                        /*
                         * =====================================================
                         * MOSTRAR PERFIL A
                         * =====================================================
                         */

                        if (savedA) {

                            const btn =
                                document.getElementById(
                                    'aBtn'
                                );

                            if (btn) {

                                btn.innerHTML =
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
                         * =====================================================
                         * MOSTRAR PERFIL B
                         * =====================================================
                         */

                        if (savedB) {

                            const btn =
                                document.getElementById(
                                    'bBtn'
                                );

                            if (btn) {

                                btn.innerHTML =
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
        };

        /*
         * =========================================================
         * CONFIGURACIÓN DEL WEBVIEW
         * =========================================================
         */

        webView.settings.apply {

            javaScriptEnabled = true

            domStorageEnabled = true

            databaseEnabled = true
                        allowFileAccess = true

            allowContentAccess = true

            allowFileAccessFromFileURLs = true

            allowUniversalAccessFromFileURLs = true

            cacheMode =
                WebSettings.LOAD_DEFAULT
        }

        /*
         * =========================================================
         * MOSTRAR WEBVIEW
         * =========================================================
         */

        setContentView(webView)

        /*
         * =========================================================
         * CARGAR JUEGO
         * =========================================================
         */

        loadGame(intent?.data)
    }

    /*
     * =============================================================
     * CARGAR JUEGO
     * =============================================================
     */

    private fun loadGame(uri: Uri?) {

        val invite =
            uri
                ?.takeIf {
                    it.scheme ==
                        "sinescapatoria" &&
                    it.host ==
                        "invite"
                }
                ?.getQueryParameter(
                    "invite"
                )
                ?.trim()
                        val target =

            if (
                !invite.isNullOrEmpty()
            ) {

                "file:///android_asset/index.html" +
                    "?invite=" +
                    Uri.encode(invite)

            } else {

                "file:///android_asset/index.html"
            }

        webView.loadUrl(target)
    }

    /*
     * =============================================================
     * BOTÓN ATRÁS DE ANDROID
     * =============================================================
     */

    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (
            webView.canGoBack()
        ) {

            webView.goBack()

        } else {

            super.onBackPressed()
        }
    }
}
    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (
            webView.canGoBack()
        ) {

            webView.goBack()

        } else {

            super.onBackPressed()
        }
    }
}
                                  
                    
