export interface DistrictNode {
  id: string;
  name: string;
  xPercent: number; // For clean canvas/SVG positioning matching Screenshot 4
  yPercent: number;
  lat: number;
  lng: number;
  isHub?: boolean; // Guwahati & Tezpur are hubs (black square markers)
  status: "clear" | "disrupted" | "restricted";
  district: string;
  state: string;
  populationServed: string;
  activeDisruptionsCount: number;
}

export interface NetworkEdge {
  id: string;
  fromId: string;
  toId: string;
  status: "clear" | "blocked" | "restricted";
  distanceKm: number;
  highway: string;
  hazardReason?: string;
}

export const DISTRICT_NODES: DistrictNode[] = [
  // Western / Sikkim
  { id: "GEY", name: "Geyzing", xPercent: 5, yPercent: 44, lat: 27.28, lng: 88.25, status: "restricted", district: "West Sikkim", state: "Sikkim", populationServed: "140,000", activeDisruptionsCount: 1 },
  { id: "MAN", name: "Mangan", xPercent: 8, yPercent: 38, lat: 27.50, lng: 88.52, status: "disrupted", district: "North Sikkim", state: "Sikkim", populationServed: "85,000", activeDisruptionsCount: 2 },
  { id: "GAN", name: "Gangtok", xPercent: 9, yPercent: 42, lat: 27.33, lng: 88.61, status: "disrupted", district: "East Sikkim", state: "Sikkim", populationServed: "280,000", activeDisruptionsCount: 2 },

  // Western Assam & Meghalaya
  { id: "TUR", name: "Tura", xPercent: 27, yPercent: 63, lat: 25.51, lng: 90.22, status: "restricted", district: "West Garo Hills", state: "Meghalaya", populationServed: "320,000", activeDisruptionsCount: 1 },
  { id: "GUW", name: "Guwahati", xPercent: 44, yPercent: 55, lat: 26.14, lng: 91.73, isHub: true, status: "clear", district: "Kamrup Metro", state: "Assam", populationServed: "1,200,000", activeDisruptionsCount: 0 },
  { id: "SHI", name: "Shillong", xPercent: 46, yPercent: 62, lat: 25.57, lng: 91.89, status: "clear", district: "East Khasi Hills", state: "Meghalaya", populationServed: "450,000", activeDisruptionsCount: 0 },

  // Southern Valley (Barak, Tripura, Mizoram)
  { id: "SIL", name: "Silchar", xPercent: 56, yPercent: 71, lat: 24.83, lng: 92.77, status: "restricted", district: "Cachar", state: "Assam", populationServed: "480,000", activeDisruptionsCount: 2 },
  { id: "DHA", name: "Dharmanagar", xPercent: 49, yPercent: 77, lat: 24.37, lng: 92.16, status: "clear", district: "North Tripura", state: "Tripura", populationServed: "210,000", activeDisruptionsCount: 0 },
  { id: "AGA", name: "Agartala", xPercent: 39, yPercent: 82, lat: 23.83, lng: 91.28, status: "clear", district: "West Tripura", state: "Tripura", populationServed: "520,000", activeDisruptionsCount: 0 },
  { id: "AIZ", name: "Aizawl", xPercent: 55, yPercent: 83, lat: 23.73, lng: 92.71, status: "disrupted", district: "Aizawl", state: "Mizoram", populationServed: "340,000", activeDisruptionsCount: 1 },
  { id: "LUN", name: "Lunglei", xPercent: 56, yPercent: 93, lat: 22.88, lng: 92.73, status: "restricted", district: "Lunglei", state: "Mizoram", populationServed: "160,000", activeDisruptionsCount: 1 },

  // Northern Plains & Arunachal
  { id: "TAW", name: "Tawang", xPercent: 46, yPercent: 39, lat: 27.58, lng: 91.86, status: "restricted", district: "Tawang", state: "Arunachal Pradesh", populationServed: "55,000", activeDisruptionsCount: 1 },
  { id: "TEZ", name: "Tezpur", xPercent: 56, yPercent: 49, lat: 26.63, lng: 92.80, isHub: true, status: "clear", district: "Sonitpur", state: "Assam", populationServed: "380,000", activeDisruptionsCount: 0 },
  { id: "ITA", name: "Itanagar", xPercent: 65, yPercent: 45, lat: 27.08, lng: 93.60, status: "clear", district: "Papum Pare", state: "Arunachal Pradesh", populationServed: "125,000", activeDisruptionsCount: 0 },
  { id: "ZIR", name: "Ziro", xPercent: 68, yPercent: 39, lat: 27.59, lng: 93.83, status: "clear", district: "Lower Subansiri", state: "Arunachal Pradesh", populationServed: "45,000", activeDisruptionsCount: 0 },

  // Eastern Corridor & Nagaland / Manipur
  { id: "JOR", name: "Jorhat", xPercent: 72, yPercent: 49, lat: 26.75, lng: 94.20, status: "clear", district: "Jorhat", state: "Assam", populationServed: "310,000", activeDisruptionsCount: 0 },
  { id: "DIB", name: "Dibrugarh", xPercent: 80, yPercent: 40, lat: 27.47, lng: 94.91, status: "clear", district: "Dibrugarh", state: "Assam", populationServed: "420,000", activeDisruptionsCount: 0 },
  { id: "PAS", name: "Pasighat", xPercent: 85, yPercent: 33, lat: 28.06, lng: 95.33, status: "restricted", district: "East Siang", state: "Arunachal Pradesh", populationServed: "75,000", activeDisruptionsCount: 0 },
  { id: "MOK", name: "Mokokchung", xPercent: 76, yPercent: 52, lat: 26.32, lng: 94.52, status: "restricted", district: "Mokokchung", state: "Nagaland", populationServed: "90,000", activeDisruptionsCount: 0 },
  { id: "KOH", name: "Kohima", xPercent: 71, yPercent: 61, lat: 25.67, lng: 94.10, status: "clear", district: "Kohima", state: "Nagaland", populationServed: "270,000", activeDisruptionsCount: 0 },
  { id: "UKH", name: "Ukhrul", xPercent: 74, yPercent: 68, lat: 25.11, lng: 94.36, status: "restricted", district: "Ukhrul", state: "Manipur", populationServed: "80,000", activeDisruptionsCount: 0 },
  { id: "IMP", name: "Imphal", xPercent: 69, yPercent: 70, lat: 24.81, lng: 93.93, status: "restricted", district: "Imphal West", state: "Manipur", populationServed: "560,000", activeDisruptionsCount: 1 }
];

