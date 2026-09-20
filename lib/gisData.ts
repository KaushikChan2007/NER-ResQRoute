export interface GisPoint {
  id: string;
  name: string;
  lat: number;
  lng: number;
  elevationMeters: number;
  category: "Relief Depot" | "Field Hospital" | "Mountain Pass" | "Hazard Zone" | "Refuge Camp" | "Helipad & Evac";
  address: string;
  rating: number;
  tags: string[];
  contactPhone: string;
  description: string;
}

export interface HazardIncident {
  id: string;
  title: string;
  type: "LANDSLIDE" | "FLASH_FLOOD" | "ROCKFALL" | "MUD_FLOW";
  severity: "CRITICAL_BLOCKED" | "MODERATE_WARNING" | "ADVISORY";
  accessibilityPercent: number;
  lat: number;
  lng: number;
  highway: string;
  description: string;
  reportedAgo: string;
  radiusMeters: number;
  recommendedDetour: string;
}

export interface RouteOption {
  id: string;
  title: string;
  isAiRecommended: boolean;
  isBlocked: boolean;
  distanceKm: number;
  durationMinutes: number;
  accessibilityScore: number;
  riskSummary: string;
  waypoints: GisPoint[];
  elevationRange: [number, number];
  color: string;
}

export interface MissionCargo {
  id: string;
  missionCode: string;
  title: string;
  origin: string;
  destination: string;
  cargoType: "COLD_CHAIN_MEDICINE" | "CRYOGENIC_OXYGEN" | "EMERGENCY_RATIONS" | "BLOOD_PLASMA";
  priority: "CRITICAL_P1" | "HIGH_P2" | "STANDARD_P3";
  targetTempCelsius?: number;
  assignedConvoy: string;
  etaMinutes: number;
  isDelivered: boolean;
}

export interface ShareContact {
  id: string;
  name: string;
  role: string;
  agency: string;
  phone: string;
  initials: string;
}

export interface OfflineRegion {
  id: string;
  title: string;
  coverage: string;
  sizeMb: number;
  isDownloaded: boolean;
  downloadProgress: number;
  features: string[];
}

