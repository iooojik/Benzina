package com.kirovcompany.benzina.interfaces

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kirovcompany.benzina.StaticVars

interface AdUtil {

    fun showInterstitialAd(context: Context, activity: Activity, isFuel : Boolean){
        val TAG = "AD"
        val adRequest = AdRequest.Builder().build()
        var mInterstitialAd : InterstitialAd? = null

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                //mInterstitialAd = null;
            }
        }

        val id : String = if (isFuel)
            StaticVars.videoAfterFuelAdId
        else StaticVars.videoOnStartUp

        InterstitialAd.load(context, id, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.show(activity)
            }
        })
    }



}