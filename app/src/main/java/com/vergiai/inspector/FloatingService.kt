package com.vergiai.inspector

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var layout: LinearLayout

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0x88000000.toInt()) // Yarı saydam siyah
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        val scanButton = Button(this).apply {
            text = "Değer Tara"
            setOnClickListener {
                // NativeEngine call buraya gelecek hocam
            }
        }

        layout.addView(scanButton)
        windowManager.addView(layout, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(layout)
    }
}

