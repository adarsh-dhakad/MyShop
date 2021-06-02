package com.example.myshop.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.example.myshop.R


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
// hide statusbar
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        // font
        val typeface:Typeface = Typeface.createFromAsset(assets,"Montserrat-Bold.ttf")
        val tv_title  = findViewById<TextView>(R.id.tv_app_name)
        tv_title.typeface = typeface

//        @Suppress("DEPRECATION")
//        Handler().postDelayed(
//                {
//                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                },
//
//                3000   )
        Handler(Looper.getMainLooper()).postDelayed(
                {
                    startActivity(Intent(this@SplashActivity,DashboardActivity::class.java))
                    finish()
                },
                3000
        )

    }}