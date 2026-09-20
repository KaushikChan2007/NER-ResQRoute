# NER-ResQRoute 🏔️🚨

**Disaster-Aware GIS Logistics & Dynamic Route Intelligence Platform for Rugged Terrain and Essential Supply Missions**  
*Developed for Smart India Hackathon (SIH 2026) — BitForge Technical Approach*

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https%3A%2F%2Fgithub.com%2FKaushikChan2007%2FNER-ResQRoute)
[![GitHub Actions](https://github.com/KaushikChan2007/NER-ResQRoute/actions/workflows/build.yml/badge.svg)](https://github.com/KaushikChan2007/NER-ResQRoute/actions)

---

## 🎯 System Overview (SIH 2026 Architecture)

NER-ResQRoute is a full-stack, disaster-resilient GIS logistics and dynamic route intelligence platform designed for the complex, landslide-prone topography of the North-Eastern Region (Assam, Meghalaya, Barak Valley, Arunachal frontier).

When monsoonal cloudbursts sever primary corridors like the **NH-6 Sonapur Tunnel section (400m mudflow)**, NER-ResQRoute empowers field officers, convoy drivers, and command operators to recalculate safe detours, track temperature-critical supplies, and maintain encrypted offline communications.

```text
========================================================================================
FOUR-LEVEL SYSTEM FLOW (BitForge Architecture)
========================================================================================

LEVEL 1 | INPUT & ACCESS
  ├─ Field Officer PWA (React 18 + Tailwind + TS)
  │    └─ IndexedDB offline cache, camera geotagging, AES-256-GCM sealed field reports
  └─ Command Operator Web Portal (Leaflet JS + TS)
       └─ Live GIS visualization, fleet telemetry tracking, strategic depot inspection

LEVEL 2 | AI & DECISION ENGINE
  ├─ A* / Dijkstra Route Optimizer (Weighted by Slope Gradient + Monsoonal Rainfall Mm/h)
  ├─ Dynamic Accessibility Scoring (Sonapur: 0% Blocked vs Umrangso Bypass: 94% Clear)
  └─ Gemini 3.5 Flash Tactical Dispatch Chatbot (Context-aware conversational booking)

LEVEL 3 | RESPONSE & ALERT ECOSYSTEM
  ├─ Proximity Hazard Radar (Tactile & visual alerts when convoy approaches mudslides)
  ├─ Battery-Efficient Real-Time Location Sharing (Enforced duration limits & kill switch)
  └─ Priority Cargo Monitoring (Cold-chain insulin, cryogenic oxygen, blood plasma)

LEVEL 4 | TRUST LAYER & OFFLINE RESILIENCE
  ├─ Encrypted Incident Store (Web Crypto API AES-256-GCM)
  ├─ Downloadable Regional Map Caches (Barak Valley, Dima Hasao, East Khasi)
  └─ Serverless Cloud Deployment (Vercel + GitHub CI/CD)
========================================================================================
```

---

## 🌟 Key Capabilities

### 1. 📱 Field Officer PWA (Mobile-First Touch & Offline-First)
- **Zero-Connectivity Resilience**: Field officers can log landslide disruptions, select highway corridors, set impact severities, and capture geotagged field notes without cellular connectivity.
- **Hardware-Accelerated AES-256-GCM Sealing**: Notes are encrypted locally using the Web Crypto API before being stored in an IndexedDB offline queue.
- **One-Click Mesh Sync**: Flushes the local offline queue to the verified incident store once network connectivity returns.

### 2. 💻 Command Operator Web Portal (Leaflet GIS)
- **Fluid Interactive GIS Canvas**: High-performance Leaflet map featuring OpenStreetMap & Carto Voyager tiles, custom depot/hospital markers, and real-time vehicle telemetry.
- **Live Corridor Comparison**:
  - **Direct NH-6 Corridor**: Red dashed obstruction line through Sonapur Tunnel (24% average accessibility, 0% at blockage).
  - **AI Weather-Resilient Highland Bypass**: Green solid line through Umrangso and Haflong ridges (94% accessibility).
- **Proximity Hazard Radar**: Audio-visual alert banner when convoy telemetry approaches active landslide perimeters.

### 3. 🧠 A* / Dijkstra Route Optimization Engine
- Mathematically evaluates road graph edges using:
  $$\text{Cost} = \text{Distance (km)} \times \left(1 + \frac{\text{Slope}\%}{100}\right) \times \left(1 + \frac{\text{Rainfall (mm/h)}}{60}\right) + \text{Obstruction Penalty}$$
- Automatically diverts high-priority medical supplies away from dangerous low-lying mudflows onto reinforced high-elevation plateaus.

### 4. 🤖 Context-Aware Tactical Dispatch Chatbot (Gemini 3.5 Flash)
- Maintains complete multi-turn conversation memory.
- Guides operators step-by-step through:
  1. Specifying cargo (Cold-chain insulin/vaccines, cryogenic oxygen, blood units).
  2. Confirming origin and destination facilities.
  3. Warning about active landslide bottlenecks and routing via safe detours.
  4. Generating official dispatch reference codes (`NER-DISPATCH-XXXX`).
- Intelligent local fallback engine if offline or if no API key is supplied.

### 5. 📡 Battery-Efficient Location Sharing & Instant Kill Switch
- Broadcasts live GPS coordinates to verified agency commanders (State Disaster Management Authority, Convoy Lead, Chief Medical Officer).
- Enforced session timers (15m, 1h, 4h, 8h).
- **Battery-Saver Mesh Beacon Mode**: Transmits in 30-second bursts, saving up to 85% battery during multi-day expeditions.
- Red **"Revoke / Go Dark"** kill switch instantly halts broadcast.

### 6. 🎨 Earthy Luxe UI & Accessibility (WCAG 2.1 AAA)
- Warm parchment, saddle brown, desert sand, and bronze in Light Mode; roasted espresso in Dark Mode.
- Fully responsive across desktop browsers, laptops, tablets, and smartphones.

---

## 🚀 1-Click Deployment to Vercel (Free)

Deploying NER-ResQRoute to Vercel takes under 2 minutes:

### Step 1: Import into Vercel
1. Go to **[https://vercel.com/new](https://vercel.com/new)**.
2. Sign in with GitHub.
3. Select the repository: **`KaushikChan2007/NER-ResQRoute`**.
4. Framework Preset will automatically detect **Next.js**.

### Step 2: Add Environment Variable
In the **Environment Variables** section, add:
- **Key**: `GEMINI_API_KEY`
- **Value**: *(Paste your Gemini API key)*

### Step 3: Deploy
1. Click **Deploy**.
2. Vercel will build and deploy the application worldwide.
3. You will receive a live URL (e.g. `https://ner-res-q-route.vercel.app`) to submit in your SIH 2026 presentation!

---

## 💻 Local Development Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/KaushikChan2007/NER-ResQRoute.git
   cd NER-ResQRoute
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Configure Environment Secrets**:
   Copy `.env.example` to `.env.local`:
   ```bash
   cp .env.example .env.local
   ```
   Add your Gemini API Key:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

4. **Run development server**:
   ```bash
   npm run dev
   ```
   Open [http://localhost:3000](http://localhost:3000) in your browser.

5. **Build for production**:
   ```bash
   npm run build
   ```

---

## 📂 Repository Structure

```
NER-ResQRoute/
├── app/
│   ├── api/
│   │   ├── chat/route.ts                 # Gemini 3.5 Flash serverless endpoint
│   │   ├── route-optimize/route.ts       # A* / Dijkstra route optimizer
│   │   └── hazards/route.ts              # Incident feeds API
│   ├── globals.css                       # Tailwind + Leaflet earthy luxe styles
│   ├── layout.tsx                        # Root layout & PWA metadata
│   └── page.tsx                          # Dual persona application interface
├── components/
│   ├── LeafletGisMap.tsx                 # Interactive Leaflet GIS map
│   ├── FieldOfficerPwa.tsx               # Mobile offline reporting PWA
│   ├── TacticalChatbot.tsx               # Context-aware dispatch chatbot
│   ├── LocationShareModal.tsx            # Battery-efficient location sharing
│   └── OfflineCacheModal.tsx             # Downloadable regional map packages
├── lib/
│   ├── gisData.ts                        # NER nodes, waypoints, hazards, hubs
│   ├── routingEngine.ts                  # A* / Dijkstra graph algorithm
│   ├── crypto.ts                         # Web Crypto API AES-256-GCM sealing
│   └── indexedDb.ts                      # Client-side IndexedDB offline queue
├── public/
│   ├── manifest.json                     # PWA web manifest
│   └── icons/
├── android/                              # Native Android app codebase (preserved)
├── package.json                          # Next.js 14 & React 18 configuration
├── tailwind.config.ts                    # Earthy Luxe color palette
├── vercel.json                           # Vercel deployment configuration
├── .gitignore                            # Comprehensive security rules
└── README.md
```

---

## 🛡️ Security & Data Privacy Policy

- **Zero Client Credential Leakage**: The Gemini API key is never exposed to browser clients; requests are processed securely through Vercel serverless edge functions.
- **Local Data Confidentiality**: Reconnaissance notes and officer reports are sealed using client-side AES-256-GCM before storage.
- **Location Privacy**: Location sharing requires explicit duration limits and can be terminated instantly with the "Revoke / Go Dark" kill switch.

---

## 📜 License

Licensed under the Apache License 2.0.
