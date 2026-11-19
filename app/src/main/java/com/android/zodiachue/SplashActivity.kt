package com.android.zodiachue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.helper.UserRepository

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        findViewById<View>(R.id.splash_content)?.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(600L).start()
        }

        val userRepository = UserRepository(this)
        Handler(Looper.getMainLooper()).postDelayed({
            val rememberedUser = userRepository.restoreSessionIfRemembered()
            val destination = if (rememberedUser != null) {
                LandingPageActivity::class.java
            } else {
                LoginPageActivity::class.java
            }
            startActivity(Intent(this, destination))
            finish()
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1100L)
    }
}
