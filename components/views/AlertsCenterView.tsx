"use client";

import { useState } from "react";
import {
  AlertTriangle,
  Bell,
  CheckCircle2,
  Radio,
  Send,
  ShieldAlert,
  Clock,
  Filter
} from "lucide-react";
import { SYSTEM_ALERTS, SystemAlert } from "@/lib/alertsData";

export default function AlertsCenterView() {
  const [alerts, setAlerts] = useState<SystemAlert[]>(SYSTEM_ALERTS);
  const [filter, setFilter] = useState<"ALL" | "UNACKNOWLEDGED" | "CRITICAL">("ALL");
  const [broadcastNotice, setBroadcastNotice] = useState<string | null>(null);

  const handleAcknowledge = (id: string) => {
    setAlerts((prev) =>
      prev.map((a) => (a.id === id ? { ...a, isAcknowledged: true } : a))
    );
  };

  const handleBroadcastSms = () => {
    setBroadcastNotice("✓ Emergency SMS broadcast transmitted to all 18 regional convoys & district cells.");
    setTimeout(() => setBroadcastNotice(null), 5000);
  };

  const filteredAlerts = alerts.filter((a) => {
    if (filter === "UNACKNOWLEDGED") return !a.isAcknowledged;
    if (filter === "CRITICAL") return a.severity === "CRITICAL";
    return true;
  });

  const unackCount = alerts.filter((a) => !a.isAcknowledged).length;

  return (
    <div className="p-6 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h2 className="text-xl font-bold text-slate-900 dark:text-white">Alerts Center</h2>
            {unackCount > 0 && (
              <span className="px-2 py-0.5 rounded-full bg-red-500 text-white text-xs font-bold shadow-sm">
                {unackCount} Unacknowledged
              </span>
            )}
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
            Real-time hazard notifications, roadblock advisories, and emergency broadcast dispatch
          </p>
        </div>

        <div className="flex items-center gap-2">
          {/* Filter Chips */}
          <div className="flex bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 p-1 rounded-xl text-xs font-semibold gap-1">
            <button
              onClick={() => setFilter("ALL")}
              className={`px-3 py-1.5 rounded-lg transition-colors ${
                filter === "ALL" ? "bg-blue-600 text-white" : "text-slate-500 hover:text-slate-900 dark:hover:text-white"
              }`}
            >
              All (8)
            </button>
            <button
              onClick={() => setFilter("UNACKNOWLEDGED")}
              className={`px-3 py-1.5 rounded-lg transition-colors ${
                filter === "UNACKNOWLEDGED" ? "bg-red-500 text-white" : "text-slate-500 hover:text-slate-900 dark:hover:text-white"
              }`}
            >
              Unacknowledged ({unackCount})
            </button>
            <button
              onClick={() => setFilter("CRITICAL")}
              className={`px-3 py-1.5 rounded-lg transition-colors ${
                filter === "CRITICAL" ? "bg-amber-500 text-white" : "text-slate-500 hover:text-slate-900 dark:hover:text-white"
              }`}
            >
              Critical Only
            </button>
          </div>

          {/* Broadcast SMS Action */}
          <button
            onClick={handleBroadcastSms}
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-red-600 hover:bg-red-700 text-white text-xs font-bold shadow-md shadow-red-600/20 transition-all"
          >
            <Send className="w-3.5 h-3.5" />
            <span>Broadcast Alert</span>
          </button>
        </div>
      </div>

      {/* Broadcast Banner Feedback */}
      {broadcastNotice && (
        <div className="p-3.5 rounded-xl bg-emerald-50 dark:bg-emerald-950/50 border border-emerald-300 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300 text-xs font-semibold flex items-center gap-2 animate-fadeIn">
          <CheckCircle2 className="w-4 h-4 text-emerald-500" />
          <span>{broadcastNotice}</span>
        </div>
      )}

      {/* Alerts Grid */}
      <div className="space-y-3.5">
        {filteredAlerts.map((alert) => (
          <div
            key={alert.id}
            className={`p-5 rounded-2xl bg-white dark:bg-slate-900 border transition-all shadow-sm ${
              !alert.isAcknowledged
                ? "border-red-400 dark:border-red-500/60 ring-1 ring-red-400/20 bg-red-50/20 dark:bg-red-950/10"
                : "border-slate-200 dark:border-slate-800"
            }`}
          >
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
              <div className="flex items-start gap-3.5">
                <div
                  className={`p-2.5 rounded-xl flex-shrink-0 ${
                    alert.severity === "CRITICAL"
                      ? "bg-red-100 dark:bg-red-950 text-red-600 dark:text-red-400"
                      : "bg-amber-100 dark:bg-amber-950 text-amber-600 dark:text-amber-400"
                  }`}
                >
                  <AlertTriangle className="w-5 h-5" />
                </div>

                <div>
                  <div className="flex items-center gap-2 flex-wrap">
                    <span
                      className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                        alert.severity === "CRITICAL"
                          ? "bg-red-100 text-red-800 dark:bg-red-900/60 dark:text-red-200"
                          : "bg-amber-100 text-amber-800 dark:bg-amber-900/60 dark:text-amber-200"
                      }`}
                    >
                      {alert.severity}
                    </span>
                    <h3 className="text-sm font-bold text-slate-900 dark:text-white">{alert.title}</h3>
                    <span className="text-xs text-slate-400 font-mono">· {alert.timeAgo}</span>
                  </div>

                  <p className="text-xs text-slate-600 dark:text-slate-300 mt-1">{alert.description}</p>
                  <p className="text-xs text-slate-500 mt-0.5">
                    Corridor: <span className="font-semibold text-slate-700 dark:text-slate-300">{alert.corridor}</span> (
                    {alert.district}, {alert.state})
                  </p>
                  <div className="text-xs text-blue-600 dark:text-blue-400 font-semibold mt-1">
                    Recommended: {alert.recommendedAction}
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2 flex-shrink-0">
                {!alert.isAcknowledged ? (
                  <button
                    onClick={() => handleAcknowledge(alert.id)}
                    className="px-3 py-1.5 rounded-xl bg-red-600 hover:bg-red-700 text-white font-bold text-xs shadow transition-all"
                  >
                    Acknowledge
                  </button>
                ) : (
                  <span className="flex items-center gap-1 text-emerald-600 dark:text-emerald-400 text-xs font-semibold">
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Acknowledged</span>
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
