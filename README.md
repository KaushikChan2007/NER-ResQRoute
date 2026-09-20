# NER-ResQRoute 🏔️🚨

**Disaster-Aware GIS Logistics & Dynamic Route Intelligence Platform for Rugged Terrain and Essential Supply Missions**

NER-ResQRoute is an Android application designed for high-stakes emergency logistics across the North-Eastern Region (NER) of India (Assam, Meghalaya, Barak Valley, Arunachal frontier). When monsoonal landslides and flash floods sever primary arteries like the NH-6 Sonapur corridor, NER-ResQRoute empowers disaster coordinators, convoy drivers, and hospital medical teams with real-time geospatial intelligence, encrypted communication, and resilient offline routing.

---

## 🎯 Problem Statement

Logistics operations in the North Eastern Region face recurring critical challenges:
- **Mountainous & Vulnerable Terrain**: Steep gradients and fragile slopes subject to monsoon landslides.
- **Dynamic Road Accessibility**: A route that is passable at dawn can be obstructed by a 400m mudflow minutes later.
- **Limited Real-Time Visibility**: Delayed on-ground reporting between remote checkpoints and central command.
- **Critical Mission Logistics**: Transporting temperature-sensitive cold-chain cargo (insulin, pediatric vaccines, blood plasma) and emergency oxygen under severe road vibration and delays.
- **Zero-Connectivity Blackouts**: Severe cellular dropouts in high-altitude passes demanding local cryptographic storage and offline functionality.

NER-ResQRoute creates a continuously updated **road accessibility, mission intelligence, and disaster resilience layer**.

---

## 💡 The Solution

NER-ResQRoute creates a dynamic logistics decision engine combining:
$$\text{Accessibility} = f(\text{GIS} + \text{GPS} + \text{Weather Risk} + \text{Field Telemetry} + \text{Mission Priority})$$

### 🔄 Resilience Pathway
```text
INGEST (GIS Data + GPS + Weather + Field Reports)
   ↓
ASSESS (Slope Gradients + Monsoonal Rainfall + Obstruction Radius)
   ↓
OPTIMIZE (AI Weather-Resilient Highland Detour Calculation)
   ↓
ALERT (Proximity Radars + Haptic Feedback + Command Broadcasts)
   ↓
MONITOR (Cold-Chain Sensor Pods + Live Telemetry Tracking)
   ↓
LEARN (Post-Disaster Mesh Sync + Heuristic Feedback)
```

---

## 🌟 Key Features

### 1. 🗺️ Interactive GIS Terrain & Dynamic Route Orchestration
- **Intuitive Touch Map**: Fluid pinch-to-zoom (0.65x to 3.5x), panning, and recentering on live convoy GPS.
- **Topographic Elevation Contours & River Ribbons**: Real-time visualization of Khasi-Jaintia and Barail mountain ranges, alongside the Brahmaputra and Barak river basins.
- **Dynamic Route Orchestration**: Live comparison between the active landslide-blocked corridor (NH-6 Sonapur Tunnel with 24% accessibility) and the AI-recommended resilient bypass (Umrangso Dima Hasao Plateau with 94% accessibility).
- **Proximity Hazard Radar**: Audio-visual and tactile haptic warnings when approaching active hazard perimeters.

### 2. 🔍 Advanced Map Search & Multi-Criterion Filtering
- **Multi-Criterion Query Engine**: Search across addresses, relief depots, trauma hospitals, mountain passes, and helipads.
- **Faceted Filters**: Filter by facility category, minimum star rating (3.5★ – 4.9★), and operational tags (`#ColdChainReady`, `#OxygenSupply`, `#4x4Access`, `#Helipad`, `#MeshRelay`).
- **Synchronized Viewports**: Matching locations are highlighted directly on the map canvas with glowing tactical halos, accompanied by an expandable list view overlay.

### 3. 📡 Battery-Efficient Real-Time Location Sharing
- **Granular Privacy & Duration Controls**: Choose specific verified command contacts (State Disaster Management Cell, Convoy Lead, Chief Medical Officer).
- **Enforced Time Limits**: Set broadcast limits (15 min, 1 hr, 4 hrs, 8 hrs) with automated expiration and an instant **"Revoke / Go Dark"** kill switch.
- **Battery-Saver Mesh Beacon Mode**: Toggle between 5-second real-time GPS streaming and a 30-second battery-saving mesh beacon that saves up to 85% battery during multi-day expeditions.
- **End-to-End Encryption**: Coordinates are sealed with AES-256-GCM before transmission.

### 4. 💾 Offline Regional Map Caching
- **Encrypted Local Storage**: Download and store high-resolution regional vector maps (Barak Valley, Dima Hasao Highland, East Khasi & Jaintia, Brahmaputra Valley) in local Room SQLite storage.
- **Zero-Connectivity Independence**: Cached packages preserve contour elevation vectors, hospital geofences, and the offline heuristic detour reasoning engine.