export const NETWORK_EDGES: NetworkEdge[] = [
  // Blocked / Disrupted Corridors (Red Dashed in Screenshot 4)
  { id: "E-01", fromId: "GUW", toId: "GAN", status: "blocked", distanceKm: 520, highway: "NH-10 Sevoke Slopes", hazardReason: "Teesta basin flash flood mudslide" },
  { id: "E-02", fromId: "GUW", toId: "SIL", status: "blocked", distanceKm: 312, highway: "NH-6 Sonapur Tunnel Km 141", hazardReason: "400m catastrophic mudflow" },
  { id: "E-03", fromId: "SIL", toId: "AIZ", status: "blocked", distanceKm: 180, highway: "NH-306 Vairengte Escarpment", hazardReason: "Bridge abutment collapse" },
  { id: "E-04", fromId: "MAN", toId: "GAN", status: "blocked", distanceKm: 65, highway: "North Sikkim Highway", hazardReason: "Active boulder fall" },

  // Clear Corridors (Green Solid in Screenshot 4)
  { id: "E-05", fromId: "GUW", toId: "TEZ", status: "clear", distanceKm: 175, highway: "NH-27 4-Lane Expressway" },
  { id: "E-06", fromId: "TEZ", toId: "ITA", status: "clear", distanceKm: 145, highway: "NH-415 Hollongi Corridor" },
  { id: "E-07", fromId: "ITA", toId: "JOR", status: "clear", distanceKm: 120, highway: "NH-715 Brahmaputra Link" },
  { id: "E-08", fromId: "JOR", toId: "DIB", status: "clear", distanceKm: 135, highway: "NH-2 Assam Trunk Road" },
  { id: "E-09", fromId: "DIB", toId: "PAS", status: "clear", distanceKm: 155, highway: "Bogibeel Bridge Corridor" },
  { id: "E-10", fromId: "GUW", toId: "SHI", status: "clear", distanceKm: 98, highway: "NH-106 4-Lane Hill Highway" },
  { id: "E-11", fromId: "SHI", toId: "AGA", status: "clear", distanceKm: 380, highway: "Alternative South Meghalaya Road" },
  { id: "E-12", fromId: "AGA", toId: "DHA", status: "clear", distanceKm: 160, highway: "NH-8 Agartala-Churaibari" },
  { id: "E-13", fromId: "JOR", toId: "KOH", status: "clear", distanceKm: 140, highway: "NH-29 Dimapur-Kohima" },

  // Restricted Corridors (Orange in Screenshot 4)
  { id: "E-14", fromId: "GUW", toId: "TUR", status: "restricted", distanceKm: 210, highway: "NH-217 Garo Foothills", hazardReason: "Water over roadway (15 km/h limit)" },
  { id: "E-15", fromId: "TEZ", toId: "TAW", status: "restricted", distanceKm: 320, highway: "Bhalukpong-Sela Pass", hazardReason: "Fog and freezing drizzle" },
  { id: "E-16", fromId: "GUW", toId: "IMP", status: "restricted", distanceKm: 470, highway: "NH-29 / NH-2 Corridor", hazardReason: "Military convoy priority escort" },
  { id: "E-17", fromId: "SIL", toId: "LUN", status: "restricted", distanceKm: 260, highway: "NH-54 South Mizoram", hazardReason: "Single lane washaway" },
  { id: "E-18", fromId: "KOH", toId: "MOK", status: "restricted", distanceKm: 150, highway: "NH-61 Nagaland Hills", hazardReason: "Loose gravel road surface" },
  { id: "E-19", fromId: "KOH", toId: "IMP", status: "restricted", distanceKm: 138, highway: "NH-2 Senapati Mountain Incline", hazardReason: "Heavy monsoon ruts" },
  { id: "E-20", fromId: "IMP", toId: "UKH", status: "restricted", distanceKm: 84, highway: "NH-202 Shirui Lily Route", hazardReason: "Escarpment slide clearance" },
  { id: "E-21", fromId: "ITA", toId: "ZIR", status: "restricted", distanceKm: 110, highway: "Potin-Ziro Road", hazardReason: "Pothole inundation" },
];
