"use client";

import { useState } from "react";
import {
  Layers,
  Map as MapIcon,
  AlertTriangle,
  Truck,
  CheckCircle2,
  Info,
  X,
  Compass,
  Navigation,
  Activity
} from "lucide-react";
import { DISTRICT_NODES, NETWORK_EDGES, DistrictNode } from "@/lib/gisGraphData";
import { FLEET_VEHICLES } from "@/lib/fleetData";
import LeafletGisMap from "@/components/LeafletGisMap";
import { ROUTE_PLANS, ACTIVE_HAZARDS, NER_POINTS } from "@/lib/gisData";

export default function GisMapView() {
  // Layer toggles matching Screenshot 4
  const [showRoutes, setShowRoutes] = useState(true);
  const [showDisruptions, setShowDisruptions] = useState(true);
  const [showVehicles, setShowVehicles] = useState(true);

  // Map view mode: SVG Network Graph (Screenshot 4) vs Leaflet Satellite Canvas
  const [mapMode, setMapMode] = useState<"GRAPH" | "LEAFLET">("GRAPH");
  const [selectedNode, setSelectedNode] = useState<DistrictNode | null>(null);

  // Coordinates mapping helper for SVG canvas
  const getNodePos = (nodeId: string) => {
    const node = DISTRICT_NODES.find((n) => n.id === nodeId);
    if (!node) return { x: 50, y: 50 };
    return { x: node.xPercent, y: node.yPercent };
  };

  return (
    <div className="p-6 space-y-4 max-w-7xl mx-auto">
      {/* Top Section Header & Layer Toggles (Matching Screenshot 4) */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 dark:text-white">GIS Accessibility Map</h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
            Real-time road, bridge, and transport accessibility across NER districts
          </p>
        </div>

        <div className="flex items-center gap-2">
          {/* Layer Toggle Pills (Matching Screenshot 4) */}
          <div className="flex bg-slate-900 p-1 rounded-xl text-xs font-semibold gap-1 text-white shadow-md">
            <button
              onClick={() => setShowRoutes(!showRoutes)}
              className={`px-3 py-1.5 rounded-lg transition-colors ${
                showRoutes ? "bg-slate-800 text-white" : "text-slate-400 hover:text-white"
              }`}
            >
              Routes
            </button>
            <button
              onClick={() => setShowDisruptions(!showDisruptions)}
              className={`px-3 py-1.5 rounded-lg transition-colors ${
                showDisruptions ? "bg-slate-800 text-white" : "text-slate-400 hover:text-white"
              }`}
            >
              Disruptions
            </button>
            <button
              onClick={() => setShowVehicles(!showVehicles)}
              className={`px-3 py-1.5 rounded-lg transition-colors ${
                showVehicles ? "bg-slate-800 text-white" : "text-slate-400 hover:text-white"
              }`}
            >
              Vehicles
            </button>
          </div>

          {/* Graph vs Leaflet Switcher */}
          <button
            onClick={() => setMapMode(mapMode === "GRAPH" ? "LEAFLET" : "GRAPH")}
            className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-xs font-bold text-slate-700 dark:text-slate-300 hover:border-slate-400 transition-all shadow-sm"
          >
            <MapIcon className="w-3.5 h-3.5 text-blue-500" />
            <span>{mapMode === "GRAPH" ? "Satellite Tiles" : "Network Topology"}</span>
          </button>
        </div>
      </div>

      {/* Main Map Canvas */}
      <div className="relative w-full h-[640px] bg-slate-50 dark:bg-[#07111E] rounded-3xl border border-slate-200 dark:border-slate-800 shadow-xl overflow-hidden">
        {mapMode === "LEAFLET" ? (
          /* High-Resolution Leaflet GIS Canvas */
          <LeafletGisMap
            activeRoute={ROUTE_PLANS[0]}
            alternateRoute={ROUTE_PLANS[1]}
            hazards={ACTIVE_HAZARDS}
            hubs={NER_POINTS}
            selectedHub={null}
            highlightedIds={new Set()}
            onSelectHub={() => {}}
            onSelectHazard={() => {}}
            telemetry={{ lat: 25.42, lng: 92.18, altitude: 1340, speed: 42, heading: 118 }}
          />
        ) : (
          /* Stylized Geospatial District Graph matching Screenshot 4 */
          <div className="relative w-full h-full select-none">
            {/* Soft Grid Background matching Screenshot 4 */}
            <div
              className="absolute inset-0 opacity-40 dark:opacity-20 pointer-events-none"
              style={{
                backgroundImage: `
                  linear-gradient(to right, #94a3b8 1px, transparent 1px),
                  linear-gradient(to bottom, #94a3b8 1px, transparent 1px)
                `,
                backgroundSize: "40px 40px"
              }}
            />

            {/* SVG Roads & Connections matching Screenshot 4 */}
            {showRoutes && (
              <svg className="absolute inset-0 w-full h-full pointer-events-none">
                {NETWORK_EDGES.map((edge) => {
                  const from = getNodePos(edge.fromId);
                  const to = getNodePos(edge.toId);
                  const isBlocked = edge.status === "blocked";
                  const isRestricted = edge.status === "restricted";

                  return (
                    <g key={edge.id}>
                      <line
                        x1={`${from.x}%`}
                        y1={`${from.y}%`}
                        x2={`${to.x}%`}
                        y2={`${to.y}%`}
                        stroke={isBlocked ? "#EF4444" : isRestricted ? "#F59E0B" : "#10B981"}
                        strokeWidth={isBlocked ? 2.5 : 2}
                        strokeDasharray={isBlocked ? "6, 6" : undefined}
                        strokeOpacity={isBlocked ? 0.9 : 0.75}
                      />
                    </g>
                  );
                })}
              </svg>
            )}

            {/* Disruption Alert Badges matching Screenshot 4 */}
            {showDisruptions && (
              <>
                {/* Sonapur mudslide alert circle */}
                <div
                  className="absolute z-10 -translate-x-1/2 -translate-y-1/2 flex items-center justify-center cursor-pointer"
                  style={{ left: "51%", top: "67%" }}
                  title="NH-6 Sonapur 400m Mudslide (0% Passable)"
                >
                  <div className="w-5 h-5 rounded-full bg-red-600 text-white text-[11px] font-bold flex items-center justify-center shadow-lg animate-pulse">
                    !
                  </div>
                </div>

                {/* Sikkim Mangan landslide */}
                <div
                  className="absolute z-10 -translate-x-1/2 -translate-y-1/2 flex items-center justify-center cursor-pointer"
                  style={{ left: "9.5%", top: "43%" }}
                  title="North Sikkim Slope Slump"
                >
                  <div className="w-4 h-4 rounded-full bg-red-600 text-white text-[10px] font-bold flex items-center justify-center shadow">
                    !
                  </div>
                </div>

                {/* Tura overflow */}
                <div
                  className="absolute z-10 -translate-x-1/2 -translate-y-1/2 flex items-center justify-center cursor-pointer"
                  style={{ left: "33%", top: "63%" }}
                  title="Tura Rockfall Warning"
                >
                  <div className="w-4 h-4 rounded-full bg-amber-500 text-white text-[10px] font-bold flex items-center justify-center shadow">
                    !
                  </div>
                </div>

                {/* Tawang pass freezing drizzle */}
                <div
                  className="absolute z-10 -translate-x-1/2 -translate-y-1/2 flex items-center justify-center cursor-pointer"
                  style={{ left: "47%", top: "40%" }}
                  title="Sela Pass Ice Warning"
                >
                  <div className="w-4 h-4 rounded-full bg-amber-500 text-white text-[10px] font-bold flex items-center justify-center shadow">
                    !
                  </div>
                </div>

                {/* Mizoram Kolasib bridge */}
                <div
                  className="absolute z-10 -translate-x-1/2 -translate-y-1/2 flex items-center justify-center cursor-pointer"
                  style={{ left: "57%", top: "81%" }}
                  title="Vairengte Bridge Abutment Fracture"
                >
                  <div className="w-4 h-4 rounded-full bg-red-600 text-white text-[10px] font-bold flex items-center justify-center shadow">
                    !
                  </div>
                </div>
              </>
            )}

            {/* Live Convoy Vehicles matching Screenshot 4 */}
            {showVehicles && (
              <>
                {FLEET_VEHICLES.slice(0, 5).map((veh, i) => {
                  const offsets = [
                    { x: 50, y: 52 }, // near Guwahati
                    { x: 60, y: 47 }, // near Tezpur
                    { x: 70, y: 46 }, // near Itanagar
                    { x: 51, y: 64 }, // near Shillong
                    { x: 74, y: 56 }  // near Jorhat
                  ];
                  const pos = offsets[i % offsets.length];

                  return (
                    <div
                      key={veh.id}
                      className="absolute z-20 -translate-x-1/2 -translate-y-1/2 cursor-pointer transition-transform hover:scale-125"
                      style={{ left: `${pos.x}%`, top: `${pos.y}%` }}
                      title={`${veh.registration} (${veh.cargo}) · ${veh.status}`}
                    >
                      <div className="w-6 h-6 rounded-full bg-blue-600 border-2 border-white shadow-lg flex items-center justify-center text-white text-[10px]">
                        🚚
                      </div>
                    </div>
                  );
                })}
              </>
            )}

            {/* District Nodes matching Screenshot 4 */}
            {DISTRICT_NODES.map((node) => {
              const isSelected = selectedNode?.id === node.id;
              const isHub = node.isHub;

              return (
                <div
                  key={node.id}
                  onClick={() => setSelectedNode(node)}
                  className="absolute z-10 -translate-x-1/2 -translate-y-1/2 flex flex-col items-center cursor-pointer group"
                  style={{ left: `${node.xPercent}%`, top: `${node.yPercent}%` }}
                >
                  {/* Node Marker */}
                  {isHub ? (
                    // Black Square Hub for Guwahati & Tezpur matching Screenshot 4
                    <div
                      className={`w-3.5 h-3.5 bg-black dark:bg-white rounded-sm shadow-md transition-transform group-hover:scale-125 ${
                        isSelected ? "ring-4 ring-blue-500" : ""
                      }`}
                    />
                  ) : (
                    // Colored Circle Node for other districts
                    <div
                      className={`w-3 h-3 rounded-full border border-white shadow-sm transition-transform group-hover:scale-125 ${
                        node.status === "clear"
                          ? "bg-emerald-500"
                          : node.status === "disrupted"
                          ? "bg-red-500"
                          : "bg-amber-500"
                      } ${isSelected ? "ring-4 ring-blue-500 scale-125" : ""}`}
                    />
                  )}

                  {/* District Label matching Screenshot 4 */}
                  <span
                    className={`mt-1 text-[10px] font-semibold whitespace-nowrap px-1 rounded transition-colors ${
                      isSelected
                        ? "bg-blue-600 text-white font-bold"
                        : "text-slate-800 dark:text-slate-200 bg-white/70 dark:bg-slate-900/70"
                    }`}
                  >
                    {node.name}
                  </span>
                </div>
              );
            })}
          </div>
        )}

        {/* Selected District Information Card Overlay */}
        {selectedNode && (
          <div className="absolute bottom-4 left-4 z-30 w-80 bg-white/95 dark:bg-slate-900/95 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-2xl p-4 backdrop-blur-md animate-slideUp text-xs space-y-2.5">
            <div className="flex items-start justify-between">
              <div>
                <span className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">
                  {selectedNode.state} · {selectedNode.district}
                </span>
                <h4 className="text-sm font-bold text-slate-900 dark:text-white">{selectedNode.name}</h4>
              </div>
              <button
                onClick={() => setSelectedNode(null)}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700 dark:hover:text-slate-200"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            </div>

            <div className="grid grid-cols-2 gap-2 text-[11px] pt-1 border-t border-slate-100 dark:border-slate-800">
              <div>
                <span className="text-slate-400">Road Status:</span>
                <p
                  className={`font-bold ${
                    selectedNode.status === "clear"
                      ? "text-emerald-500"
                      : selectedNode.status === "disrupted"
                      ? "text-red-500"
                      : "text-amber-500"
                  }`}
                >
                  {selectedNode.status.toUpperCase()}
                </p>
              </div>
              <div>
                <span className="text-slate-400">Population Served:</span>
                <p className="font-bold text-slate-800 dark:text-slate-200">{selectedNode.populationServed}</p>
              </div>
            </div>

            <div className="pt-1 flex items-center justify-between text-[11px] text-slate-500">
              <span>GPS: {selectedNode.lat.toFixed(2)}°N, {selectedNode.lng.toFixed(2)}°E</span>
              <span className="text-blue-500 font-semibold">{selectedNode.isHub ? "Staging Hub" : "Transit Node"}</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
