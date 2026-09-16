package com.epaperlauncher.feature.billing.data

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.epaperlauncher.core.data.domain.repository.BillingRepository
import com.epaperlauncher.feature.billing.domain.model.EntitlementState
import com.epaperlauncher.feature.billing.domain.model.ProProduct
import com.epaperlauncher.feature.billing.domain.model.PurchaseResult
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Implementation of BillingRepository using Play Billing Library.
 */
@ViewModelScoped
class BillingRepositoryImpl @Inject constructor() : BillingRepository {

    private val _entitlementState = MutableStateFlow<EntitlementState>(EntitlementState.Free)
    
    override fun observeEntitlement(): Flow<EntitlementState> = _entitlementState.asStateFlow()

    private var billingClient: BillingClient? = null
    private var productDetailsMap = mutableMapOf<String, ProductDetails>()
    private var pendingPurchaseProductId: String? = null

    /**
     * Initialize the BillingClient. Call this from Application or when needed.
     */
    fun initializeBillingClient(activity: Activity): Boolean {
        if (billingClient != null) return true

        billingClient = BillingClient.newBuilder(activity.applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        var isConnected = false
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isConnected = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                if (isConnected) {
                    queryProductDetails()
                    queryPastPurchases(activity)
                } else {
                    _entitlementState.update { 
                        EntitlementState.Error("Billing setup failed: ${billingResult.debugMessage}") 
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
                // Will retry on next startConnection
            }
        })

        return isConnected
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _entitlementState.update { EntitlementState.Free }
        } else {
            _entitlementState.update { 
                EntitlementState.Error("Purchase failed: ${billingResult.debugMessage}") 
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                // Grant entitlement
                _entitlementState.update { EntitlementState.ProUnlocked }
                
                // Acknowledge purchase (required for non-consumables)
                acknowledgePurchase(purchase)
                
                pendingPurchaseProductId = null
            }
            Purchase.PurchaseState.PENDING -> {
                _entitlementState.update { EntitlementState.Pending }
            }
            Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                // Ignore
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.isAcknowledged) return

        billingClient?.acknowledgePurchase(
            com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        ) { billingResult ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                // Handle acknowledgment failure
            }
        }
    }

    private fun queryProductDetails() {
        val productList = ProProduct.values().map { product ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(product.productId)
                .setProductType(
                    when (product.type) {
                        ProProduct.ProductType.ONE_TIME -> BillingClient.ProductType.INAPP
                        ProProduct.ProductType.SUBSCRIPTION -> BillingClient.ProductType.SUBS
                    }
                )
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { details ->
                    productDetailsMap[details.productId] = details
                }
            }
        }
    }

    private fun queryPastPurchases(activity: Activity) {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductList(
                    listOf(
                        BillingClient.ProductType.INAPP,
                        BillingClient.ProductType.SUBS
                    )
                )
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                val hasActivePurchase = purchases.any { 
                    it.purchaseState == Purchase.PurchaseState.PURCHASED &&
                    !it.isAcknowledged || it.products.any { prod -> 
                        prod in ProProduct.values().map { p -> p.productId }
                    }
                }
                
                if (hasActivePurchase) {
                    _entitlementState.update { EntitlementState.ProUnlocked }
                }
            }
        }
    }

    override suspend fun launchPurchaseFlow(activity: Activity) {
        if (billingClient == null || !billingClient!!.isReady) {
            if (!initializeBillingClient(activity)) {
                _entitlementState.update { 
                    EntitlementState.Error("Billing service not available") 
                }
                return
            }
        }

        // Default to one-time upgrade product
        val productId = ProProduct.PRO_UPGRADE_ONE_TIME.productId
        val productDetails = productDetailsMap[productId]

        if (productDetails == null) {
            _entitlementState.update { 
                EntitlementState.Error("Product details not loaded") 
            }
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient?.launchBillingFlow(activity, flowParams)?.let { billingResult ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                _entitlementState.update { 
                    EntitlementState.Error("Failed to launch purchase: ${billingResult.debugMessage}") 
                }
            } else {
                _entitlementState.update { EntitlementState.Pending }
                pendingPurchaseProductId = productId
            }
        }
    }

    override suspend fun restorePurchases() {
        // Query purchases will be called automatically on connect,
        // but we can trigger it manually here
        // This is typically called when user explicitly requests restore
    }

    override fun isBillingAvailable(): Boolean {
        return billingClient?.isReady == true
    }

    /**
     * Clean up resources when no longer needed.
     */
    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
    }
}
