export interface SystemAlert {
  id: string;
  title: string;
  severity: "CRITICAL" | "WARNING" | "ADVISORY";
  corridor: string;
  district: string;
  state: string;
  isAcknowledged: boolean;
  timeAgo: string;
  description: string;
  recommendedAction: string;
}

export const SYSTEM_ALERTS: SystemAlert[] = [
  {
    id: "ALT-01",
    title: "NH-6 Sonapur Tunnel Mudslide (400m Obstruction)",
    severity: "CRITICAL",
    corridor: "NH-6 East Jaintia Hills Km 141",
    district: "East Jaintia Hills",
    state: "Meghalaya",
    isAcknowledged: false, // Unacknowledged #1
    timeAgo: "12m ago",
    description: "Catastrophic slope failure following 84mm/h torrential cloudburst. Over 400m of road engulfed by mud and boulders. Total closure.",
    recommendedAction: "Divert all Barak Valley and Mizoram medical convoys to SH-7 Umrangso-Dima Hasao Highland Bypass immediately."
  },
  {
    id: "ALT-02",
    title: "Mangan-Chungthang Road Slump",
    severity: "CRITICAL",
    corridor: "North Sikkim Highway Km 48",
    district: "North Sikkim",
    state: "Sikkim",
    isAcknowledged: false, // Unacknowledged #2
    timeAgo: "28m ago",
    description: "Road foundation collapsed toward Teesta river bed. Heavy vehicle transit strictly suspended.",
    recommendedAction: "Stage emergency rations at Mangan Base. Await BRO reconnaissance clearance."
  },
  {
    id: "ALT-03",
    title: "Barak River Embankment Water Overtopping",
    severity: "WARNING",
    corridor: "NH-37 Badarpur Ghat",
    district: "Cachar",
    state: "Assam",
    isAcknowledged: true,
    timeAgo: "1h ago",
    description: "Water depth 0.5m across 200m section of highway. High clearance 4x4 convoys only.",
    recommendedAction: "Maintain convoy speed under 15 km/h with wading snorkel engaged."
  },
  {
    id: "ALT-04",
    title: "Tura-Dalu Escarpment Rockfall Warning",
    severity: "WARNING",
    corridor: "NH-217 Garo Hills",
    district: "West Garo Hills",
    state: "Meghalaya",
    isAcknowledged: true,
    timeAgo: "2h ago",
    description: "Intermittent stone fall triggered by soil saturation. Spotters deployed.",
    recommendedAction: "Proceed only with visual safety spotters at hairpin bends."
  },
  {
    id: "ALT-05",
    title: "NH-306 Vairengte Bridge Abutment Fracture",
    severity: "CRITICAL",
    corridor: "NH-306 Assam-Mizoram Border",
    district: "Kolasib",
    state: "Mizoram",
    isAcknowledged: true,
    timeAgo: "3h ago",
    description: "Bridge support cracked by flood debris. Load capacity downgraded to light vehicles only (<3.5 tons).",
    recommendedAction: "Split heavy oxygen consignments into light 4x4 utility shuttle runs."
  },
  {
    id: "ALT-06",
    title: "Sela Pass Sub-Zero Freezing Drizzle",
    severity: "ADVISORY",
    corridor: "Balipara-Charduar-Tawang Highway",
    district: "Tawang",
    state: "Arunachal Pradesh",
    isAcknowledged: true,
    timeAgo: "4h ago",
    description: "Black ice on high-altitude switchbacks above 13,000 ft. Visibility down to 25m in fog.",
    recommendedAction: "Ensure all convoy wheels are fitted with snow/mud chains."
  },
  {
    id: "ALT-07",
    title: "Brahmaputra Ferry Suspension at Majuli",
    severity: "ADVISORY",
    corridor: "Nimati Ghat Crossing",
    district: "Jorhat",
    state: "Assam",
    isAcknowledged: true,
    timeAgo: "6h ago",
    description: "Strong monsoon river currents exceeding 4.2 knots. River ferry crossings halted.",
    recommendedAction: "Route road convoys via Bogibeel Bridge or Koliabhumur Bridge."
  },
  {
    id: "ALT-08",
    title: "Imphal Bypass Mud Ruts Alert",
    severity: "WARNING",
    corridor: "NH-2 Kangpokpi Section",
    district: "Kangpokpi",
    state: "Manipur",
    isAcknowledged: true,
    timeAgo: "8h ago",
    description: "Deep mud ruts up to 45cm causing vehicle undercarriage groundings.",
    recommendedAction: "High-clearance all-terrain logistics trucks only."
  }
];
