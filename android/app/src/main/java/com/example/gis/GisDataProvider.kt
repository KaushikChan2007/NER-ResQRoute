package com.example.gis

object GisDataProvider {

  // Regional Strategic Hubs across North-Eastern Region (NER) with addresses, ratings, and tags
  val GUWAHATI_DEPOT = GisPoint(
    id = "GH-01",
    name = "Guwahati Central Disaster Logistics Depot",
    latitude = 26.1445,
    longitude = 91.7362,
    elevationMeters = 55,
    hubType = HubType.PRIMARY_DEPOT,
    address = "National Highway 27, Jalukbari, Guwahati, Assam 781014",
    category = "Relief Depot",
    rating = 4.9f,
    tags = listOf("#ColdChainReady", "#Helipad", "#FuelDepot", "#HeavyTruckAccess"),
    contactPhone = "+91 361 257 0122",
    description = "Primary staging warehouse for Assam & Meghalaya disaster relief supplies. Equipped with sub-zero pharmaceutical storage and 24/7 diesel generator bank."
  )

  val SHILLONG_HOSPITAL = GisPoint(
    id = "SH-02",
    name = "Shillong Civil Medical Center & Trauma Care",
    latitude = 25.5788,
    longitude = 91.8933,
    elevationMeters = 1525,
    hubType = HubType.FIELD_HOSPITAL,
    address = "GS Road, Police Bazar, Shillong, Meghalaya 793001",
    category = "Field Hospital",
    rating = 4.8f,
    tags = listOf("#ColdChainReady", "#OxygenSupply", "#TraumaICU", "#BloodBank"),
    contactPhone = "+91 364 222 4100",
    description = "Regional hospital receiving critical medical cargo. Maintains 120 dedicated ICU beds and automated cryogenic oxygen tanks."
  )

  val JOWAI_TRANSIT = GisPoint(
    id = "JW-03",
    name = "Jowai Highland Waypoint & Inspection Post",
    latitude = 25.4526,
    longitude = 92.2036,
    elevationMeters = 1380,
    hubType = HubType.WAYPOINT,
    address = "NH-6 Bypass, West Jaintia Hills, Meghalaya 793150",
    category = "Mountain Pass",
    rating = 4.2f,
    tags = listOf("#4x4Access", "#MeshRelay", "#WeighStation", "#RainGauge"),
    contactPhone = "+91 365 220 8911",
    description = "Highland monitoring post overlooking monsoonal escarpments. Deploys road clearing graders and satellite radio repeaters."
  )

  val SONAPUR_BOTTLENECK = GisPoint(
    id = "SN-04",
    name = "Sonapur Pass & NH-6 Tunnel Hazard Checkpoint",
    latitude = 25.0440,
    longitude = 92.3680,
    elevationMeters = 420,
    hubType = HubType.WAYPOINT,
    address = "NH-6 Sonapur Tunnel Km 141, East Jaintia Hills, Meghalaya",
    category = "Hazard Zone",
    rating = 2.4f,
    tags = listOf("#MudslideProne", "#0PercentAccess", "#HeavyDebris", "#EmergencyEscort"),
    contactPhone = "+91 365 291 0022",
    description = "Critical vulnerability point subject to recurring heavy landslides. Currently blocked by 400m mud accumulation."
  )

  val SILCHAR_BASE = GisPoint(
    id = "SL-05",
    name = "Silchar Relief Forward Base & Medical Depot",
    latitude = 24.8333,
    longitude = 92.7789,
    elevationMeters = 25,
    hubType = HubType.FORWARD_POST,
    address = "Circuit House Road, Tarapur, Silchar, Assam 788001",
    category = "Relief Depot",
    rating = 4.7f,
    tags = listOf("#ColdChainReady", "#OxygenSupply", "#BoatRescue", "#Helipad"),
    contactPhone = "+91 384 224 5510",
    description = "Southern forward distribution command serving Barak Valley and neighboring Mizoram/Tripura supply lines."
  )

