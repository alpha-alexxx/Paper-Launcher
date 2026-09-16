# E-Paper Launcher

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-pink.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-black.svg)](LICENSE)

> **Transform your Android device into a calm, distraction-free e-ink reader.**  
> A minimalist launcher that reskins your entire interface with warm paper tones, flat grayscale surfaces, and typography-led design. Inspired by Kindle aesthetics and Niagara's efficiency.

---

## 📱 Features

### Core Experience
- **Typography-First Home Screen**: Vertical text list with alphabet scrubber (Niagara-inspired)
- **E-Ink Visual Language**: Warm cream background, flat grayscale palette, hairline dividers
- **System-Wide Filter**: Optional full-screen paper overlay via Accessibility Service + MediaProjection
- **Custom Icon Engine**: Automatic conversion of app icons to monochrome/line-art/stamp styles
- **Paper Widgets**: First-party weather, calendar, and todo widgets styled in paper theme
- **Adaptive Layout**: Seamless experience on phones, tablets, and foldables

### Design Philosophy
- **No Shadows, No Gradients**: Pure flat surfaces with 1dp hairline borders
- **Warm Paper Tone**: `#F5F1E8` cream background with `#2B2A28` ink text
- **Subtle Grain Texture**: Optional noise overlay for authentic paper feel
- **Clean Typography**: Sans-serif font stack optimized for readability
- **Minimal Animations**: Fast-out-slow-in transitions, optional page-turn effects

### Monetization
- **Free Tier**: Basic launcher, single icon style, accessibility-level grayscale
- **Pro Tier** (One-time/Subscription): System-wide filter, all icon styles, premium widgets, advanced grain textures

---

## 🏗 Architecture

Built with **Clean Architecture** and **MVVM** patterns across 10 modular components:

```
e-paper-launcher/
├── app/                      # Application shell, DI graph root
├── core/
│   ├── core-ui/              # Design system (tokens, typography, components)
│   ├── core-data/            # Repositories, Room DB, DataStore
│   └── core-common/          # Utils, dispatchers, base classes
├── feature/
│   ├── launcher-home/        # Home screen, app list, search
│   ├── launcher-drawer/      # App drawer, all-apps view
│   ├── filter-engine/        # Accessibility service, overlay rendering
│   ├── icon-engine/          # Icon processing pipeline
│   ├── widgets/              # Paper-styled first-party widgets
│   └── settings/             # Theme config, billing, permissions
└── buildSrc/                 # Gradle convention plugins
```

### Tech Stack
| Layer | Technology |
|-------|------------|
| **Language** | Kotlin 2.0.20 |
| **UI** | Jetpack Compose + Material3 Adaptive |
| **DI** | Hilt 2.52 |
| **Local DB** | Room 2.6.1 |
| **Preferences** | Proto DataStore 1.1.1 |
| **Async** | Coroutines + Flow |
| **Image Processing** | AGSL Shaders (API 33+), ColorMatrix |
| **Background Work** | WorkManager 2.9.1 |
| **Billing** | Play Billing Library 7.0.0 |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | Latest Stable |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 17 or higher
- **Android SDK**: API 26+ (Tested on API 34)
- **Physical Device**: Recommended for testing Accessibility Services and MediaProjection

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/e-paper-launcher.git
   cd e-paper-launcher
   ```

2. **Sync Gradle**
   - Open project in Android Studio
   - Wait for Gradle sync to complete (downloads dependencies automatically)
   - Ensure you have internet connection for first sync

3. **Build Configuration**
   - Select `standard` flavor for Play Store build (Tier 1 filter only)
   - Select `root` flavor for side-load build (includes Tier 2 rooted filter)
   - Update `gradle.properties` if custom signing is needed

4. **Run on Device**
   ```bash
   ./gradlew installStandardDebug
   ```
   > ⚠️ **Note**: Emulators may not fully support Accessibility Overlay or MediaProjection. Physical device recommended.

---

## 🔐 Permissions & Setup

### Required Permissions (Granted at Runtime)
| Permission | Purpose | When Requested |
|------------|---------|----------------|
| `QUERY_ALL_PACKAGES` | List installed apps | On first launch |
| `SYSTEM_ALERT_WINDOW` | Render filter overlay | When enabling system filter |
| `MediaProjection` | Capture screen for filter | When enabling system filter |
| `Accessibility Service` | Detect app switches, render overlay | User must enable in Settings |

### First-Time Setup Flow
1. **Launch App**: Grant app list permission
2. **Set as Default Launcher**: Android system prompt
3. **Enable Accessibility Service**: 
   - Navigate to Settings → Accessibility → E-Paper Filter
   - Toggle service ON
4. **Grant Screen Capture** (Optional Pro Feature):
   - Enable "System-Wide Filter" in app settings
   - Accept MediaProjection dialog
5. **Customize Theme**: Adjust paper tone, grain, contrast in Settings

> 📝 **Play Store Compliance**: Accessibility Service usage requires declaration form submission. MediaProjection capability must be disclosed in Data Safety section.

---

## 🎨 Design System

### Color Tokens
```kotlin
// Warm Paper Tone (Default)
Background: #F5F1E8  // Cream paper
Surface:   #EDE8DE  // Slightly darker paper
Ink:       #2B2A28  // Primary text
Muted:     #6B6862  // Secondary text
Divider:   #D8D3C7  // Hairline borders
Accent:    #8A7F6A  // Focus states (subtle)
```

### Typography
- **Primary Font**: System sans-serif (optimized for screen readability)
- **Scale**: 
  - Display Large: 34sp (Headers)
  - Title: 22sp (Section headers)
  - Body: 17sp (App names, content)
  - Label: 13sp (UI chrome, buttons)

### Spacing & Shapes
- **Grid**: 8dp base unit (4, 8, 16, 24, 32, 48)
- **Corners**: 0–2dp (Sharp, page-like edges)
- **Elevation**: None (Replaced by 1dp hairline dividers)
- **Motion**: 220ms fade + 8dp drift (No scale/bounce)

---

## 🧪 Testing

### Run Tests
```bash
# Unit Tests
./gradlew testStandardDebugUnitTest

