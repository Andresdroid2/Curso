package com.example.curso.PantillasUnidades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.curso.R

class UnidadVideoActivity : AppCompatActivity() {

    private lateinit var webViewVideo: WebView
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private lateinit var fullScreenContainer: FrameLayout
    private var currentVideoUrl: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidad_video)
        onBackPressedDispatcher.addCallback(this, backCallback)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        val txtTitulo = findViewById<TextView>(R.id.txtTitulo)
        val btnIrParcial = findViewById<Button>(R.id.btnIrParcial)
        val btnVolverIntro = findViewById<Button>(R.id.btnVolverIntro)
        webViewVideo = findViewById(R.id.webViewVideo)
        fullScreenContainer = findViewById(R.id.fullscreen_container)

        val titulo = intent.getStringExtra("titulo")
        val videoUrl = intent.getStringExtra("video_url")
        val unidadId = intent.getStringExtra("unidad_id")
        val order = intent.getIntExtra("order", 0)

        if (titulo == null || videoUrl == null) {
            Toast.makeText(this, "Error: unidad no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        txtTitulo.text = titulo
        currentVideoUrl = videoUrl
        cargarVideoEnWebView(videoUrl)

        btnIrParcial.setOnClickListener {
            if (unidadId.isNullOrEmpty()) {
                Toast.makeText(this, "No se encontró el ID de la unidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ParcialActivity::class.java)
            intent.putExtra("unidad_id", unidadId)
            intent.putExtra("titulo", titulo)
            intent.putExtra("video_url", videoUrl)
            intent.putExtra("unidad_order", order)

            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnVolverIntro.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun cargarVideoEnWebView(videoUrl: String) {
        val videoId = extraerIdDeYoutube(videoUrl)
        val urlMovil = "https://m.youtube.com/watch?v=$videoId"

        val settings = webViewVideo.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.mediaPlaybackRequiresUserGesture = false
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        // Permitir reproducir inline (sin reiniciar al cambiar fullscreen)
        settings.mediaPlaybackRequiresUserGesture = false
        webViewVideo.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webViewVideo.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        webViewVideo.webViewClient = WebViewClient()

        webViewVideo.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                customView = view
                customViewCallback = callback
                fullScreenContainer.visibility = View.VISIBLE
                fullScreenContainer.addView(view)
                webViewVideo.visibility = View.GONE

                hideSystemUI()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }

            override fun onHideCustomView() {
                fullScreenContainer.visibility = View.GONE
                fullScreenContainer.removeView(customView)
                customView = null
                webViewVideo.visibility = View.VISIBLE

                showSystemUI()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                customViewCallback?.onCustomViewHidden()
            }
        }

        if (webViewVideo.url == null || webViewVideo.url != urlMovil) {
            webViewVideo.loadUrl(urlMovil)
        }
    }

    private fun extraerIdDeYoutube(url: String): String {
        return when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
            url.contains("shorts/") -> url.substringAfter("shorts/").substringBefore("?")
            else -> url
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView)
            .show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            webViewVideo.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (customView != null) {
                (webViewVideo.webChromeClient as WebChromeClient).onHideCustomView()
            } else {
                webViewVideo.stopLoading()
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}