  val UMRANGSO_POST = GisPoint(
    id = "UM-06",
    name = "Umrangso Dima Hasao Plateau Refuge Camp",
    latitude = 25.5100,
    longitude = 92.7400,
    elevationMeters = 710,
    hubType = HubType.REFUGE_CAMP,
    address = "SH-7 Dima Hasao Highland Corridor, Assam 788930",
    category = "Refuge Camp",
    rating = 4.6f,
    tags = listOf("#EmergencyShelter", "#RationsHub", "#4x4Access", "#MedicalTriage"),
    contactPhone = "+91 367 328 1004",
    description = "Strategic highland detour station with emergency fuel reserves, emergency field clinic, and 500-person capacity shelter."
  )

  val HAFLONG_PASS = GisPoint(
    id = "HF-07",
    name = "Haflong Mountain Ridge Weather Bypass",
    latitude = 25.1700,
    longitude = 93.0200,
    elevationMeters = 680,
    hubType = HubType.WAYPOINT,
    address = "Old Jatinga Valley Road, Haflong, Assam 788819",
    category = "Mountain Pass",
    rating = 4.4f,
    tags = listOf("#4x4Access", "#HighClearance", "#ScenicEscarpment", "#MeshRelay"),
    contactPhone = "+91 367 323 6050",
    description = "Alternative mountain pass bypassing flooded Barak lowlands. Reinforced culverts and landslide prevention netting."
  )

  val TEZPUR_DEPOT = GisPoint(
    id = "TZ-08",
    name = "Tezpur Foothills Base & Air Support Link",
    latitude = 26.6338,
    longitude = 92.8006,
    elevationMeters = 48,
    hubType = HubType.PRIMARY_DEPOT,
    address = "Mission Chariali, Tezpur, Assam 784001",
    category = "Relief Depot",
    rating = 4.8f,
    tags = listOf("#Helipad", "#FuelDepot", "#HeavyAirlift", "#ColdChainReady"),
    contactPhone = "+91 371 222 3001",
    description = "Northern base linking Brahmaputra plain to Arunachal frontier. Equipped for emergency Mi-17 heavy airlift ops."
  )

  val ITANAGAR_HOSPITAL = GisPoint(
    id = "IT-09",
    name = "Itanagar Emergency Center & Base Hospital",
    latitude = 27.0844,
    longitude = 93.6053,
    elevationMeters = 320,
    hubType = HubType.FIELD_HOSPITAL,
    address = "NH-415, Niti Vihar, Itanagar, Arunachal Pradesh 791111",
    category = "Field Hospital",
    rating = 4.9f,
    tags = listOf("#OxygenSupply", "#TraumaICU", "#ColdChainReady", "#BloodBank"),
    contactPhone = "+91 360 221 2345",
    description = "Apex mountain healthcare facility providing surgical care, snakebite anti-venom, and neonatal intensive units."
  )

  val BARAK_HELIPAD = GisPoint(
    id = "BH-10",
    name = "Barak Lowland Emergency Helipad",
    latitude = 24.9100,
    longitude = 92.6500,
    elevationMeters = 38,
    hubType = HubType.FORWARD_POST,
    address = "Panchgram Air Strip, Hailakandi Road, Assam 788802",
    category = "Helipad & Evac",
    rating = 4.5f,
    tags = listOf("#Helipad", "#BoatRescue", "#NightLanding", "#EvacPost"),
    contactPhone = "+91 384 228 0101",
    description = "Concrete reinforced helipad for airlifting patients during severe Barak river monsoon inundations."
  )

  val allHubs = listOf(
    GUWAHATI_DEPOT,
    SHILLONG_HOSPITAL,
    JOWAI_TRANSIT,
    SONAPUR_BOTTLENECK,
    SILCHAR_BASE,
    UMRANGSO_POST,
    HAFLONG_PASS,
    TEZPUR_DEPOT,
    ITANAGAR_HOSPITAL,
    BARAK_HELIPAD
  )

