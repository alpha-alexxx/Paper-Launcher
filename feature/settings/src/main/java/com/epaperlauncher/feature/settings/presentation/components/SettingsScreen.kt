package com.epaperlauncher.feature.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.epaperlauncher.core.data.domain.model.ContrastPreset
import com.epaperlauncher.core.data.domain.model.IconStyle
import com.epaperlauncher.core.data.domain.model.PaperTone
import com.epaperlauncher.core.ui.PaperTokens
import com.epaperlauncher.core.ui.Spacing

/**
 * Settings screen with all configuration options.
 */
@Composable
fun SettingsScreen(
    uiState: com.epaperlauncher.feature.settings.presentation.SettingsUiState,
    onPaperToneChanged: (PaperTone) -> Unit,
    onGrainIntensityChanged: (Float) -> Unit,
    onContrastChanged: (ContrastPreset) -> Unit,
    onPageTurnAnimationToggled: () -> Unit,
    onAutoDayNightToggled: () -> Unit,
    onIconStyleChanged: (IconStyle) -> Unit,
    onSystemWideFilterToggled: () -> Unit,
    onRestorePurchases: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PaperTokens.warmBackground)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Header
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = PaperTokens.warmInk,
                modifier = Modifier.padding(vertical = Spacing.sm)
            )
        }

        // Paper Tone Section
        item {
            PaperToneSection(
                selectedTone = uiState.themeConfig.paperTone,
                onToneSelected = onPaperToneChanged
            )
        }

        // Grain Intensity Section
        item {
            GrainIntensitySection(
                currentIntensity = uiState.themeConfig.grainIntensity,
                onIntensityChanged = onGrainIntensityChanged
            )
        }

        // Contrast Section
        item {
            ContrastSection(
                selectedPreset = uiState.themeConfig.contrastCurve,
                onPresetSelected = onContrastChanged
            )
        }

        // Icon Style Section
        item {
            IconStyleSection(
                selectedStyle = uiState.themeConfig.iconStyle,
                onStyleSelected = onIconStyleChanged
            )
        }

        // Toggle Options Section
        item {
            ToggleOptionsSection(
                pageTurnAnimationEnabled = uiState.themeConfig.pageTurnAnimation,
                autoDayNightEnabled = uiState.themeConfig.autoDayNightSwitch,
                onPageTurnAnimationToggle = onPageTurnAnimationToggled,
                onAutoDayNightToggle = onAutoDayNightToggled
            )
        }

        // Filter Engine Section
        item {
            FilterEngineSection(
                filterEnabled = uiState.themeConfig.filterEnabledSystemWide,
                engineState = uiState.filterEngineState,
                hasOverlayPermission = uiState.hasOverlayPermission,
                isAccessibilityServiceEnabled = uiState.isAccessibilityServiceEnabled,
                isProUnlocked = uiState.isProUnlocked,
                onToggleFilter = onSystemWideFilterToggled
            )
        }

        // Billing Section
        if (!uiState.isProUnlocked) {
            item {
                BillingSection(
                    onRestorePurchases = onRestorePurchases
                )
            }
        }
    }
}

/**
 * Paper tone selection section.
 */
@Composable
private fun PaperToneSection(
    selectedTone: PaperTone,
    onToneSelected: (PaperTone) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Paper Tone",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            item {
                PaperToneChip(
                    label = "Warm",
                    selected = selectedTone == PaperTone.WARM,
                    onClick = { onToneSelected(PaperTone.WARM) },
                    backgroundColor = PaperTokens.warmBackground
                )
            }
            item {
                PaperToneChip(
                    label = "Cold",
                    selected = selectedTone == PaperTone.COLD,
                    onClick = { onToneSelected(PaperTone.COLD) },
                    backgroundColor = PaperTokens.coldBackground
                )
            }
            item {
                PaperToneChip(
                    label = "Pure White",
                    selected = selectedTone == PaperTone.PURE_WHITE,
                    onClick = { onToneSelected(PaperTone.PURE_WHITE) },
                    backgroundColor = PaperTokens.pureBackground
                )
            }
        }
    }
}

/**
 * Paper tone selection chip.
 */
@Composable
private fun PaperToneChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    backgroundColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PaperTokens.warmInk else PaperTokens.warmDivider,
                shape = RoundedCornerShape(2.dp)
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = PaperTokens.warmInk
        )
    }
}

/**
 * Grain intensity slider section.
 */
@Composable
private fun GrainIntensitySection(
    currentIntensity: Float,
    onIntensityChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Grain Intensity",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        Text(
            text = "${(currentIntensity * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = PaperTokens.warmInkMuted,
            modifier = Modifier.padding(bottom = Spacing.xs)
        )

        androidx.compose.material3.Slider(
            value = currentIntensity,
            onValueChange = onIntensityChanged,
            valueRange = 0f..0.3f,
            steps = 5,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Contrast preset selection section.
 */
@Composable
private fun ContrastSection(
    selectedPreset: ContrastPreset,
    onPresetSelected: (ContrastPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Contrast",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            item {
                ContrastChip(
                    label = "Low",
                    selected = selectedPreset == ContrastPreset.LOW,
                    onClick = { onPresetSelected(ContrastPreset.LOW) }
                )
            }
            item {
                ContrastChip(
                    label = "Standard",
                    selected = selectedPreset == ContrastPreset.STANDARD,
                    onClick = { onPresetSelected(ContrastPreset.STANDARD) }
                )
            }
            item {
                ContrastChip(
                    label = "High",
                    selected = selectedPreset == ContrastPreset.HIGH,
                    onClick = { onPresetSelected(ContrastPreset.HIGH) }
                )
            }
            item {
                ContrastChip(
                    label = "Extra High",
                    selected = selectedPreset == ContrastPreset.EXTRA_HIGH,
                    onClick = { onPresetSelected(ContrastPreset.EXTRA_HIGH) }
                )
            }
        }
    }
}

