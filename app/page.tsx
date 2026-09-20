"use client";

import { useState, useMemo } from "react";
import {
  Shield,
  Navigation,
  Compass,
  AlertTriangle,
  Radio,
  HardDrive,
  MessageSquare,
  Moon,
  Sun,
  Search,
  Filter,
  Layers,
  MapPin,
  Clock,
  Truck,
  RotateCcw,
  Sparkles,
  ExternalLink,
  Laptop,
  Smartphone
} from "lucide-react";
import LeafletGisMap from "@/components/LeafletGisMap";
import FieldOfficerPwa from "@/components/FieldOfficerPwa";
import TacticalChatbot from "@/components/TacticalChatbot";
import LocationShareModal from "@/components/LocationShareModal";
import OfflineCacheModal from "@/components/OfflineCacheModal";
import {
  NER_POINTS,
  ACTIVE_HAZARDS,
  ROUTE_PLANS,
  INITIAL_MISSIONS,
  GisPoint,
  HazardIncident,
  RouteOption,
  MissionCargo,
  ShareContact
} from "@/lib/gisData";

export default function Home() {
  // Mode selection: Command Operator Web Portal vs Field Officer PWA
  const [activePersona, setActivePersona] = useState<"COMMAND_PORTAL" | "FIELD_PWA">("COMMAND_PORTAL");

  // Route & GIS States
  const [isSafeRouteSelected, setIsSafeRouteSelected] = useState(true);
  const [selectedHub, setSelectedHub] = useState<GisPoint | null>(null);
  const [selectedHazard, setSelectedHazard] = useState<HazardIncident | null>(null);
  const [missions, setMissions] = useState<MissionCargo[]>(INITIAL_MISSIONS);

  // Search & Filter States
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  const [selectedTag, setSelectedTag] = useState<string | null>(null);

  // Dialog & Chatbot States
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [isLocationShareOpen, setIsLocationShareOpen] = useState(false);
  const [isOfflineModalOpen, setIsOfflineModalOpen] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(true);

  // Live Convoy Telemetry (simulated real-time vehicle)
  const [telemetry, setTelemetry] = useState({
    lat: 25.4200,
    lng: 92.1800,
    altitude: 1340,
    speed: 42,
    heading: 118,
    isNearHazard: true
  });

  // Active Location Sharing state
  const [activeShareSession, setActiveShareSession] = useState<{
    contactName: string;
    expiresAt: number;
    isBatterySaver: boolean;
  } | null>(null);

  // Filtered Hubs
  const filteredHubs = useMemo(() => {
    return NER_POINTS.filter((point) => {
      const q = searchQuery.toLowerCase();
      const matchesQuery =
        !q ||
        point.name.toLowerCase().includes(q) ||
        point.address.toLowerCase().includes(q) ||
        point.tags.some((t) => t.toLowerCase().includes(q));

      const matchesCategory =
        selectedCategory === "ALL" || point.category.toLowerCase() === selectedCategory.toLowerCase();

      const matchesTag = !selectedTag || point.tags.includes(selectedTag);

      return matchesQuery && matchesCategory && matchesTag;
    });
  }, [searchQuery, selectedCategory, selectedTag]);

  const highlightedIds = useMemo(() => new Set(filteredHubs.map((h) => h.id)), [filteredHubs]);

  const activeRoute = isSafeRouteSelected ? ROUTE_PLANS[0] : ROUTE_PLANS[1];
  const alternateRoute = isSafeRouteSelected ? ROUTE_PLANS[1] : ROUTE_PLANS[0];

  const handleStartShare = (contact: ShareContact, durationMins: number, isBatterySaver: boolean) => {
    setActiveShareSession({
      contactName: contact.name,
      expiresAt: Date.now() + durationMins * 60 * 1000,
      isBatterySaver
    });
  };

  const handleStopShare = () => {
    setActiveShareSession(null);
  };

  const handleBookingConfirmed = (text: string) => {
    const newMission: MissionCargo = {
      id: "M-" + Date.now(),
      missionCode: `NER-${Math.floor(1000 + Math.random() * 9000)}`,
      title: text.includes("Oxygen") ? "Emergency Cryogenic Oxygen Dispatch" : "Cold-Chain Vaccine Priority Dispatch",
      origin: "Guwahati Central Depot",
      destination: text.includes("Silchar") ? "Silchar Relief Base Hospital" : "Shillong Civil Medical Center",
      cargoType: text.includes("Oxygen") ? "CRYOGENIC_OXYGEN" : "COLD_CHAIN_MEDICINE",
      priority: "CRITICAL_P1",
      targetTempCelsius: 3.5,
      assignedConvoy: "Convoy Delta-1 (4x4)",
      etaMinutes: 260,
      isDelivered: false
    };
    setMissions((prev) => [newMission, ...prev]);
  };

  return (
    <div className={`min-h-screen flex flex-col ${isDarkMode ? "dark bg-luxe-obsidian text-earth-100" : "bg-earth-50 text-earth-950"}`}>
      {/* Top Application Header */}
      <header className="sticky top-0 z-40 bg-earth-950/90 border-b border-earth-800 backdrop-blur-md px-4 py-2.5 flex items-center justify-between text-earth-100">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-luxe-copper to-earth-700 flex items-center justify-center text-white font-black shadow-lg">
            NER
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-sm font-bold tracking-tight">NER-ResQRoute</h1>
              <span className="px-2 py-0.5 rounded-full bg-luxe-copper/20 text-luxe-copper border border-luxe-copper/30 text-[10px] font-bold">
                SIH 2026 • BitForge
              </span>
            </div>
            <p className="text-[11px] text-earth-400">
              Disaster-Aware GIS Logistics & Route Intelligence Platform
            </p>
          </div>
        </div>

        {/* Persona Switcher & Global Actions */}
        <div className="flex items-center gap-2">
          {/* Persona Switcher */}
          <div className="hidden sm:flex bg-earth-900 border border-earth-700 rounded-xl p-1 gap-1">
            <button
              onClick={() => setActivePersona("COMMAND_PORTAL")}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-semibold transition-all ${
                activePersona === "COMMAND_PORTAL"
                  ? "bg-luxe-copper text-white shadow-md"
                  : "text-earth-400 hover:text-earth-200"
              }`}
            >
              <Laptop className="w-3.5 h-3.5" />
              <span>Command Portal</span>
            </button>
            <button
              onClick={() => setActivePersona("FIELD_PWA")}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-semibold transition-all ${
                activePersona === "FIELD_PWA"
                  ? "bg-luxe-copper text-white shadow-md"
                  : "text-earth-400 hover:text-earth-200"
              }`}
            >
              <Smartphone className="w-3.5 h-3.5" />
              <span>Field Officer PWA</span>
            </button>
          </div>

          {/* Location Sharing HUD Icon */}
          <button
            onClick={() => setIsLocationShareOpen(true)}
            title="Real-Time Location Sharing"
            className={`p-2 rounded-xl border transition-all ${
              activeShareSession
                ? "bg-emerald-950 border-emerald-500 text-emerald-400"
                : "bg-earth-900 border-earth-700 text-earth-300 hover:border-earth-600"
            }`}
          >
            <Radio className="w-4 h-4" />
          </button>

          {/* Offline Map Cache Button */}
          <button
            onClick={() => setIsOfflineModalOpen(true)}
            title="Offline Region Packages"
            className="p-2 rounded-xl bg-earth-900 border border-earth-700 text-earth-300 hover:border-earth-600 transition-colors"
          >
            <HardDrive className="w-4 h-4" />
          </button>

          {/* Tactical Chatbot FAB */}
          <button
            onClick={() => setIsChatOpen(!isChatOpen)}
            title="Gemini AI Tactical Dispatcher"
            className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-luxe-copper hover:bg-earth-600 text-white font-bold text-xs shadow-lg transition-all"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span className="hidden md:inline">Tactical AI</span>
          </button>
        </div>
      </header>

      {/* Main View Area */}
      <div className="flex-1 flex flex-col md:flex-row overflow-hidden relative">
        {activePersona === "FIELD_PWA" ? (
          /* ========================================================================= */
          /* PERSONA 1: Field Officer PWA (Mobile Touch Offline Reporting & Geotagging) */
          /* ========================================================================= */
          <main className="flex-1 p-4 md:p-8 overflow-y-auto bg-earth-950">
            <FieldOfficerPwa
              telemetry={telemetry}
              onNewHazardReported={() => {
                // Flash alert
              }}
            />
          </main>
        ) : (
          /* ========================================================================= */
          /* PERSONA 2: Command Operator Web Portal (Leaflet GIS + Route Intelligence) */
          /* ========================================================================= */
          <div className="flex-1 flex flex-col md:flex-row h-full relative">
            {/* Left Sidebar: Routing, Search & Missions Console */}
            <aside className="w-full md:w-96 bg-earth-900/95 border-r border-earth-800 p-4 space-y-4 overflow-y-auto z-10 flex-shrink-0">
              {/* Route Orchestration Card */}
              <div className="p-4 rounded-2xl bg-earth-950 border border-earth-800 shadow-xl space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs uppercase font-bold tracking-wider text-earth-400">
                    Route Orchestration
                  </span>
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full font-bold ${
                      isSafeRouteSelected
                        ? "bg-emerald-950 text-emerald-400 border border-emerald-500/40"
                        : "bg-red-950 text-red-400 border border-red-500/40"
                    }`}
                  >
                    {isSafeRouteSelected ? "94% ACCESSIBLE" : "0% BLOCKED"}
                  </span>
                </div>

                <div>
                  <h3 className="text-sm font-bold text-earth-100">{activeRoute.title}</h3>
                  <p className="text-xs text-earth-400 mt-1">{activeRoute.riskSummary}</p>
                </div>

                <div className="grid grid-cols-3 gap-2 py-2 border-y border-earth-800/80 text-center">
                  <div>
                    <span className="text-[10px] text-earth-400 uppercase">Distance</span>
                    <p className="text-xs font-bold font-mono text-earth-200">{activeRoute.distanceKm} km</p>
                  </div>
                  <div>
                    <span className="text-[10px] text-earth-400 uppercase">Est. Duration</span>
                    <p className="text-xs font-bold font-mono text-earth-200">
                      {Math.floor(activeRoute.durationMinutes / 60)}h {activeRoute.durationMinutes % 60}m
                    </p>
                  </div>
                  <div>
                    <span className="text-[10px] text-earth-400 uppercase">Max Alt</span>
                    <p className="text-xs font-bold font-mono text-earth-200">{activeRoute.elevationRange[1]}m</p>
                  </div>
                </div>

                <div className="flex gap-2 pt-1">
                  <button
                    onClick={() => setIsSafeRouteSelected(!isSafeRouteSelected)}
                    className="flex-1 py-2 bg-earth-800 hover:bg-earth-700 text-earth-100 text-xs font-semibold rounded-xl border border-earth-700 transition-colors"
                  >
                    Switch Route
                  </button>
                  <button
                    onClick={() => setIsSafeRouteSelected(true)}
                    className="flex-1 py-2 bg-luxe-copper hover:bg-earth-600 text-white text-xs font-bold rounded-xl shadow transition-colors"
                  >
                    Recalculate AI Detour
                  </button>
                </div>
              </div>

              {/* Hazard Proximity Alert Banner */}
              {telemetry.isNearHazard && (
                <div className="p-3.5 rounded-xl bg-red-950/60 border border-red-500/50 text-red-200 text-xs flex items-start gap-2.5 shadow-lg animate-pulse">
                  <AlertTriangle className="w-5 h-5 text-red-400 flex-shrink-0 mt-0.5" />
                  <div>
                    <h4 className="font-bold text-red-300">ALERT: Sonapur Landslide Zone (~18 km)</h4>
                    <p className="text-[11px] text-red-300/80 mt-0.5">
                      NH-6 tunnel entrance is completely blocked by 400m mudflow. Heavy convoys must divert via Umrangso.
                    </p>
                  </div>
                </div>
              )}

              {/* Multi-Criterion Search & Filter Console */}
              <div className="space-y-2.5">
                <div className="relative">
                  <Search className="w-4 h-4 text-earth-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Search depots, hospitals, passes, tags..."
                    className="w-full bg-earth-950 border border-earth-700 rounded-xl pl-9 pr-3 py-2 text-xs text-earth-100 placeholder-earth-500 focus:outline-none focus:border-luxe-copper"
                  />
                </div>

                {/* Categories */}
                <div className="flex gap-1.5 overflow-x-auto pb-1">
                  {["ALL", "Relief Depot", "Field Hospital", "Mountain Pass", "Hazard Zone", "Helipad & Evac"].map(
                    (cat) => (
                      <button
                        key={cat}
                        onClick={() => setSelectedCategory(cat)}
                        className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold whitespace-nowrap transition-colors ${
                          selectedCategory.toLowerCase() === cat.toLowerCase()
                            ? "bg-luxe-copper text-white"
                            : "bg-earth-950 text-earth-400 hover:text-earth-200 border border-earth-800"
                        }`}
                      >
                        {cat}
                      </button>
                    )
                  )}
                </div>

                {/* Popular Operational Tags */}
                <div className="flex flex-wrap gap-1 text-[10px]">
                  {["#ColdChainReady", "#OxygenSupply", "#4x4Access", "#Helipad"].map((tag) => (
                    <button
                      key={tag}
                      onClick={() => setSelectedTag(selectedTag === tag ? null : tag)}
                      className={`px-2 py-0.5 rounded-full border transition-colors ${
                        selectedTag === tag
                          ? "bg-amber-500 text-black border-amber-400 font-bold"
                          : "bg-earth-950/60 border-earth-800 text-earth-400 hover:border-earth-700"
                      }`}
                    >
                      {tag}
                    </button>
                  ))}
                </div>
              </div>

              {/* Priority Mission-Aware Supply Dispatches */}
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs uppercase font-bold tracking-wider text-earth-400">
                    Active Mission Convoys
                  </span>
                  <span className="text-xs text-earth-500 font-mono">{missions.length} Registered</span>
                </div>

                <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
                  {missions.map((m) => (
                    <div
                      key={m.id}
                      className="p-3 rounded-xl bg-earth-950 border border-earth-800 hover:border-earth-700 transition-colors space-y-1 text-xs"
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-earth-100">{m.title}</span>
                        <span className="text-[10px] px-1.5 py-0.5 rounded bg-red-950 text-red-400 font-bold">
                          {m.priority}
                        </span>
                      </div>
                      <p className="text-earth-400 text-[11px]">
                        {m.origin} → {m.destination}
                      </p>
                      <div className="flex items-center justify-between text-[10px] text-earth-500 pt-1">
                        <span>{m.assignedConvoy}</span>
                        <span className="text-emerald-400 font-mono">ETA: {m.etaMinutes}m</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </aside>

            {/* Right: Full Interactive Leaflet GIS Map Canvas */}
            <main className="flex-1 h-[65vh] md:h-auto relative">
              <LeafletGisMap
                activeRoute={activeRoute}
                alternateRoute={alternateRoute}
                hazards={ACTIVE_HAZARDS}
                hubs={filteredHubs}
                selectedHub={selectedHub}
                highlightedIds={highlightedIds}
                onSelectHub={(hub) => setSelectedHub(hub)}
                onSelectHazard={(hazard) => setSelectedHazard(hazard)}
                telemetry={telemetry}
              />

              {/* Active Sharing Notification Badge (Top Center) */}
              {activeShareSession && (
                <div className="absolute top-4 left-1/2 -translate-x-1/2 z-20 px-4 py-1.5 rounded-full bg-earth-950/90 border border-emerald-500/80 shadow-2xl flex items-center gap-2 text-xs text-emerald-300 backdrop-blur-md">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
                  <span className="font-semibold">Live Location Sharing: {activeShareSession.contactName}</span>
                </div>
              )}
            </main>
          </div>
        )}
      </div>

      {/* Tactical Chatbot Drawer */}
      <TacticalChatbot
        isOpen={isChatOpen}
        onClose={() => setIsChatOpen(false)}
        onBookingConfirmed={handleBookingConfirmed}
      />

      {/* Location Sharing Dialog */}
      <LocationShareModal
        isOpen={isLocationShareOpen}
        onClose={() => setIsLocationShareOpen(false)}
        telemetry={telemetry}
        activeSession={activeShareSession}
        onStartShare={handleStartShare}
        onStopShare={handleStopShare}
      />

      {/* Offline Region Cache Modal */}
      <OfflineCacheModal
        isOpen={isOfflineModalOpen}
        onClose={() => setIsOfflineModalOpen(false)}
      />
    </div>
  );
}
