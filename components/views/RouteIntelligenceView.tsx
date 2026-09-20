"use client";

import { useState } from "react";
import {
  Route,
  Zap,
  TrendingDown,
  TrendingUp,
  AlertOctagon,
  CheckCircle2,
  Sliders,
  Compass,
  ArrowRight,
  Mountain,
  CloudRain
} from "lucide-react";
import { ROUTE_PLANS } from "@/lib/gisData";

export default function RouteIntelligenceView() {
  const [rainfallMmH, setRainfallMmH] = useState(84);
  const [selectedRouteId, setSelectedRouteId] = useState("RT-SAFE-01");

  // Dynamic cost calculation based on simulated rainfall
  const safeBaseKm = 348;
  const safeSlope = 4.8;
  const safeCost = Math.round(safeBaseKm * (1 + safeSlope / 100) * (1 + (rainfallMmH * 0.4) / 60));

  const blockedBaseKm = 312;
  const blockedSlope = 7.4;
  const blockedCost = rainfallMmH > 60 ? 9999 : Math.round(blockedBaseKm * (1 + blockedSlope / 100) * (1 + rainfallMmH / 60) + 500);

  return (
    <div className="p-6 space-y-6 max-w-7xl mx-auto">
      {/* Title */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h2 className="text-xl font-bold text-slate-900 dark:text-white">Route Intelligence</h2>
            <span className="px-2.5 py-0.5 rounded-full bg-blue-100 dark:bg-blue-950 text-blue-600 dark:text-blue-400 text-xs font-bold font-mono">
              A* / Dijkstra Dynamic Heuristic
            </span>
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
            Terrain-aware, multi-factor pathfinding balancing slope elevation, monsoonal rainfall, and landslide blockages
          </p>
        </div>

        {/* Rainfall Simulation Slider */}
        <div className="p-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm flex items-center gap-3">
          <CloudRain className="w-5 h-5 text-blue-500 flex-shrink-0" />
          <div>
            <div className="flex items-center justify-between text-xs gap-3">
              <span className="font-semibold text-slate-700 dark:text-slate-300">Monsoon Rainfall:</span>
              <span className="font-bold font-mono text-blue-600 dark:text-blue-400">{rainfallMmH} mm/h</span>
            </div>
            <input
              type="range"
              min="10"
              max="140"
              value={rainfallMmH}
              onChange={(e) => setRainfallMmH(Number(e.target.value))}
              className="w-44 accent-blue-600 cursor-pointer h-1.5 bg-slate-200 dark:bg-slate-700 rounded-lg mt-1"
            />
          </div>
        </div>
      </div>

      {/* Side-by-Side Corridor Comparison Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Recommended Corridor: Umrangso Highland Bypass */}
        <div
          onClick={() => setSelectedRouteId("RT-SAFE-01")}
          className={`p-6 rounded-3xl border-2 transition-all cursor-pointer shadow-sm relative overflow-hidden ${
            selectedRouteId === "RT-SAFE-01"
              ? "border-emerald-500 bg-white dark:bg-slate-900 ring-4 ring-emerald-500/10 shadow-lg"
              : "border-slate-200 dark:border-slate-800 bg-white/70 dark:bg-slate-900/70"
          }`}
        >
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-400 text-xs font-bold uppercase tracking-wider flex items-center gap-1">
                <CheckCircle2 className="w-3.5 h-3.5" />
                AI Recommended Bypass
              </span>
            </div>
            <span className="text-2xl font-black font-mono text-emerald-600 dark:text-emerald-400">
              94% Clear
            </span>
          </div>

          <h3 className="text-base font-bold text-slate-900 dark:text-white mt-3">
            SH-7 Umrangso & Haflong Mountain Ridge Bypass
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Bypasses the flooded Barak lowlands and collapsed Sonapur Tunnel via reinforced high-altitude plateaus.
          </p>

          <div className="grid grid-cols-3 gap-2 my-4 p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl text-center text-xs">
            <div>
              <span className="text-[10px] text-slate-400 uppercase">Distance</span>
              <p className="font-bold font-mono text-slate-900 dark:text-white">348.0 km</p>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 uppercase">A* Cost Metric</span>
              <p className="font-bold font-mono text-emerald-600">{safeCost}</p>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 uppercase">Est. Duration</span>
              <p className="font-bold font-mono text-slate-900 dark:text-white">6h 50m</p>
            </div>
          </div>

          <div className="space-y-2 text-xs">
            <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
              <Mountain className="w-4 h-4 text-emerald-500" />
              <span>Ridge elevation: 710m – 1525m (Safe from valley flash floods)</span>
            </div>
            <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
              <Zap className="w-4 h-4 text-emerald-500" />
              <span>Retaining barriers installed along Jatinga curves</span>
            </div>
          </div>
        </div>

        {/* Impassable Corridor: NH-6 Sonapur Tunnel */}
        <div
          onClick={() => setSelectedRouteId("RT-BLOCKED-02")}
          className={`p-6 rounded-3xl border-2 transition-all cursor-pointer shadow-sm relative overflow-hidden ${
            selectedRouteId === "RT-BLOCKED-02"
              ? "border-red-500 bg-white dark:bg-slate-900 ring-4 ring-red-500/10 shadow-lg"
              : "border-slate-200 dark:border-slate-800 bg-white/70 dark:bg-slate-900/70"
          }`}
        >
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-1 rounded-full bg-red-100 dark:bg-red-950 text-red-700 dark:text-red-400 text-xs font-bold uppercase tracking-wider flex items-center gap-1">
                <AlertOctagon className="w-3.5 h-3.5" />
                Impassable / Blocked
              </span>
            </div>
            <span className="text-2xl font-black font-mono text-red-600 dark:text-red-400">
              0% Passable
            </span>
          </div>

          <h3 className="text-base font-bold text-slate-900 dark:text-white mt-3">
            NH-6 Direct Sonapur Tunnel Corridor (East Jaintia Hills)
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            400m mud accumulation at Km 141 entrance. Total slope failure; heavy earthmoving clearance est: 28-36 hours.
          </p>

          <div className="grid grid-cols-3 gap-2 my-4 p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl text-center text-xs">
            <div>
              <span className="text-[10px] text-slate-400 uppercase">Distance</span>
              <p className="font-bold font-mono text-slate-900 dark:text-white">312.4 km</p>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 uppercase">A* Cost Metric</span>
              <p className="font-bold font-mono text-red-500">
                {blockedCost === 9999 ? "∞ (BLOCKED)" : blockedCost}
              </p>
            </div>
            <div>
              <span className="text-[10px] text-slate-400 uppercase">Delay Penalty</span>
              <p className="font-bold font-mono text-red-500">+32.0 hrs</p>
            </div>
          </div>

          <div className="space-y-2 text-xs">
            <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
              <AlertOctagon className="w-4 h-4 text-red-500" />
              <span>Incline collapsed under 84mm/h rain volume</span>
            </div>
            <div className="flex items-center gap-2 text-slate-700 dark:text-slate-300">
              <TrendingDown className="w-4 h-4 text-red-500" />
              <span>Severe vibration risk for cold-chain medication canisters</span>
            </div>
          </div>
        </div>
      </div>

      {/* A* Mathematical Graph Model Card */}
      <div className="p-6 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white">
            A* / Dijkstra Objective Function & Weight Calculation
          </h3>
          <span className="text-xs text-slate-400">SIH 2026 Core Algorithm</span>
        </div>

        <div className="p-4 bg-slate-50 dark:bg-slate-800/80 rounded-2xl font-mono text-xs text-slate-800 dark:text-slate-200 leading-relaxed overflow-x-auto">
          Cost(u, v) = Distance(km) × [1 + (Slope% / 100)] × [1 + (Rainfall mm/h / 60)] + HazardObstructionPenalty
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
          <div className="p-3.5 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
            <span className="font-bold text-blue-600 block mb-1">1. Terrain Slope Gradient</span>
            <p className="text-slate-500 dark:text-slate-400 text-[11px]">
              Steep ascents (e.g. 7.4% at Sonapur) multiply fuel burn and double rollover hazard during rainfall saturation.
            </p>
          </div>
          <div className="p-3.5 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
            <span className="font-bold text-blue-600 block mb-1">2. Monsoonal Precipitation</span>
            <p className="text-slate-500 dark:text-slate-400 text-[11px]">
              Dynamic satellite feeds inject real-time rain mm/h. Above 60 mm/h, low-lying highways trigger automatic detour heuristics.
            </p>
          </div>
          <div className="p-3.5 bg-slate-50 dark:bg-slate-800/40 rounded-xl">
            <span className="font-bold text-blue-600 block mb-1">3. Field Incident Overlay</span>
            <p className="text-slate-500 dark:text-slate-400 text-[11px]">
              Verified on-ground officer reports add an infinite penalty (Cost → ∞) to completely severed corridors.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
