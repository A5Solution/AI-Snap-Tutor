package com.example.aisnaptutor.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdmobRewardedAd {
    var rewardedAd: RewardedAd? = null

    fun loadRewardedAd(context: Context, rewardedId: String){
        if (rewardedAd != null)
        {
            return
        }

        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(context,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("MyRewardedAd", adError?.toString()!!)
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("MyRewardedAd", "Ad was loaded.")
                rewardedAd = ad
            }
        })
    }

    fun showRewarded(context: Context, adEvent: (Boolean) -> Unit){
        rewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object: FullScreenContentCallback(){
                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("MyRewardedAd", "onAdClicked: ")
                    rewardedAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.d("MyRewardedAd", "onAdDismissedFullScreenContent: ")
                    adEvent.invoke(true)
                    rewardedAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    Log.d("MyRewardedAd", "onAdFailedToShowFullScreenContent: ")
                    rewardedAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.d("MyRewardedAd", "onAdImpression: ")
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Log.d("MyRewardedAd", "onAdShowedFullScreenContent: ")
                }
            }
            ad.show(context as Activity, OnUserEarnedRewardListener { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d("MyRewardedAd", "User earned the reward.")
            })
        } ?: run {
            Log.d("MyRewardedAd", "The rewarded ad wasn't ready yet.")
        }
    }
}

