package com.epaperlauncher.feature.billing.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epaperlauncher.core.ui.PaperTokens
import com.epaperlauncher.core.ui.Spacing
import com.epaperlauncher.feature.billing.domain.model.EntitlementState
import com.epaperlauncher.feature.billing.presentation.BillingUiState

/**
 * Pro upgrade card component for settings screen or dedicated upgrade screen.
 */
@Composable
fun ProUpgradeCard(
    uiState: BillingUiState,
    onUpgradeClick: () -> Unit,
    onRestoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (uiState.isProUnlocked) {
        PaperTokens.warmSurface.copy(alpha = 0.5f)
    } else {
        PaperTokens.warmBackground
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = PaperTokens.warmDivider,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(Spacing.lg)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Pro badge
            if (uiState.isProUnlocked) {
                ProUnlockedBadge()
            } else {
                ProFeatureList()
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Action buttons
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = PaperTokens.accent
                )
            } else if (!uiState.isProUnlocked) {
                Button(
                    onClick = onUpgradeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PaperTokens.accent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = "Upgrade to Pro",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                TextButton(
                    onClick = onRestoreClick,
                    text = "Restore Purchases"
                )
            } else {
                // Already unlocked - show features list
                Text(
                    text = "Pro Features Unlocked",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PaperTokens.warmInkMuted
                )
            }

            // Error message
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = Spacing.md)
                )
            }
        }
    }
}

@Composable
private fun ProUnlockedBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(PaperTokens.accent.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = PaperTokens.accent,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        Text(
            text = "✓ PRO UNLOCKED",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PaperTokens.accent
        )
    }
}

@Composable
private fun ProFeatureList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Unlock Pro Features:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = PaperTokens.warmInk
        )

        FeatureItem("System-wide e-ink filter")
        FeatureItem("Advanced grain textures")
        FeatureItem("All icon styles")
        FeatureItem("Premium widget pack")
        FeatureItem("Priority support")
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = "•",
            color = PaperTokens.accent,
            fontSize = 16.sp
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = PaperTokens.warmInkMuted
        )
    }
}

@Composable
private fun TextButton(
    onClick: () -> Unit,
    text: String
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        colors = androidx.compose.material3.TextButtonDefaults.textButtonColors(
            contentColor = PaperTokens.warmInkMuted
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp
        )
    }
}

/**
 * Compact billing status indicator for gating UI elements.
 */
@Composable
fun ProGatingBanner(
    isProUnlocked: Boolean,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isProUnlocked) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                PaperTokens.accent.copy(alpha = 0.1f)
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Pro feature - Upgrade to unlock",
            fontSize = 13.sp,
            color = PaperTokens.warmInkMuted
        )

        Text(
            text = "UPGRADE →",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PaperTokens.accent,
            modifier = Modifier
                .padding(4.dp)
        )
    }
}
