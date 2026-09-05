SIN ESCAPATORIA 5D — ANDROID

1. Instala Android Studio estable (Quail 4 / 2026.1.4 o posterior compatible).
2. Abre esta carpeta como proyecto.
3. Espera a que Gradle termine la sincronización.
4. Conecta un móvil Android con Depuración USB o usa el emulador.
5. Pulsa Run para probar.
6. Para generar APK: Build > Generate App Bundles or APKs > Generate APKs.
7. Para publicación en Google Play, genera un Android App Bundle (AAB) firmado de release.

SERVIDOR REMOTO
La app carga el HTML localmente, pero las partidas entre dos dispositivos usan Server.js por HTTPS.
Dentro de la app aparece “CONFIGURAR SERVIDOR”. Introduce, por ejemplo:
https://juego.tudominio.com

MODO LOCAL
No necesita servidor. Los dos jugadores pueden usar el mismo móvil.

INVITACIONES
La app usa el esquema sinescapatoria://invite?invite=XXXXXX para abrir una invitación directamente en la app instalada.

IMPORTANTE
No se ha inventado una URL de servidor: debes desplegar Server.js en un hosting HTTPS y poner esa dirección en la app.
