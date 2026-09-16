package com.epaperlauncher.core.data.domain.repository

import android.app.Activity
import com.epaperlauncher.feature.billing.domain.model.EntitlementState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Play Billing (Pro tier entitlements).
 */
interface BillingRepository {
    /**
     * Observe whether the user has Pro entitlement.
     */
    fun observeEntitlement(): Flow<EntitlementState>

    /**
     * Launch purchase flow for Pro upgrade.
     */
    suspend fun launchPurchaseFlow(activity: Activity)

    /**
     * Restore purchases (called on user request or first launch).
     */
    suspend fun restorePurchases()

    /**
     * Check if billing is available (Google Play Services).
     */
    fun isBillingAvailable(): Boolean
}