export const NER_POINTS: GisPoint[] = [
  {
    id: "GH-01",
    name: "Guwahati Central Disaster Logistics Depot",
    lat: 26.1445,
    lng: 91.7362,
    elevationMeters: 55,
    category: "Relief Depot",
    address: "National Highway 27, Jalukbari, Guwahati, Assam",
    rating: 4.9,
    tags: ["#ColdChainReady", "#Helipad", "#FuelDepot", "#HeavyTruckAccess"],
    contactPhone: "+91 361 257 0122",
    description: "Primary logistics staging warehouse for Assam and Meghalaya relief supplies."
  },
  {
    id: "SH-02",
    name: "Shillong Civil Medical Center & Trauma Care",
    lat: 25.5788,
    lng: 91.8933,
    elevationMeters: 1525,
    category: "Field Hospital",
    address: "GS Road, Police Bazar, Shillong, Meghalaya",
    rating: 4.8,
    tags: ["#ColdChainReady", "#OxygenSupply", "#TraumaICU", "#BloodBank"],
    contactPhone: "+91 364 222 4100",
    description: "High-altitude medical facility receiving urgent insulin, plasma, and oxygen supplies."
  },
  {
    id: "JW-03",
    name: "Jowai Highland Waypoint & Inspection Post",
    lat: 25.4526,
    lng: 92.2036,
    elevationMeters: 1380,
    category: "Mountain Pass",
    address: "NH-6 Bypass, West Jaintia Hills, Meghalaya",
    rating: 4.2,
    tags: ["#4x4Access", "#MeshRelay", "#WeighStation", "#RainGauge"],
    contactPhone: "+91 365 220 8911",
    description: "Highland monitoring post overseeing steep escarpment roads and rainfall runoff."
  },
  {
    id: "SN-04",
    name: "Sonapur Pass & NH-6 Tunnel Hazard Checkpoint",
    lat: 25.0440,
    lng: 92.3680,
    elevationMeters: 420,
    category: "Hazard Zone",
    address: "NH-6 Sonapur Tunnel Km 141, East Jaintia Hills, Meghalaya",
    rating: 2.1,
    tags: ["#MudslideProne", "#0PercentAccess", "#HeavyDebris", "#EmergencyEscort"],
    contactPhone: "+91 365 291 0022",
    description: "CRITICAL BOTTLENECK: 400m mud accumulation. Incline collapsed due to 84mm/h rain."
  },
  {
    id: "SL-05",
    name: "Silchar Relief Forward Base & Medical Depot",
    lat: 24.8333,
    lng: 92.7789,
    elevationMeters: 25,
    category: "Relief Depot",
    address: "Circuit House Road, Tarapur, Silchar, Assam",
    rating: 4.7,
    tags: ["#ColdChainReady", "#OxygenSupply", "#BoatRescue", "#Helipad"],
    contactPhone: "+91 384 224 5510",
    description: "Southern distribution hub serving Barak Valley, Mizoram, and Tripura supply lines."
  },
  {
    id: "UM-06",
    name: "Umrangso Dima Hasao Plateau Refuge Camp",
    lat: 25.5100,
    lng: 92.7400,
    elevationMeters: 710,
    category: "Refuge Camp",
    address: "SH-7 Dima Hasao Highland Corridor, Assam",
    rating: 4.6,
    tags: ["#EmergencyShelter", "#RationsHub", "#4x4Access", "#MedicalTriage"],
    contactPhone: "+91 367 328 1004",
    description: "Strategic high-ground staging post bypassing the flooded southern valleys."
  },
  {
    id: "HF-07",
    name: "Haflong Mountain Ridge Weather Bypass",
    lat: 25.1700,
    lng: 93.0200,
    elevationMeters: 680,
    category: "Mountain Pass",
    address: "Old Jatinga Valley Road, Haflong, Assam",
    rating: 4.4,
    tags: ["#4x4Access", "#HighClearance", "#ScenicEscarpment", "#MeshRelay"],
    contactPhone: "+91 367 323 6050",
    description: "Reinforced ridge road equipped with landslide retention barriers and mesh repeaters."
  },
  {
    id: "TZ-08",
    name: "Tezpur Foothills Base & Air Support Link",
    lat: 26.6338,
    lng: 92.8006,
    elevationMeters: 48,
    category: "Relief Depot",
    address: "Mission Chariali, Tezpur, Assam",
    rating: 4.8,
    tags: ["#Helipad", "#FuelDepot", "#HeavyAirlift", "#ColdChainReady"],
    contactPhone: "+91 371 222 3001",
    description: "Northern plains base supporting helicopter airlift and heavy logistics across Brahmaputra."
  },
  {
    id: "IT-09",
    name: "Itanagar Emergency Center & Base Hospital",
    lat: 27.0844,
    lng: 93.6053,
    elevationMeters: 320,
    category: "Field Hospital",
    address: "NH-415, Niti Vihar, Itanagar, Arunachal Pradesh",
    rating: 4.9,
    tags: ["#OxygenSupply", "#TraumaICU", "#ColdChainReady", "#BloodBank"],
    contactPhone: "+91 360 221 2345",
    description: "Apex healthcare facility for trauma and surgical emergency dispatches."
  },
  {
    id: "BH-10",
    name: "Barak Lowland Emergency Helipad",
    lat: 24.9100,
    lng: 92.6500,
    elevationMeters: 38,
    category: "Helipad & Evac",
    address: "Panchgram Air Strip, Hailakandi Road, Assam",
    rating: 4.5,
    tags: ["#Helipad", "#BoatRescue", "#NightLanding", "#EvacPost"],
    contactPhone: "+91 384 228 0101",
    description: "Helipad for rapid medical evacuations during monsoon flood surges."
  }
];

export const ACTIVE_HAZARDS: HazardIncident[] = [
  {
    id: "HZ-101",
    title: "Severe Monsoon Mudflow & Rockslide",
    type: "LANDSLIDE",
    severity: "CRITICAL_BLOCKED",
    accessibilityPercent: 0,
    lat: 25.0440,
    lng: 92.3680,
    highway: "NH-6 Sonapur Tunnel Km 141",
    description: "400m mud accumulation. Slope collapsed after 84mm/h torrential rain. Heavy convoys prohibited.",
    reportedAgo: "14 mins ago",
    radiusMeters: 800,
    recommendedDetour: "Engage Umrangso-Haflong Highland Bypass (SH-7)"
  },
  {
    id: "HZ-102",
    title: "Barak River Embankment Overflow",
    type: "FLASH_FLOOD",
    severity: "MODERATE_WARNING",
    accessibilityPercent: 42,
    lat: 24.9100,
    lng: 92.6500,
    highway: "NH-37 / Old Cachar Road",
    description: "Water depth 0.6m over road deck. High ground clearance 4x4 military convoys only.",
    reportedAgo: "38 mins ago",
    radiusMeters: 500,
    recommendedDetour: "Maintain 15 km/h with visual ground guides"
  },
  {
    id: "HZ-103",
    title: "Active Rockfall Escarpment",
    type: "ROCKFALL",
    severity: "ADVISORY",
    accessibilityPercent: 78,
    lat: 25.4526,
    lng: 92.2036,
    highway: "NH-6 Jowai Hill Section Km 82",
    description: "Intermittent stone fall on hairpin bend. Road clearing grader on standby.",
    reportedAgo: "1 hr ago",
    radiusMeters: 300,
    recommendedDetour: "Proceed with spotters"
  }
];