### 5. 🤖 Context-Aware Tactical Support & Booking Chatbot
- **Conversational Memory**: Powered by Google Gemini models with full multi-turn context retention.
- **Multi-Step Logistics Bookings**: Guides coordinators step-by-step through cargo selection (cold-chain insulin, cryogenic oxygen, blood units), route confirmation around active roadblocks, and convoy vehicle assignment.
- **Automatic Mission Dispatch**: Finalized bookings are inserted directly into the encrypted local mission database.
- **Resilient Fallback**: Seamless transition to a rule-based tactical engine when offline.

### 6. 🔒 Enterprise-Grade Security & Privacy
- **Hardware-Backed AES-256-GCM Encryption**: All on-ground hazard reports, tactical notes, and location caches are encrypted via `AndroidKeyStore`.
- **Secrets Gradle Plugin**: Strict separation of credentials. API keys are injected via `BuildConfig` at compile time and never committed to source control.

### 7. 🎨 Earthy Luxe UI & Accessibility (WCAG 2.1 AAA)
- **Palette**: Warm sand, parchment, and deep umber tones in Light Mode; rich roasted espresso in Dark Mode.
- **Accessibility**: High contrast (>7:1 ratio), subtle vestibular-safe animations, and multi-pattern tactile haptic feedback.
- **Gesture Navigation**: Swipeable horizontal pager across Map, Missions, Reports, and Command Center.

---

## 🏗️ Architecture & Technology Stack

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: Modern Android Architecture (MVVM, StateFlow, Coroutines)
- **Local Persistence**: Room Database (with AES-GCM encrypted fields)
- **Security**: Android KeyStore API, AES-256-GCM (`CryptoManager`)
- **Geospatial & GPS**: Google Play Services Location (`FusedLocationProviderClient`)
- **AI Intelligence**: Gemini 3.5 Flash REST API (via OkHttp & Moshi)
- **Configuration Management**: Secrets Gradle Plugin (`.env` and `.env.example`)
- **Testing**: JUnit4, Robolectric, Roborazzi screenshot tests

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- Android SDK 36 (Minimum SDK 24)
- JDK 17 or higher

### Installation & Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/KaushikChan2007/NER-ResQRoute.git
   cd NER-ResQRoute
   ```

2. **Configure Environment Secrets**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Open `.env` and add your Gemini API Key:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
   > **Note**: `.env` is strictly ignored by `.gitignore` and will never be committed to Git.

3. **Build & Run**:
   - Open the project in Android Studio.
   - Sync Gradle project with build files.
   - Select an emulator or physical Android device (Android 7.0+ / API 24+).
   - Press **Run ▶**.

### Running Tests
Execute local unit and Robolectric tests:
```bash
./gradlew testDebugUnitTest
```

---

## 📂 Project Structure

```
NER-ResQRoute/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Root Activity & Permissions
│   │   │   │   ├── ai/
│   │   │   │   │   ├── GeminiRouteAdvisor.kt     # Disaster routing advisor
│   │   │   │   │   └── GeminiChatbotManager.kt   # Context-aware booking agent
│   │   │   │   ├── data/local/
│   │   │   │   │   ├── Entities.kt               # Room entities
│   │   │   │   │   ├── ResQRouteDao.kt           # Room DAO
│   │   │   │   │   ├── ResQRouteDatabase.kt      # Room Database
│   │   │   │   │   └── ResQRouteRepository.kt    # Encrypted data repository
│   │   │   │   ├── gis/
│   │   │   │   │   ├── GisModels.kt              # Data structures & search states
│   │   │   │   │   ├── GisDataProvider.kt        # NER geospatial points & routes
│   │   │   │   │   └── LocationTracker.kt        # GPS & telemetry engine
│   │   │   │   ├── security/
│   │   │   │   │   └── CryptoManager.kt          # AES-256-GCM KeyStore encryption
│   │   │   │   └── ui/
│   │   │   │       ├── components/               # Reusable map & dialog components
│   │   │   │       ├── screens/                  # 4 main tabs (Map, Missions, Reports, Command)
│   │   │   │       ├── theme/                    # Earthy luxe colors, typography & theme
│   │   │   │       └── viewmodel/                # Central ResQRouteViewModel
│   │   │   ├── res/                              # Drawables, mipmaps, strings, layouts
│   │   │   └── AndroidManifest.xml
│   │   └── test/                                 # Unit & Robolectric test suite
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                        # Version catalog
├── .env.example                                  # Template for environment secrets
├── .gitignore                                    # Protected files & build outputs
└── README.md
```

---

## 🛡️ Privacy & Security Best Practices

- **Zero Hardcoded Secrets**: Secrets Gradle Plugin automatically reads `.env` and provides `BuildConfig.GEMINI_API_KEY`.
- **Local Data Sealing**: Tactical field reports and offline vector caches are stored encrypted using device hardware-backed keys.
- **Location Privacy**: Location sharing sessions require explicit duration limits and can be terminated instantly by the operator.

---

## 📜 License

This project is licensed under the Apache License 2.0.
