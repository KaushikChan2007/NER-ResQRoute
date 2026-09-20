"use client";

import { useEffect, useState } from "react";
import dynamic from "next/dynamic";
import { GisPoint, HazardIncident, RouteOption, ACTIVE_HAZARDS, NER_POINTS } from "@/lib/gisData";

interface LeafletGisMapProps {
  activeRoute: RouteOption;
  alternateRoute: RouteOption;
  hazards: HazardIncident[];
  hubs: GisPoint[];
  selectedHub: GisPoint | null;
  highlightedIds: Set<string>;
  onSelectHub: (hub: GisPoint) => void;
  onSelectHazard: (hazard: HazardIncident) => void;
  telemetry: {
    lat: number;
    lng: number;
    altitude: number;
    speed: number;
    heading: number;
  };
}

// Dynamically import Leaflet components to prevent Next.js SSR window errors
const MapContainer = dynamic(
  () => import("react-leaflet").then((mod) => mod.MapContainer),
  { ssr: false }
);
const TileLayer = dynamic(
  () => import("react-leaflet").then((mod) => mod.TileLayer),
  { ssr: false }
);
const Marker = dynamic(
  () => import("react-leaflet").then((mod) => mod.Marker),
  { ssr: false }
);
const Popup = dynamic(
  () => import("react-leaflet").then((mod) => mod.Popup),
  { ssr: false }
);
const Polyline = dynamic(
  () => import("react-leaflet").then((mod) => mod.Polyline),
  { ssr: false }
);
const Circle = dynamic(
  () => import("react-leaflet").then((mod) => mod.Circle),
  { ssr: false }
);

