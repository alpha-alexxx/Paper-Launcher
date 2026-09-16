package com.epaperlauncher.core.ui

import androidx.compose.ui.graphics.Color

/**
 * E-Paper Design Tokens
 * 
 * A minimalist, Kindle-inspired aesthetic:
 * - Warm cream paper background (#F5F1E8)
 * - Flat grayscale palette (no colors, only ink tones)
 * - Thin hairline dividers (1dp, subtle)
 * - No shadows, no gradients, no elevation
 * - Subtle paper grain texture overlay
 * - Typography-led layout hierarchy
 * - Clean sans-serif font throughout
 */
object PaperTokens {
    
    // ─────────────────────────────────────────────────────────────
    // PAPER BACKGROUNDS (Warm Cream Tones)
    // ─────────────────────────────────────────────────────────────
    
    /** Primary background: warm cream paper (#F5F1E8) */
    val paperBackground = Color(0xFFF5F1E8)
    
    /** Secondary surface: slightly darker cream (#EDE8DD) */
    val paperSurface = Color(0xFFEDE8DD)
    
    /** Tertiary surface: subtle variation (#E5E0D4) */
    val paperSurfaceVariant = Color(0xFFE5E0D4)
    
    // ─────────────────────────────────────────────────────────────
    // INK COLORS (Flat Grayscale Palette)
    // ─────────────────────────────────────────────────────────────
    
    /** Primary ink: deep charcoal for main text (#2A2A2A) */
    val inkPrimary = Color(0xFF2A2A2A)
    
    /** Secondary ink: medium gray for secondary text (#5A5A5A) */
    val inkSecondary = Color(0xFF5A5A5A)
    
    /** Tertiary ink: light gray for hints/disabled (#8A8A8A) */
    val inkTertiary = Color(0xFF8A8A8A)
    
    /** Subtle ink: very light gray for subtle elements (#B0B0B0) */
    val inkSubtle = Color(0xFFB0B0B0)
    
    // ─────────────────────────────────────────────────────────────
    // DIVIDERS (Hairline, 1dp)
    // ─────────────────────────────────────────────────────────────
    
    /** Standard divider: subtle hairline (#D4CFC4) */
    val divider = Color(0xFFD4CFC4)
    
    /** Strong divider: more visible separation (#C4BFB4) */
    val dividerStrong = Color(0xFFC4BFB4)
    
    /** Subtle divider: minimal separation (#E0DBD0) */
    val dividerSubtle = Color(0xFFE0DBD0)
    
    // ─────────────────────────────────────────────────────────────
    // DARK MODE (E-Ink Night Mode)
    // ─────────────────────────────────────────────────────────────
    
    /** Dark background: warm charcoal (#1A1A1A) */
    val darkBackground = Color(0xFF1A1A1A)
    
    /** Dark surface: slightly lighter charcoal (#242424) */
    val darkSurface = Color(0xFF242424)
    
    /** Dark surface variant: subtle variation (#2E2E2E) */
    val darkSurfaceVariant = Color(0xFF2E2E2E)
    
    /** Dark ink primary: off-white for readability (#E8E8E8) */
    val darkInkPrimary = Color(0xFFE8E8E8)
    
    /** Dark ink secondary: light gray (#A0A0A0) */
    val darkInkSecondary = Color(0xFFA0A0A0)
    
    /** Dark ink tertiary: medium gray (#707070) */
    val darkInkTertiary = Color(0xFF707070)
    
    /** Dark divider: subtle separation (#3A3A3A) */
    val darkDivider = Color(0xFF3A3A3A)
    
    /** Dark divider subtle: minimal separation (#2E2E2E) */
    val darkDividerSubtle = Color(0xFF2E2E2E)
    
    // ─────────────────────────────────────────────────────────────
    // SPECIAL STATES (Still Grayscale)
    // ─────────────────────────────────────────────────────────────
    
    /** Selected/active state: subtle highlight (#E8E4D8) */
    val stateSelected = Color(0xFFE8E4D8)
    
    /** Pressed state: slight darken (#D8D4C8) */
    val statePressed = Color(0xFFD8D4C8)
    
    /** Focus ring: thin accent (#6A6A6A) */
    val stateFocus = Color(0xFF6A6A6A)
    
    /** Error state: dark gray-red tint (#5A4A4A) */
    val stateError = Color(0xFF5A4A4A)
    
    /** Success state: dark gray-green tint (#4A5A4A) */
    val stateSuccess = Color(0xFF4A5A4A)
}