# Instrumented Tests (Requires device/emulator)
./gradlew connectedStandardDebugAndroidTest

# Lint Check
./gradlew lintStandardDebug
```

### Test Coverage
- **Domain Layer**: Use case logic, sorting, search algorithms
- **Repository Layer**: Room DB operations, DataStore persistence
- **ViewModel Layer**: State transitions, error handling
- **UI Layer**: Compose rendering, interaction tests
- **Golden Images**: Icon processing visual regression

---

## 📦 Building for Release

### Generate Signed APK/AAB
```bash
# Debug Build
./gradlew assembleStandardDebug

# Release Build (Requires signing config)
./gradlew bundleStandardRelease
```

### Flavor Matrix
| Flavor | Distribution | Filter Tier | Root Required |
|--------|--------------|-------------|---------------|
| `standard` | Play Store | Tier 1 (Overlay) | No |
| `root` | Side-load/F-Droid | Tier 2 (Compositor Hook) | Yes |

### ProGuard Rules
Included in `app/proguard-rules.pro`:
- Keep Hilt generated classes
- Preserve Room entities
- Protect Billing Library classes
- Optimize AGSL shader references

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork the Repo** and create a feature branch
2. **Follow Architecture**: Maintain Clean Architecture boundaries
3. **Write Tests**: New features require unit tests
4. **Design Consistency**: Adhere to paper design tokens
5. **Submit PR**: Include description, screenshots, and test results

### Code Style
- **Kotlin**: Official coding conventions
- **Commit Messages**: Conventional Commits format
- **Documentation**: KDoc for public APIs

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### Third-Party Licenses
- **Source Serif 4 / Inter Fonts**: SIL Open Font License
- **Material Icons**: Apache License 2.0
- **Play Billing Library**: Proprietary (Google)

---

## 🙏 Acknowledgments

- **Niagara Launcher**: Inspiration for text-first home screen
- **Kindle Paperwhite**: Visual aesthetic reference
- **Android Open Source Project**: Base launcher frameworks
- **Jetpack Compose Team**: Modern UI toolkit

---

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/yourusername/e-paper-launcher/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/e-paper-launcher/discussions)
- **Email**: support@epaperlauncher.dev
- **Privacy Policy**: [Link to hosted policy]

---

<div align="center">

**Made with ❤️ for digital minimalism**  
*Reduce distraction. Focus on what matters.*

[![Star](https://img.shields.io/github/stars/yourusername/e-paper-launcher?style=social)](https://github.com/yourusername/e-paper-launcher/stargazers)
[![Fork](https://img.shields.io/github/forks/yourusername/e-paper-launcher?style=social)](https://github.com/yourusername/e-paper-launcher/network/members)

</div>