  // Real-time Active Hazards
  fun getActiveHazards(): List<HazardIncident> = listOf(
    HazardIncident(
      id = "HZ-101",
      title = "Severe Monsoon Landslide",
      hazardType = HazardType.LANDSLIDE,
      severity = HazardSeverity.CRITICAL_BLOCKED,
      location = SONAPUR_BOTTLENECK,
      highwayName = "NH-6 Sonapur Tunnel Approach",
      description = "Massive rockfall and 400m mud accumulation. Incline collapsed due to 84mm/h rain. Road completely blocked for heavy vehicles.",
      reportedTimeAgo = "18 mins ago",
      activeEvacuationRadiusMeters = 800.0
    ),
    HazardIncident(
      id = "HZ-102",
      title = "Flash Flood River Surge",
      hazardType = HazardType.FLASH_FLOOD,
      severity = HazardSeverity.MODERATE_WARNING,
      location = GisPoint("WP-BRK", "Barak Lowland Crossing", 24.9100, 92.6500, 32),
      highwayName = "NH-37 / Old Cachar Road",
      description = "Barak River surge overflowing embankment. Water depth 0.6m. High ground clearance or 4x4 military relief convoys only.",
      reportedTimeAgo = "42 mins ago",
      activeEvacuationRadiusMeters = 400.0
    ),
    HazardIncident(
      id = "HZ-103",
      title = "Active Rockfall Zone",
      hazardType = HazardType.ROCKFALL_ZONE,
      severity = HazardSeverity.ADVISORY_MONITORED,
      location = JOWAI_TRANSIT,
      highwayName = "NH-6 Jowai Hill Escarpment",
      description = "Intermittent pebble and gravel slide on hairpin bend KM 82. Speed restricted to 20 km/h with spotters.",
      reportedTimeAgo = "1 hour ago",
      activeEvacuationRadiusMeters = 300.0
    )
  )

  fun getRoutePlans(): Pair<RoutePlan, RoutePlan> {
    val safeRoute = RoutePlan(
      id = "RT-SAFE-01",
      title = "AI Weather-Resilient Highland Bypass",
      isAiRecommended = true,
      isBlocked = false,
      distanceKm = 348.0,
      durationMinutes = 410,
      averageAccessibilityScore = 94,
      riskSummary = "Bypasses Sonapur slide via Umrangso & Haflong highland ridges. Road reinforced against monsoons.",
      waypoints = listOf(GUWAHATI_DEPOT, SHILLONG_HOSPITAL, UMRANGSO_POST, HAFLONG_PASS, SILCHAR_BASE),
      elevationMin = 25,
      elevationMax = 1525
    )

    val blockedRoute = RoutePlan(
      id = "RT-BLOCKED-02",
      title = "NH-6 Direct Sonapur Corridor (Impassable)",
      isAiRecommended = false,
      isBlocked = true,
      distanceKm = 312.4,
      durationMinutes = 720,
      averageAccessibilityScore = 24,
      riskSummary = "CRITICAL: Sonapur Tunnel completely obstructed by 400m mudslide. NHAI clearance est: 28-36 hours.",
      waypoints = listOf(GUWAHATI_DEPOT, SHILLONG_HOSPITAL, JOWAI_TRANSIT, SONAPUR_BOTTLENECK, SILCHAR_BASE),
      elevationMin = 25,
      elevationMax = 1525
    )

    return Pair(safeRoute, blockedRoute)
  }