export default function LeafletGisMap({
  activeRoute,
  alternateRoute,
  hazards,
  hubs,
  selectedHub,
  highlightedIds,
  onSelectHub,
  onSelectHazard,
  telemetry
}: LeafletGisMapProps) {
  const [isClient, setIsClient] = useState(false);
  const [L, setL] = useState<any>(null);

  useEffect(() => {
    setIsClient(true);
    import("leaflet").then((leaflet) => {
      setL(leaflet.default);
    });
  }, []);

  if (!isClient || !L) {
    return (
      <div className="w-full h-full flex items-center justify-center bg-luxe-obsidian text-earth-300">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-2 border-earth-400 border-t-transparent rounded-full animate-spin" />
          <span className="text-xs tracking-wider uppercase font-semibold">Initializing Leaflet GIS Canvas...</span>
        </div>
      </div>
    );
  }

  // Create custom marker icon based on category
  const createIcon = (color: string, label: string, isHighlighted: boolean) => {
    return L.divIcon({
      className: "custom-gis-marker",
      html: `
        <div style="
          position: relative;
          display: flex;
          align-items: center;
          justify-content: center;
          width: ${isHighlighted ? '36px' : '28px'};
          height: ${isHighlighted ? '36px' : '28px'};
          background-color: ${color};
          border: 2px solid ${isHighlighted ? '#F59E0B' : '#FFFFFF'};
          border-radius: 50%;
          box-shadow: 0 4px 10px rgba(0,0,0,0.5);
          cursor: pointer;
          ${isHighlighted ? 'box-shadow: 0 0 15px #F59E0B;' : ''}
        ">
          <span style="color: white; font-size: 11px; font-weight: bold;">${label}</span>
        </div>
      `,
      iconSize: [28, 28],
      iconAnchor: [14, 14],
      popupAnchor: [0, -16]
    });
  };

  const convoyIcon = L.divIcon({
    className: "convoy-marker",
    html: `
      <div style="
        width: 32px;
        height: 32px;
        background-color: #3B82F6;
        border: 3px solid #FFFFFF;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 0 20px rgba(59, 130, 246, 0.9);
      ">
        <span style="color: white; font-size: 14px;">🚚</span>
      </div>
    `,
    iconSize: [32, 32],
    iconAnchor: [16, 16]
  });

  const activePathPositions = activeRoute.waypoints.map((p) => [p.lat, p.lng] as [number, number]);
  const alternatePathPositions = alternateRoute.waypoints.map((p) => [p.lat, p.lng] as [number, number]);

  return (
    <div className="relative w-full h-full">
      <MapContainer
        center={[25.5788, 92.2036]}
        zoom={8}
        scrollWheelZoom={true}
        className="w-full h-full z-0"
      >
        {/* Carto Dark Matter / OpenStreetMap tiles */}
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
          url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
        />

        {/* Alternate Route (Dotted/Lower Opacity) */}
        <Polyline
          positions={alternatePathPositions}
          pathOptions={{
            color: alternateRoute.isBlocked ? "#DC2626" : "#6B7280",
            weight: 4,
            opacity: 0.6,
            dashArray: "8, 8"
          }}
        />

        {/* Active Route (Solid Glow) */}
        <Polyline
          positions={activePathPositions}
          pathOptions={{
            color: activeRoute.isBlocked ? "#DC2626" : "#10B981",
            weight: 6,
            opacity: 0.9
          }}
        />

        {/* Hazard Radius Circles */}
        {hazards.map((hazard) => (
          <Circle
            key={hazard.id}
            center={[hazard.lat, hazard.lng]}
            radius={hazard.radiusMeters}
            pathOptions={{
              color: hazard.severity === "CRITICAL_BLOCKED" ? "#DC2626" : "#F59E0B",
              fillColor: hazard.severity === "CRITICAL_BLOCKED" ? "#DC2626" : "#F59E0B",
              fillOpacity: 0.35,
              weight: 2
            }}
            eventHandlers={{
              click: () => onSelectHazard(hazard)
            }}
          >
            <Popup>
              <div className="p-2 space-y-1 text-xs">
                <span className="font-bold text-red-600 block">{hazard.title}</span>
                <p className="text-gray-700">{hazard.highway}</p>
                <p className="font-semibold text-gray-900">Accessibility: {hazard.accessibilityPercent}%</p>
                <p className="text-gray-600 italic">{hazard.description}</p>
                <div className="mt-1 pt-1 border-t border-gray-200 text-green-700 font-bold">
                  Detour: {hazard.recommendedDetour}
                </div>
              </div>
            </Popup>
          </Circle>
        ))}

        {/* Convoy Telemetry Marker */}
        <Marker position={[telemetry.lat, telemetry.lng]} icon={convoyIcon}>
          <Popup>
            <div className="p-2 text-xs">
              <span className="font-bold text-blue-600 block">Convoy Delta-1 (Lead Transport)</span>
              <p>Alt: {telemetry.altitude}m | Speed: {telemetry.speed} km/h</p>
              <p>GPS: {telemetry.lat.toFixed(4)}, {telemetry.lng.toFixed(4)}</p>
            </div>
          </Popup>
        </Marker>

        {/* Strategic NER Hubs & Depots */}
        {hubs.map((hub) => {
          const isHighlighted = highlightedIds.has(hub.id);
          const color =
            hub.category === "Relief Depot" ? "#3B82F6" :
            hub.category === "Field Hospital" ? "#10B981" :
            hub.category === "Hazard Zone" ? "#DC2626" :
            hub.category === "Refuge Camp" ? "#8B5CF6" : "#D97706";

          const label =
            hub.category === "Relief Depot" ? "D" :
            hub.category === "Field Hospital" ? "H" :
            hub.category === "Hazard Zone" ? "!" :
            hub.category === "Refuge Camp" ? "R" : "P";

          return (
            <Marker
              key={hub.id}
              position={[hub.lat, hub.lng]}
              icon={createIcon(color, label, isHighlighted)}
              eventHandlers={{
                click: () => onSelectHub(hub)
              }}
            >
              <Popup>
                <div className="p-2 space-y-1 text-xs max-w-xs">
                  <div className="flex items-center justify-between gap-2">
                    <span className="font-bold text-gray-900">{hub.name}</span>
                    <span className="px-1.5 py-0.5 bg-amber-100 text-amber-800 font-bold rounded">
                      ★ {hub.rating}
                    </span>
                  </div>
                  <p className="text-earth-600 font-semibold">{hub.category} • Alt: {hub.elevationMeters}m</p>
                  <p className="text-gray-600">{hub.address}</p>
                  <p className="text-gray-500 italic">{hub.description}</p>
                  <p className="font-mono text-gray-700 font-semibold">{hub.contactPhone}</p>
                  <div className="flex flex-wrap gap-1 mt-1">
                    {hub.tags.map((t, idx) => (
                      <span key={idx} className="bg-gray-100 text-gray-700 px-1 py-0.5 rounded text-[10px]">
                        {t}
                      </span>
                    ))}
                  </div>
                </div>
              </Popup>
            </Marker>
          );
        })}
      </MapContainer>
    </div>
  );
}
