# Filter Engine Module - Phase 0 Spike

## Overview
This module implements the core technical risk of the E-Paper Launcher: the real-time screen filter pipeline that transforms colorful app content into an e-ink/paper visual style.

## Architecture

### Tier 1 (Non-Root, Ships to All Users)
The filter uses a two-component approach:

1. **AccessibilityService** (`FilterOverlayService`)
   - Legally draws a persistent overlay across other apps
   - Detects window/app-switch events via `TYPE_WINDOW_STATE_CHANGED`
   - Triggers page-turn transition animations

2. **MediaProjection + VirtualDisplay** (`ScreenCaptureManager`)
   - Captures the live screen framebuffer
   - Requires one-time user consent via system dialog
   - Processes frames through the AGSL shader pipeline

3. **AGSL Shader** (`PaperFilterShader`, API 33+)
   - Grayscale conversion (luminosity method)
   - Contrast/gamma adjustment
   - Paper tone mapping (warm/cold/pure white)
   - Grain texture overlay

4. **Legacy Fallback** (`LegacyPaperFilter`, API <33)
   - CPU-based ColorMatrix operations
   - Grayscale + contrast only (no grain/dither)

### Key Files

```
feature/filter-engine/
├── build.gradle.kts                          # Module dependencies
├── src/main/
│   ├── java/com/epaperlauncher/feature/filterengine/
│   │   ├── data/
│   │   │   ├── FilterOverlayService.kt       # Accessibility service
│   │   │   ├── impl/
│   │   │   │   └── FilterEngineControllerImpl.kt  # Controller implementation
│   │   │   └── mediaprojection/
│   │   │       └── ScreenCaptureManager.kt   # MediaProjection pipeline
│   │   ├── domain/
│   │   │   └── model/
│   │   │       └── FilterModels.kt           # Data models (FilterConfig, CapturedFrame, FilterResult)
│   │   ├── presentation/
│   │   │   ├── components/
│   │   │   │   └── FilterOverlayCanvas.kt    # Compose overlay UI
│   │   │   └── shader/
│   │   │       └── PaperFilterShader.kt      # AGSL + legacy implementations
│   │   └── di/
│   │       └── FilterEngineModule.kt         # Hilt DI module
│   └── res/raw/
│       └── paper_filter_shader.agsl          # AGSL shader source
└── src/test/
    └── java/com/epaperlauncher/feature/filterengine/
        └── LegacyPaperFilterTest.kt          # Unit tests for legacy filter
```

## Usage

### Enable Filter
```kotlin
@Inject lateinit var filterController: FilterEngineController

// Check permissions
if (!filterController.hasOverlayPermission()) {
    // Request overlay permission
}

if (!filterController.isAccessibilityServiceEnabled()) {
    // Guide to accessibility settings
}

// Enable
val state = filterController.requestEnable()
```

### Observe State
```kotlin
lifecycleScope.launch {
    filterController.observeEngineState().collect { state ->
        when (state) {
            FilterEngineState.DISABLED -> // Show enable UI
            FilterEngineState.PERMISSION_REQUIRED -> // Show permission guidance
            FilterEngineState.ACTIVE -> // Filter is running
            FilterEngineState.ERROR -> // Show error
        }
    }
}
```

## Performance Considerations

1. **Frame Rate Target**: 30-60 FPS for smooth UI
2. **Resolution Handling**: Downsample before shader processing on high-res displays (tablets)
3. **Battery Safeguards**: 
   - Only process when screen is on
   - Pause during static scenes
   - Auto-disable for video/camera apps (per-app overrides)

## Testing Strategy

- **Unit Tests**: `LegacyPaperFilterTest` validates grayscale/contrast logic
- **Manual Device Testing**: Required for AGSL shader + MediaProjection pipeline
- **Device Matrix**: Test on low/mid/high-end devices for perf validation

## Known Limitations (Phase 0)

1. **Page-turn animation**: Placeholder only, full shader-driven curl effect in Phase 3
2. **Per-app overrides**: Not yet persisted to DataStore
3. **Real-time frame processing**: Uses simulated test bitmap; production MediaProjection integration needs device testing
4. **Dithering**: Only available in rooted Tier 2 (future phase)

## Next Steps (Post-Spike)

1. Test AGSL shader on physical devices (API 33+)
2. Measure frame time on mid-range devices
3. Implement resolution downsampling strategy for tablets
4. Add per-app override persistence via ThemeRepository
5. Implement page-turn shader animation
6. Build Quick Settings Tile for toggle

## Permissions Required

```xml
<!-- Overlay permission -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- Accessibility service (declared in manifest) -->
<uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE" />

<!-- MediaProjection (runtime consent via system dialog) -->
<!-- No manifest declaration needed, requested at runtime -->

<!-- Foreground service (for overlay service) -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

## Manifest Service Declaration

```xml
<service
    android:name=".feature.filterengine.data.FilterOverlayService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

## accessibility_service_config.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/accessibility_service_description"
    android:accessibilityEventTypes="typeWindowStateChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:notificationTimeout="100"
    android:canRetrieveWindowContent="false"
    android:settingsActivity="com.epaperlauncher.feature.settings.SettingsActivity" />
```

**Note**: `canRetrieveWindowContent="false"` is critical for Play Store approval - we only need window state changes, not screen content reading.
