package com.epaperlauncher.feature.billing.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epaperlauncher.core.data.domain.repository.BillingRepository
import com.epaperlauncher.feature.billing.domain.model.EntitlementState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing Pro upgrade UI state and billing operations.
 */
@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billingRepository: BillingRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableBillingUiState()
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    init {
        observeEntitlement()
    }

    private fun observeEntitlement() {
        viewModelScope.launch {
            billingRepository.observeEntitlement().collect { entitlement ->
                _uiState.entitlement = entitlement
                updateDerivedState()
            }
        }
    }

    private fun updateDerivedState() {
        _uiState.isProUnlocked = _uiState.entitlement is EntitlementState.ProUnlocked
        _uiState.isLoading = _uiState.entitlement is EntitlementState.Pending
        _uiState.errorMessage = when (val state = _uiState.entitlement) {
            is EntitlementState.Error -> state.message
            else -> null
        }
    }

    /**
     * Initialize billing client (call from Activity onCreate).
     */
    fun initializeBilling(activity: Activity) {
        billingRepository.initializeBillingClient(activity)
    }

    /**
     * Launch purchase flow for Pro upgrade.
     */
    fun launchPurchase(activity: Activity) {
        viewModelScope.launch {
            billingRepository.launchPurchaseFlow(activity)
        }
    }

    /**
     * Restore purchases (user-initiated).
     */
    fun restorePurchases() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
        }
    }

    /**
     * Clear error state.
     */
    fun clearError() {
        _uiState.errorMessage = null
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.endConnection()
    }
}

/**
 * UI state for the billing screen/component.
 */
data class BillingUiState(
    val entitlement: EntitlementState = EntitlementState.Free,
    val isProUnlocked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productName: String? = null,
    val productPrice: String? = null,
    val productDescription: String? = null
)

private class MutableBillingUiState(
    var entitlement: EntitlementState = EntitlementState.Free,
    var isProUnlocked: Boolean = false,
    var isLoading: Boolean = false,
    var errorMessage: String? = null,
    var productName: String? = null,
    var productPrice: String? = null,
    var productDescription: String? = null
)
