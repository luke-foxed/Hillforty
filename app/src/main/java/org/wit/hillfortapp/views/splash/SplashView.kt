package org.wit.hillfortapp.views.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.login.LoginView


class SplashView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        // 4second splash time
        Handler().postDelayed({
            startActivity(Intent(this@SplashView, LoginView::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 4000)
    }
}