export const ROUTE_PLANS: RouteOption[] = [
  {
    id: "RT-SAFE-01",
    title: "AI Weather-Resilient Highland Bypass (Umrangso)",
    isAiRecommended: true,
    isBlocked: false,
    distanceKm: 348.0,
    durationMinutes: 410,
    accessibilityScore: 94,
    riskSummary: "Bypasses Sonapur mudslide via Umrangso & Haflong highland ridges. Road reinforced against monsoons.",
    waypoints: [
      NER_POINTS[0], // Guwahati
      NER_POINTS[1], // Shillong
      NER_POINTS[5], // Umrangso
      NER_POINTS[6], // Haflong
      NER_POINTS[4], // Silchar
    ],
    elevationRange: [25, 1525],
    color: "#1B8A5A"
  },
  {
    id: "RT-BLOCKED-02",
    title: "NH-6 Direct Sonapur Corridor (IMPASSABLE)",
    isAiRecommended: false,
    isBlocked: true,
    distanceKm: 312.4,
    durationMinutes: 720,
    accessibilityScore: 24,
    riskSummary: "CRITICAL: Sonapur Tunnel completely obstructed by 400m mudslide. NHAI clearance est: 28-36 hours.",
    waypoints: [
      NER_POINTS[0], // Guwahati
      NER_POINTS[1], // Shillong
      NER_POINTS[2], // Jowai
      NER_POINTS[3], // Sonapur
      NER_POINTS[4], // Silchar
    ],
    elevationRange: [25, 1525],
    color: "#C53030"
  }
];

export const INITIAL_MISSIONS: MissionCargo[] = [
  {
    id: "M-01",
    missionCode: "MED-NER-01",
    title: "Cold-Chain Insulin & Pediatric Vaccines",
    origin: "Guwahati Central Depot",
    destination: "Shillong Civil Medical Center",
    cargoType: "COLD_CHAIN_MEDICINE",
    priority: "CRITICAL_P1",
    targetTempCelsius: 3.5,
    assignedConvoy: "Convoy Delta-1 (Refrigerated 4x4)",
    etaMinutes: 80,
    isDelivered: false
  },
  {
    id: "M-02",
    missionCode: "OXY-NER-02",
    title: "Emergency Cryogenic Oxygen Cylinders",
    origin: "Tezpur Foothills Base",
    destination: "Silchar Relief Base Hospital",
    cargoType: "CRYOGENIC_OXYGEN",
    priority: "CRITICAL_P1",
    assignedConvoy: "Convoy Bravo-3 (Hazard Certified)",
    etaMinutes: 195,
    isDelivered: false
  },
  {
    id: "M-03",
    missionCode: "RAT-NER-03",
    title: "High-Energy Ready Rations & Water Purification",
    origin: "Guwahati Central Depot",
    destination: "Umrangso Dima Hasao Refuge Camp",
    cargoType: "EMERGENCY_RATIONS",
    priority: "HIGH_P2",
    assignedConvoy: "Convoy Echo-4 (Heavy All-Terrain)",
    etaMinutes: 240,
    isDelivered: false
  }
];

export const SHARE_CONTACTS: ShareContact[] = [
  {
    id: "C-01",
    name: "State Disaster Management Cell (NER-HQ)",
    role: "Central Operations Dispatcher",
    agency: "State Disaster Management Authority",
    phone: "+91 361 223 7000",
    initials: "HQ"
  },
  {
    id: "C-02",
    name: "Major Vikram Sen",
    role: "Convoy Delta Lead Escort",
    agency: "Border Road Logistics Unit",
    phone: "+91 943 512 8840",
    initials: "VS"
  },
  {
    id: "C-03",
    name: "Dr. Ananya Roy",
    role: "Chief Medical Officer",
    agency: "Shillong Trauma & Civil Hospital",
    phone: "+91 986 401 2299",
    initials: "AR"
  }
];

export const OFFLINE_REGIONS: OfflineRegion[] = [
  {
    id: "REG-01",
    title: "Barak Valley & Cachar Corridor",
    coverage: "Silchar, Hailakandi, Karimganj",
    sizeMb: 48.5,
    isDownloaded: true,
    downloadProgress: 100,
    features: ["High-res Topo Vectors", "Flood Embankment Nodes", "Offline Routing Index"]
  },
  {
    id: "REG-02",
    title: "Dima Hasao Highland Region",
    coverage: "Haflong, Umrangso, Jatinga",
    sizeMb: 62.0,
    isDownloaded: true,
    downloadProgress: 100,
    features: ["Escarpment Gradients", "SH-7 Bypass Nodes", "Offline AI Risk Engine"]
  },
  {
    id: "REG-03",
    title: "East Khasi & Jaintia Escarpment",
    coverage: "Shillong, Jowai, Sonapur",
    sizeMb: 74.2,
    isDownloaded: false,
    downloadProgress: 0,
    features: ["NH-6 Vulnerability Zones", "Civil Hospital Geofences", "Monsoon Runoff Maps"]
  }
];
