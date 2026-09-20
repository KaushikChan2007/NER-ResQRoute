"use client";

import {
  AlertTriangle,
  Truck,
  Activity,
  MapPin,
  Route,
  TrendingDown,
  ArrowUpRight,
  ShieldCheck,
  Clock,
  Compass
} from "lucide-react";
import { SYSTEM_ALERTS } from "@/lib/alertsData";
import { FLEET_VEHICLES } from "@/lib/fleetData";
import { INITIAL_MISSIONS } from "@/lib/gisData";

interface DashboardViewProps {
  onNavigateToGisMap: () => void;
  onNavigateToRouteIntelligence: () => void;
  onNavigateToVehicles: () => void;
  onNavigateToAlerts: () => void;
}

export default function DashboardView({
  onNavigateToGisMap,
  onNavigateToRouteIntelligence,
  onNavigateToVehicles,
  onNavigateToAlerts
}: DashboardViewProps) {
  // Metric card definitions (Matching Screenshot 2 exactly)
  const metricCards = [
    {
      title: "Active Disruptions",
      count: "6",
      subtitle: "2 under monitoring",
      subtitleColor: "text-amber-600 dark:text-amber-400",
      icon: AlertTriangle,
      iconColor: "text-red-500",
      iconBg: "bg-red-50 dark:bg-red-950/40",
      onClick: onNavigateToAlerts
    },
    {
      title: "Vehicles Moving",
      count: "5",
      subtitle: "2 delayed/broken",
      subtitleColor: "text-amber-600 dark:text-amber-400",
      icon: Truck,
      iconColor: "text-emerald-500",
      iconBg: "bg-emerald-50 dark:bg-emerald-950/40",
      onClick: onNavigateToVehicles
    },
    {
      title: "Unacknowledged Alerts",
      count: "2",
      subtitle: "8 total alerts",
      subtitleColor: "text-red-500 dark:text-red-400 font-semibold",
      icon: Activity,
      iconColor: "text-blue-500",
      iconBg: "bg-blue-50 dark:bg-blue-950/40",
      onClick: onNavigateToAlerts
    },
    {
      title: "Critical Districts",
      count: "3",
      subtitle: "19 connected",
      subtitleColor: "text-red-500 dark:text-red-400",
      icon: MapPin,
      iconColor: "text-amber-500",
      iconBg: "bg-amber-50 dark:bg-amber-950/40",
      onClick: onNavigateToGisMap
    },
    {
      title: "In Transit",
      count: "6",
      subtitle: "2 delayed",
      subtitleColor: "text-amber-600 dark:text-amber-400",
      icon: Route,
      iconColor: "text-blue-500",
      iconBg: "bg-blue-50 dark:bg-blue-950/40",
      onClick: onNavigateToVehicles
    },
    {
      title: "Closed Routes",
      count: "2",
      subtitle: "4 restricted",
      subtitleColor: "text-amber-600 dark:text-amber-400",
      icon: TrendingDown,
      iconColor: "text-red-500",
      iconBg: "bg-red-50 dark:bg-red-950/40",
      onClick: onNavigateToRouteIntelligence
    }
  ];

  return (
    <div className="p-6 space-y-6 max-w-7xl mx-auto">
      {/* Metric Cards Row (Matching Screenshot 2) */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {metricCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div
              key={idx}
              onClick={card.onClick}
              className="p-4 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm hover:shadow-md transition-all cursor-pointer flex flex-col justify-between"
            >
              <div className="flex items-start justify-between">
                <span className="text-xs font-semibold text-slate-600 dark:text-slate-400 leading-tight">
                  {card.title}
                </span>
                <div className={`p-2 rounded-xl ${card.iconBg} ${card.iconColor}`}>
                  <Icon className="w-4 h-4" />
                </div>
              </div>

              <div className="mt-3">
                <span className="text-2xl font-black text-slate-900 dark:text-white font-mono">
                  {card.count}
                </span>
                <p className={`text-[11px] mt-0.5 ${card.subtitleColor}`}>
                  {card.subtitle}
                </p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Critical Sonapur Corridor Hero Status Banner */}
      <div className="p-5 rounded-2xl bg-gradient-to-r from-red-950/80 via-slate-900 to-slate-900 border border-red-500/40 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="flex items-start gap-3.5">
          <div className="p-2.5 rounded-xl bg-red-600/30 border border-red-500/50 text-red-400 flex-shrink-0">
            <AlertTriangle className="w-6 h-6 animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="px-2 py-0.5 rounded-full bg-red-600 text-white text-[10px] font-bold uppercase tracking-wider">
                Critical Disruption
              </span>
              <span className="text-xs text-slate-400">NH-6 Sonapur Tunnel Km 141</span>
            </div>
            <h3 className="text-base font-bold text-white mt-1">
              Catastrophic 400m Mudslide: Sonapur Corridor 0% Accessible
            </h3>
            <p className="text-xs text-slate-300 mt-1 max-w-2xl">
              Heavy monsoonal rains (84mm/h) triggered total slope failure across East Jaintia Hills. 
              All medical and food consignments are routed via the AI Weather-Resilient Highland Bypass (Umrangso-Haflong).
            </p>
          </div>
        </div>

        <button
          onClick={onNavigateToRouteIntelligence}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold shadow-lg transition-all flex-shrink-0"
        >
          <span>View AI Detour Plan</span>
          <ArrowUpRight className="w-4 h-4" />
        </button>
      </div>

      {/* Two Column Layout: Recent Disruptions & Active In-Transit Convoys */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left: Active Road Disruptions Table */}
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-white">Active Road & Bridge Disruptions</h3>
              <p className="text-xs text-slate-500">Live monitored incident feeds across NER corridors</p>
            </div>
            <button
              onClick={onNavigateToAlerts}
              className="text-xs text-blue-600 dark:text-blue-400 font-semibold hover:underline"
            >
              View all 8 alerts →
            </button>
          </div>

          <div className="space-y-3">
            {SYSTEM_ALERTS.slice(0, 4).map((alert) => (
              <div
                key={alert.id}
                className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700/60 space-y-1.5"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span
                      className={`w-2 h-2 rounded-full ${
                        alert.severity === "CRITICAL" ? "bg-red-500" : "bg-amber-500"
                      }`}
                    />
                    <h4 className="text-xs font-bold text-slate-900 dark:text-slate-100">{alert.title}</h4>
                  </div>
                  <span className="text-[10px] text-slate-400 font-mono">{alert.timeAgo}</span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300">{alert.description}</p>
                <div className="text-[11px] text-blue-600 dark:text-blue-400 font-medium pt-0.5">
                  Action: {alert.recommendedAction}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right: Real-time Convoys in Transit */}
        <div className="p-5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-white">Convoys In Transit</h3>
              <p className="text-xs text-slate-500">Live GPS tracking of critical supply transports</p>
            </div>
            <button
              onClick={onNavigateToVehicles}
              className="text-xs text-blue-600 dark:text-blue-400 font-semibold hover:underline"
            >
              Open Fleet Tracker →
            </button>
          </div>

          <div className="space-y-3">
            {FLEET_VEHICLES.slice(0, 4).map((vehicle) => (
              <div
                key={vehicle.id}
                className="p-3.5 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700/60 flex items-center justify-between"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-blue-100 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 flex items-center justify-center font-bold">
                    <Truck className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-bold text-slate-900 dark:text-white">{vehicle.registration}</span>
                      <span
                        className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${
                          vehicle.status === "Moving"
                            ? "bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-400"
                            : vehicle.status === "Delayed"
                            ? "bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-400"
                            : "bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300"
                        }`}
                      >
                        {vehicle.status}
                      </span>
                    </div>
                    <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
                      {vehicle.cargo} • Driver: {vehicle.driverName}
                    </p>
                    <p className="text-[11px] text-blue-600 dark:text-blue-400 font-semibold">
                      To: {vehicle.destination.name}
                    </p>
                  </div>
                </div>

                <div className="text-right text-xs">
                  {vehicle.speedKmh ? (
                    <span className="font-mono font-bold text-emerald-600 dark:text-emerald-400 block">
                      {vehicle.speedKmh} km/h
                    </span>
                  ) : (
                    <span className="text-slate-400 block font-mono">0 km/h</span>
                  )}
                  <span className="text-[10px] text-slate-400">{vehicle.timeAgo}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
