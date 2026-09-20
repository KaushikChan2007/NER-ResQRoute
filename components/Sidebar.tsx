"use client";

import {
  LayoutGrid,
  Map,
  Truck,
  Bell,
  Route,
  Package,
  FileText,
  RotateCw
} from "lucide-react";

export type NavTabId =
  | "dashboard"
  | "gis-map"
  | "vehicle-tracking"
  | "alerts-center"
  | "route-intelligence"
  | "deliveries"
  | "field-reports";

interface SidebarProps {
  activeTab: NavTabId;
  onSelectTab: (tab: NavTabId) => void;
  unacknowledgedAlertsCount?: number;
  onRefreshData: () => void;
  isRefreshing?: boolean;
}

export default function Sidebar({
  activeTab,
  onSelectTab,
  unacknowledgedAlertsCount = 2,
  onRefreshData,
  isRefreshing = false
}: SidebarProps) {
  const navItems: { id: NavTabId; label: string; icon: any; badge?: number }[] = [
    { id: "dashboard", label: "Dashboard", icon: LayoutGrid },
    { id: "gis-map", label: "GIS Map", icon: Map },
    { id: "vehicle-tracking", label: "Vehicle Tracking", icon: Truck },
    { id: "alerts-center", label: "Alerts Center", icon: Bell, badge: unacknowledgedAlertsCount },
    { id: "route-intelligence", label: "Route Intelligence", icon: Route },
    { id: "deliveries", label: "Deliveries", icon: Package },
    { id: "field-reports", label: "Field Reports", icon: FileText }
  ];

  return (
    <aside className="w-64 bg-[#0B132B] text-slate-300 flex flex-col justify-between p-4 border-r border-slate-800/80 select-none flex-shrink-0 min-h-screen">
      <div className="space-y-6">
        {/* Brand Header */}
        <div className="flex items-center gap-3 px-2 pt-2">
          <div className="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center text-white font-bold text-sm shadow-lg shadow-blue-500/20">
            NER
          </div>
          <div>
            <h1 className="text-sm font-bold text-white tracking-wide">NER-ResQRoute</h1>
            <p className="text-[10px] text-slate-400">SIH 2026 · BitForge</p>
          </div>
        </div>

        {/* Navigation Items (Matching Screenshot 1) */}
        <nav className="space-y-1.5 pt-2">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;

            return (
              <button
                key={item.id}
                onClick={() => onSelectTab(item.id)}
                className={`w-full flex items-center justify-between px-3.5 py-3 rounded-xl text-sm font-semibold transition-all ${
                  isActive
                    ? "bg-blue-600 text-white shadow-lg shadow-blue-600/30"
                    : "text-slate-400 hover:text-slate-100 hover:bg-slate-800/50"
                }`}
              >
                <div className="flex items-center gap-3">
                  <Icon className="w-5 h-5" />
                  <span>{item.label}</span>
                </div>

                {item.badge && item.badge > 0 && (
                  <span className="w-5 h-5 rounded-full bg-red-500 text-white text-[11px] font-bold flex items-center justify-center shadow-md">
                    {item.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Bottom: Refresh Data Button (Matching Screenshot 1) */}
      <div className="pt-4 border-t border-slate-800/80">
        <button
          onClick={onRefreshData}
          disabled={isRefreshing}
          className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 hover:text-white text-xs font-semibold border border-slate-800 transition-all shadow-sm"
        >
          <RotateCw className={`w-3.5 h-3.5 ${isRefreshing ? "animate-spin text-blue-400" : ""}`} />
          <span>{isRefreshing ? "Updating Feeds..." : "Refresh data"}</span>
        </button>
      </div>
    </aside>
  );
}