  fun getInitialMissions() = listOf(
    com.example.data.local.MissionEntity(
      missionCode = "MED-NER-01",
      title = "Critical Cold-Chain Insulin & Pediatric Vaccines",
      destination = "Shillong Civil Medical Center",
      origin = "Guwahati Central Depot",
      cargoType = "MEDICINE",
      priorityLevel = "CRITICAL_P1",
      targetTempCelsius = 3.5,
      isDelivered = false,
      etaMinutes = 75,
      assignedConvoy = "Convoy Delta-1 (Refrigerated 4x4)"
    ),
    com.example.data.local.MissionEntity(
      missionCode = "OXY-NER-02",
      title = "Emergency Oxygen Cylinders & Concentrators",
      destination = "Silchar Relief Base Hospital",
      origin = "Tezpur Foothills Base",
      cargoType = "OXYGEN",
      priorityLevel = "CRITICAL_P1",
      targetTempCelsius = null,
      isDelivered = false,
      etaMinutes = 180,
      assignedConvoy = "Convoy Bravo-3 (Hazard Certified)"
    ),
    com.example.data.local.MissionEntity(
      missionCode = "RAT-NER-03",
      title = "High-Energy Ready Rations & Water Purification",
      destination = "Umrangso Dima Hasao Refuge Camp",
      origin = "Guwahati Central Depot",
      cargoType = "FOOD_RATIONS",
      priorityLevel = "HIGH_P2",
      targetTempCelsius = null,
      isDelivered = false,
      etaMinutes = 240,
      assignedConvoy = "Convoy Echo-4 (Heavy All-Terrain)"
    )
  )

  // Contacts for Real-Time Location Sharing
  val defaultShareContacts = listOf(
    LocationShareContact(
      id = "CONT-01",
      name = "State Disaster Management Cell (NER-HQ)",
      role = "Central Command Dispatcher",
      department = "State Disaster Management Authority",
      phone = "+91 361 223 7000",
      avatarInitials = "HQ"
    ),
    LocationShareContact(
      id = "CONT-02",
      name = "Major Vikram Sen",
      role = "Convoy Delta Lead Escort",
      department = "Border Logistics Unit",
      phone = "+91 943 512 8840",
      avatarInitials = "VS"
    ),
    LocationShareContact(
      id = "CONT-03",
      name = "Dr. Ananya Roy",
      role = "Chief Medical Officer",
      department = "Shillong Trauma Center",
      phone = "+91 986 401 2299",
      avatarInitials = "AR"
    ),
    LocationShareContact(
      id = "CONT-04",
      name = "Wing Commander K. Bora",
      role = "Search & Air Rescue Coordinator",
      department = "Tezpur Air Wing Hub",
      phone = "+91 977 400 3311",
      avatarInitials = "KB"
    )
  )

  // Offline Downloadable Map Regions
  val defaultOfflineRegions = listOf(
    OfflineMapRegion(
      id = "REG-01",
      title = "Barak Valley & Cachar Corridor",
      stateCoverage = "Assam (Silchar, Hailakandi, Karimganj)",
      sizeMb = 48.5,
      isDownloaded = true,
      downloadProgress = 100,
      lastUpdated = "Updated 2 hrs ago",
      featuresList = listOf("High-res Topo Vectors", "Flood Embankment GeoJSON", "Offline Road Routing", "Emergency Hospitals")
    ),
    OfflineMapRegion(
      id = "REG-02",
      title = "Dima Hasao Highland Region",
      stateCoverage = "Assam (Haflong, Umrangso, Jatinga)",
      sizeMb = 62.0,
      isDownloaded = true,
      downloadProgress = 100,
      lastUpdated = "Updated 6 hrs ago",
      featuresList = listOf("Escarpment Elevation Gradients", "SH-7 Bypass Nodes", "Offline AI Risk Engine", "Shelter Coordinates")
    ),
    OfflineMapRegion(
      id = "REG-03",
      title = "East Khasi & Jaintia Escarpment",
      stateCoverage = "Meghalaya (Shillong, Jowai, Dawki)",
      sizeMb = 74.2,
      isDownloaded = false,
      downloadProgress = 0,
      lastUpdated = "Cloud package available (v2.4)",
      featuresList = listOf("NH-6 Landslide Vulnerability Zones", "Civil Hospital Geofences", "Monsoon Runoff Maps")
    ),
    OfflineMapRegion(
      id = "REG-04",
      title = "Brahmaputra Valley Supply Ring",
      stateCoverage = "Assam (Guwahati, Dispur, Tezpur)",
      sizeMb = 56.8,
      isDownloaded = false,
      downloadProgress = 0,
      lastUpdated = "Cloud package available (v2.1)",
      featuresList = listOf("Heavy Depots & Railheads", "River Ferry Crossings", "Mi-17 Helipad Coordinates")
    )
  )
}
