# Settings Module - Phase 3 Implementation

## Overview
The settings module provides the complete theming and configuration UI for the E-Paper Launcher, allowing users to customize:
- Paper tone (Warm/Cold/Pure White)
- Grain intensity (0–30%)
- Contrast preset (Low/Standard/High/Extra High)
- Icon style (Flat Monochrome/Line Art/Stamp)
- Page turn animation toggle
- Auto day/night switch toggle
- System-wide filter enable/disable (Pro feature)
- Per-app filter overrides

## Architecture

### Package Structure
```
feature/settings/
├── build.gradle.kts                          # Module dependencies
├── src/main/
│   ├── java/com/epaperlauncher/feature/settings/
│   │   ├── di/
│   │   │   └── SettingsModule.kt             # Hilt DI for ViewModel + DataStore
│   │   └── presentation/
│   │       ├── SettingsViewModel.kt          # Main settings VM
│   │       ├── components/
│   │       │   └── SettingsScreen.kt         # Compose UI with all sections
│   │       ├── theme/                        # (Future: theme-specific screens)
│   │       └── filter/                       # (Future: filter-specific screens)
│   └── res/values/
│       └── strings.xml                       # Localized strings
└── src/test/
    └── java/com/epaperlauncher/feature/settings/
        └── SettingsViewModelTest.kt          # Unit tests
```

## Key Components

### SettingsViewModel
- Collects state from three repositories via `combine`:
  - `ThemeRepository` → `ThemeConfig`
  - `FilterEngineController` → `FilterEngineState`
  - `BillingRepository` → Pro entitlement status
- Exposes `SettingsUiState` as `StateFlow`
- Provides mutation methods for all configurable options
- Handles system-wide filter toggle with permission checks

### SettingsScreen Composable
- Single-file implementation with all UI sections
- Uses paper-themed design tokens (`PaperTokens`, `Spacing`)
- Sections:
  1. **Paper Tone**: LazyRow of selectable chips with preview backgrounds
  2. **Grain Intensity**: Slider with percentage display (0–30%, 5 steps)
  3. **Contrast**: LazyRow of preset chips
  4. **Icon Style**: LazyRow of style chips
  5. **Display Options**: Two toggle rows (page turn, auto day/night)
  6. **System-Wide Filter**: Status text + toggle, Pro gating, permission warnings
  7. **Upgrade to Pro**: Billing section (hidden if already unlocked)

### DataStore Integration
- Uses Proto DataStore for type-safe `ThemeConfig` persistence
- Custom `ThemeConfigProtoSerializer` handles protobuf serialization
- Corruption handler resets to defaults on read failure
- File stored at `{filesDir}/theme_config.pb`

## Dependencies
- `core:core-data` → Repository interfaces, `ThemeConfig`, `FilterEngineState`
- `core:core-ui` → `PaperTokens`, `Spacing` design tokens
- `feature:filter-engine` → `FilterEngineControllerImpl` binding
- `feature:icon-engine` → Future icon reprocessing integration
- Hilt, Compose, Navigation, DataStore, Coroutines

## Testing Strategy
- **Unit Tests** (`SettingsViewModelTest`):
  - Initial state validation
  - Mutation method verification (paper tone, grain, contrast, toggles)
  - Filter enable/disable flow
  - Per-app override updates
- **Compose UI Tests** (future): Screen rendering, interaction, state updates
- **Manual Testing**: Permission flows, Pro gating, real device filter behavior

## Usage Example

```kotlin
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onPaperToneChanged = viewModel::setPaperTone,
        onGrainIntensityChanged = viewModel::setGrainIntensity,
        onContrastChanged = viewModel::setContrastPreset,
        onPageTurnAnimationToggled = viewModel::togglePageTurnAnimation,
        onAutoDayNightToggled = viewModel::toggleAutoDayNight,
        onIconStyleChanged = viewModel::setIconStyle,
        onSystemWideFilterToggled = viewModel::toggleSystemWideFilter,
        onRestorePurchases = viewModel::restorePurchases
    )
}
```

## Permissions & Consent Flow
The settings screen guides users through required permissions for the system-wide filter:
1. **Overlay Permission**: Checked via `hasOverlayPermission()` → launches `ACTION_MANAGE_OVERLAY_PERMISSION` intent if missing
2. **Accessibility Service**: Checked via `isAccessibilityServiceEnabled()` → launches `ACTION_ACCESSIBILITY_SETTINGS` if disabled
3. **MediaProjection**: Requested by `FilterOverlayService` when filter is first enabled (system dialog, must be re-prompted each session per Android OS design)

All consent flows are clearly explained in the UI before triggering system dialogs (Play Store policy requirement).

## Pro Tier Gating
- System-wide filter toggle is disabled + shows "Pro tier required" message if `isProUnlocked == false`
- Billing section appears only when Pro is not unlocked
- Upgrade button triggers `BillingRepository.launchPurchaseFlow(activity)` (stubbed in Phase 3, implemented in Phase 6)

## Known Limitations (Phase 3)
1. **Icon reprocessing**: Changing icon style updates `ThemeConfig` but does not yet trigger bulk reprocessing of cached icons (TODO comment in ViewModel)
2. **Per-app overrides**: Stored in `ThemeConfig` map but UI for managing per-app list not yet built
3. **Purchase flow**: `launchPurchaseFlow` is stubbed; full Play Billing integration in Phase 6
4. **Tablet layout**: Settings screen uses single-column `LazyColumn`; could benefit from two-pane layout on tablets (left rail: categories, right pane: settings)

## Next Steps (Post-Phase 3)
1. Add per-app filter override management screen (list of installed apps with toggles)
2. Implement icon reprocessing pipeline when style changes
3. Integrate Play Billing Library for Pro upgrade
4. Add tablet-optimized two-pane settings layout
5. Quick Settings Tile for filter toggle (direct access without opening app)
6. Export/import theme config backup (JSON or proto file)
