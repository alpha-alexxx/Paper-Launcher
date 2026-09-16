package com.epaperlauncher.feature.billing.domain.model

/**
 * Represents the user's Pro entitlement state.
 */
sealed class EntitlementState {
    /**
     * User has not purchased Pro yet.
     */
    object Free : EntitlementState()

    /**
     * User has active Pro subscription or one-time purchase.
     */
    object ProUnlocked : EntitlementState()

    /**
     * Purchase is pending (user is in checkout flow).
     */
    object Pending : EntitlementState()

    /**
     * Error occurred during billing operations.
     */
    data class Error(val message: String) : EntitlementState()
}

/**
 * Available Pro products.
 */
enum class ProProduct(
    val productId: String,
    val type: ProductType
) {
    PRO_UPGRADE_ONE_TIME("epaper_pro_upgrade", ProductType.ONE_TIME),
    PRO_SUBSCRIPTION_MONTHLY("epaper_pro_monthly", ProductType.SUBSCRIPTION);

    enum class ProductType {
        ONE_TIME,
        SUBSCRIPTION
    }
}

/**
 * Purchase result from billing operations.
 */
sealed class PurchaseResult {
    object Success : PurchaseResult()
    data class Failure(val errorCode: Int, val message: String) : PurchaseResult()
    object Cancelled : PurchaseResult()
}
