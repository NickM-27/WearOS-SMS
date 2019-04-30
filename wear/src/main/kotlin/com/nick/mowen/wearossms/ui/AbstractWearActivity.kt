package com.nick.mowen.wearossms.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.wearable.activity.WearableActivity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import com.android.billingclient.api.*
import com.nick.mowen.wearossms.R

abstract class AbstractWearActivity : WearableActivity(), PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    protected var premium = false
    protected var dynamicColor = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildBilling()

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("trueDark", false))
            window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dynamicColor = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("contactColor", true)
    }

    override fun onStart() {
        super.onStart()
        premium = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("premium", false)
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(this).edit { putBoolean("premium", premium) }
        super.onStop()
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null)
            premium = true
    }

    private fun buildBilling() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .build()

        billingClient!!.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResponse: Int) {
                if (billingResponse == BillingClient.BillingResponse.OK)
                    premium = checkPremium()
                else
                    Toast.makeText(this@AbstractWearActivity, "Purchases may not be working correctly", Toast.LENGTH_SHORT).show()
            }

            override fun onBillingServiceDisconnected() {

            }
        })
    }

    private fun checkPremium(): Boolean {
        val purchaseResults: Purchase.PurchasesResult = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
        val purchases: List<Purchase> = purchaseResults.purchasesList
        return purchases.isNotEmpty()
    }

    protected fun getPremium() {
        if (!premium) {
            if (billingClient == null) {
                buildBilling()
                getPremium()
            } else
                billingClient!!.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSku("wear_sms_premium")
                        .setType(BillingClient.SkuType.INAPP)
                        .build())
        }
    }
}