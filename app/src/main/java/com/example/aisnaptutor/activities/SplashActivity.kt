package com.example.aisnaptutor.activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.aisnaptutor.R
import com.example.aisnaptutor.SharePref
import com.example.aisnaptutor.ads.AdmobRewardedAd
import com.example.aisnaptutor.databinding.ActivitySplashBinding
import com.google.android.gms.ads.MobileAds

class SplashActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    companion object {
        val admobRewarded = AdmobRewardedAd()
        var admobRewardedId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        admobRewardedId = getString(R.string.admob_rewarded_id)
//        setLanguage(SharePref.getString("language", "en"))

//         Initialize Mobile Ads SDK
        MobileAds.initialize(this) {
            admobRewarded.loadRewardedAd(this, admobRewardedId)
        }
        val rotateAnimation = ObjectAnimator.ofFloat(binding.imageView, "rotation", 0f, 360f)
        rotateAnimation.duration = 5000
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE

        // Start animation
        rotateAnimation.start()

        val handler = Handler()

        val runnable = object : Runnable {
            override fun run() {
                val progress = binding.progressBar.progress
                if (progress < binding.progressBar.max) {
                    binding.progressBar.progress = progress + 2
                    handler.postDelayed(this, 100)
                }
            }
        }

        handler.post(runnable)

        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            binding.progressBar.visibility = View.GONE
        }, 5000)
    }
}