/**
 * Contrast selection chip.
 */
@Composable
private fun ContrastChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PaperTokens.warmInk else PaperTokens.warmDivider,
                shape = RoundedCornerShape(2.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = PaperTokens.warmInk
        )
    }
}

/**
 * Icon style selection section.
 */
@Composable
private fun IconStyleSection(
    selectedStyle: IconStyle,
    onStyleSelected: (IconStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Icon Style",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            item {
                IconStyleChip(
                    label = "Flat Mono",
                    selected = selectedStyle == IconStyle.FLAT_MONOCHROME,
                    onClick = { onStyleSelected(IconStyle.FLAT_MONOCHROME) }
                )
            }
            item {
                IconStyleChip(
                    label = "Line Art",
                    selected = selectedStyle == IconStyle.LINE_ART,
                    onClick = { onStyleSelected(IconStyle.LINE_ART) }
                )
            }
            item {
                IconStyleChip(
                    label = "Stamp",
                    selected = selectedStyle == IconStyle.STAMP,
                    onClick = { onStyleSelected(IconStyle.STAMP) }
                )
            }
        }
    }
}

/**
 * Icon style selection chip.
 */
@Composable
private fun IconStyleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) PaperTokens.warmInk else PaperTokens.warmDivider,
                shape = RoundedCornerShape(2.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = PaperTokens.warmInk
        )
    }
}

/**
 * Toggle options section.
 */
@Composable
private fun ToggleOptionsSection(
    pageTurnAnimationEnabled: Boolean,
    autoDayNightEnabled: Boolean,
    onPageTurnAnimationToggle: () -> Unit,
    onAutoDayNightToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = "Display Options",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.xs)
        )

        SettingToggleRow(
            label = "Page Turn Animation",
            enabled = pageTurnAnimationEnabled,
            onToggle = onPageTurnAnimationToggle
        )

        SettingToggleRow(
            label = "Auto Day/Night Switch",
            enabled = autoDayNightEnabled,
            onToggle = onAutoDayNightToggle
        )
    }
}

/**
 * Single toggle row for settings.
 */
@Composable
private fun SettingToggleRow(
    label: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = PaperTokens.warmInk
        )

        androidx.compose.material3.Switch(
            checked = enabled,
            onCheckedChange = { onToggle() },
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = PaperTokens.accent,
                checkedTrackColor = PaperTokens.warmDivider
            )
        )
    }
}

/**
 * Filter engine control section.
 */
@Composable
private fun FilterEngineSection(
    filterEnabled: Boolean,
    engineState: com.epaperlauncher.core.data.domain.model.FilterEngineState,
    hasOverlayPermission: Boolean,
    isAccessibilityServiceEnabled: Boolean,
    isProUnlocked: Boolean,
    onToggleFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "System-Wide Filter",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        Text(
            text = when (engineState) {
                com.epaperlauncher.core.data.domain.model.FilterEngineState.DISABLED -> 
                    "Filter is disabled"
                com.epaperlauncher.core.data.domain.model.FilterEngineState.PERMISSION_REQUIRED -> 
                    "Permission required - enable in settings"
                com.epaperlauncher.core.data.domain.model.FilterEngineState.ACTIVE -> 
                    "Filter is active"
                com.epaperlauncher.core.data.domain.model.FilterEngineState.ERROR -> 
                    "Error - check logs"
            },
            style = MaterialTheme.typography.bodySmall,
            color = PaperTokens.warmInkMuted,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        if (!isProUnlocked) {
            Text(
                text = "Pro tier required for system-wide filter",
                style = MaterialTheme.typography.bodySmall,
                color = PaperTokens.accent,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }

        SettingToggleRow(
            label = "Enable System-Wide Filter",
            enabled = filterEnabled && isProUnlocked,
            onToggle = onToggleFilter
        )

        if (!hasOverlayPermission || !isAccessibilityServiceEnabled) {
            Text(
                text = buildString {
                    if (!hasOverlayPermission) append("• Overlay permission required\n")
                    if (!isAccessibilityServiceEnabled) append("• Accessibility service must be enabled")
                },
                style = MaterialTheme.typography.bodySmall,
                color = PaperTokens.warmInkMuted,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

/**
 * Billing/Pro upgrade section.
 */
@Composable
private fun BillingSection(
    onRestorePurchases: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Upgrade to Pro",
            style = MaterialTheme.typography.titleMedium,
            color = PaperTokens.warmInk,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )

        Text(
            text = "Unlock system-wide filter, all icon styles, and premium grain textures",
            style = MaterialTheme.typography.bodyMedium,
            color = PaperTokens.warmInkMuted,
            modifier = Modifier.padding(bottom = Spacing.md)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            androidx.compose.material3.Button(
                onClick = { /* TODO: Launch purchase flow */ },
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PaperTokens.warmInk
                )
            ) {
                Text(
                    text = "Upgrade to Pro",
                    color = PaperTokens.warmBackground,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            androidx.compose.material3.OutlinedButton(
                onClick = onRestorePurchases,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.OutlinedButtonDefaults.outlinedButtonColors(
                    contentColor = PaperTokens.warmInk
                )
            ) {
                Text(
                    text = "Restore",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
