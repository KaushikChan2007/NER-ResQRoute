"use client";

import { useState } from "react";
import {
  Package,
  Thermometer,
  Truck,
  CheckCircle2,
  Clock,
  Plus,
  ShieldCheck,
  AlertCircle
} from "lucide-react";
import { INITIAL_MISSIONS, MissionCargo } from "@/lib/gisData";

interface DeliveriesViewProps {
  onOpenBookingAssistant: () => void;
}

export default function DeliveriesView({ onOpenBookingAssistant }: DeliveriesViewProps) {
  const [missions, setMissions] = useState<MissionCargo[]>([
    ...INITIAL_MISSIONS,
    {
      id: "M-04",
      missionCode: "PLAS-NER-04",
      title: "Fresh Frozen Blood Plasma Units",
      origin: "Guwahati Central Depot",
      destination: "Silchar Base Hospital",
      cargoType: "BLOOD_PLASMA",
      priority: "CRITICAL_P1",
      targetTempCelsius: -2.0,
      assignedConvoy: "Convoy Echo-2 (Cold Pod 4x4)",
      etaMinutes: 215,
      isDelivered: false
    },
    {
      id: "M-05",
      missionCode: "SURG-NER-05",
      title: "Emergency Surgical Triage Kits",
      origin: "Tezpur Foothills Base",
      destination: "Itanagar Base Hospital",
      cargoType: "EMERGENCY_RATIONS",
      priority: "HIGH_P2",
      assignedConvoy: "Convoy Alpha-1 (Heavy Utility)",
      etaMinutes: 110,
      isDelivered: false
    },
    {
      id: "M-06",
      missionCode: "WAT-NER-06",
      title: "Clean Water Purification Micro-Plants",
      origin: "Guwahati Central Depot",
      destination: "Umrangso Refuge Camp",
      cargoType: "EMERGENCY_RATIONS",
      priority: "STANDARD_P3",
      assignedConvoy: "Convoy Foxtrot-3 (All-Terrain)",
      etaMinutes: 280,
      isDelivered: false
    }
  ]);

  return (
    <div className="p-6 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h2 className="text-xl font-bold text-slate-900 dark:text-white">Deliveries & Missions</h2>
            <span className="px-2.5 py-0.5 rounded-full bg-blue-100 dark:bg-blue-950 text-blue-600 dark:text-blue-400 text-xs font-bold font-mono">
              {missions.length} Registered
            </span>
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
            Life-critical medical supply dispatches, cold-chain temperature telemetry, and relief cargo tracking
          </p>
        </div>

        <button
          onClick={onOpenBookingAssistant}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-md shadow-blue-600/20 transition-all"
        >
          <Plus className="w-4 h-4" />
          <span>Book New Mission Dispatch</span>
        </button>
      </div>

      {/* Deliveries Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {missions.map((m) => {
          const isColdChain = m.cargoType === "COLD_CHAIN_MEDICINE" || m.cargoType === "BLOOD_PLASMA";
          return (
            <div
              key={m.id}
              className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md transition-all space-y-3.5 flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold font-mono text-blue-600 dark:text-blue-400">
                    {m.missionCode}
                  </span>
                  <span
                    className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${
                      m.priority === "CRITICAL_P1"
                        ? "bg-red-100 text-red-800 dark:bg-red-950 dark:text-red-400"
                        : "bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-400"
                    }`}
                  >
                    {m.priority}
                  </span>
                </div>

                <h3 className="text-sm font-bold text-slate-900 dark:text-white mt-1.5">{m.title}</h3>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                  From: <span className="text-slate-700 dark:text-slate-300 font-medium">{m.origin}</span>
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400">
                  To: <span className="text-blue-600 dark:text-blue-400 font-semibold">{m.destination}</span>
                </p>
              </div>

              {/* Cold-Chain Pod Sensor */}
              {isColdChain && m.targetTempCelsius !== undefined && (
                <div className="p-3 rounded-xl bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900/60 flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2">
                    <Thermometer className="w-4 h-4 text-blue-600" />
                    <div>
                      <span className="font-bold text-blue-900 dark:text-blue-300 block text-[11px]">
                        Sensor Pod Telemetry
                      </span>
                      <span className="text-[10px] text-blue-700 dark:text-blue-400">Active Peltier Cooling</span>
                    </div>
                  </div>
                  <span className="font-mono font-bold text-blue-600">{m.targetTempCelsius}°C</span>
                </div>
              )}

              {/* Footer */}
              <div className="pt-2 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs text-slate-500">
                <span className="flex items-center gap-1">
                  <Truck className="w-3.5 h-3.5 text-slate-400" />
                  <span className="truncate max-w-[140px]">{m.assignedConvoy}</span>
                </span>
                <span className="font-mono font-bold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                  <Clock className="w-3 h-3" />
                  ETA {m.etaMinutes}m
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
