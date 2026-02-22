package com.vergiai.inspector

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var rootLayout: LinearLayout
    private lateinit var statusText: TextView
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // UI Oluşturma
        createFloatingUI()
    }

    private fun createFloatingUI() {
        // Ana Layout (Siyah yarı saydam)
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xCC000000.toInt()) // %80 Siyah
            setPadding(16, 16, 16, 16)
        }

        // Başlık / Durum Metni
        statusText = TextView(this).apply {
            text = "VergiAI: Hazır"
            setTextColor(0xFF00FF00.toInt()) // Yeşil
            textSize = 14f
        }
        rootLayout.addView(statusText)

        // Aksiyon Butonu
        val actionButton = Button(this).apply {
            text = "Menüyü Aç"
            setBackgroundColor(0xFFD32F2F.toInt()) // Kırmızı
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                toggleMenu(this)
            }
        }
        rootLayout.addView(actionButton)

        // Pencere Ayarları
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        // Sürükleme Mantığı (Drag & Drop)
        rootLayout.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(rootLayout, params)
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(rootLayout, params)
    }

    private fun toggleMenu(button: Button) {
        if (!isExpanded) {
            // Menüyü Genişlet
            button.text = "Kapat"
            statusText.text = "Mod: No-Root (Overlay)"
            
            // Dinamik olarak yeni butonlar ekle
            val scanBtn = Button(this).apply {
                text = "Otomatik Tıkla (Demo)"
                setOnClickListener {
                    Toast.makeText(context, "Accessibility Servisi Gerekli!", Toast.LENGTH_SHORT).show()
                }
            }
            rootLayout.addView(scanBtn)
            
            val exitBtn = Button(this).apply {
                text = "Çıkış"
                setOnClickListener { stopSelf() }
            }
            rootLayout.addView(exitBtn)
            
            isExpanded = true
        } else {
            // Menüyü Küçült
            // (Basitlik için layout'u yeniden çizmek yerine child view'ları silebiliriz ama
            // şimdilik sadece stopSelf ile kapatalım veya metni değiştirelim)
            button.text = "Menüyü Aç"
            statusText.text = "VergiAI: Gizli"
            
            // Eklenen butonları temizle (Basit yöntem: 2. indexten sonrasını sil)
            while (rootLayout.childCount > 2) {
                rootLayout.removeViewAt(2)
            }
            isExpanded = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::rootLayout.isInitialized) {
            windowManager.removeView(rootLayout)
        }
    }
}
