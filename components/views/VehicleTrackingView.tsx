"use client";

import { useState, useMemo } from "react";
import {
  Search,
  Truck,
  Navigation,
  Fuel,
  BatteryCharging,
  Thermometer,
  Phone,
  Radio,
  MapPin,
  Clock,
  CheckCircle2,
  AlertTriangle
} from "lucide-react";
import { FLEET_VEHICLES, FleetVehicle } from "@/lib/fleetData";

export default function VehicleTrackingView() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedFilter, setSelectedFilter] = useState<string>("All");
  const [selectedVehicle, setSelectedVehicle] = useState<FleetVehicle | null>(null);

  const filterOptions = ["All", "Moving", "Idle", "Delayed", "Broken Down", "Loading", "Unloading"];

  const filteredVehicles = useMemo(() => {
    return FLEET_VEHICLES.filter((vehicle) => {
      const q = searchQuery.toLowerCase();
      const matchesSearch =
        !q ||
        vehicle.registration.toLowerCase().includes(q) ||
        vehicle.cargo.toLowerCase().includes(q) ||
        vehicle.driverName.toLowerCase().includes(q) ||
        vehicle.destination.name.toLowerCase().includes(q);

      const matchesFilter =
        selectedFilter === "All" || vehicle.status.toLowerCase() === selectedFilter.toLowerCase();

      return matchesSearch && matchesFilter;
    });
  }, [searchQuery, selectedFilter]);

  return (
    <div className="p-6 space-y-6 max-w-7xl mx-auto">
      {/* Title & Subtitle (Matching Screenshot 3) */}
      <div>
        <h2 className="text-xl font-bold text-slate-900 dark:text-white">Vehicle Tracking</h2>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
          GPS-based real-time movement of vehicles carrying essential commodities
        </p>
      </div>

      {/* Search Bar & Status Filter Pills (Matching Screenshot 3) */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        {/* Search Input */}
        <div className="relative w-full md:w-96">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by registration, cargo, or driver..."
            className="w-full bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl pl-10 pr-4 py-2.5 text-xs text-slate-900 dark:text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 shadow-sm"
          />
        </div>

        {/* Filter Pills (Matching Screenshot 3) */}
        <div className="flex flex-wrap gap-1.5">
          {filterOptions.map((f) => (
            <button
              key={f}
              onClick={() => setSelectedFilter(f)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all ${
                selectedFilter === f
                  ? "bg-slate-900 dark:bg-white text-white dark:text-slate-900 shadow-md"
                  : "bg-white dark:bg-slate-900 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white border border-slate-200 dark:border-slate-800"
              }`}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      {/* Two Column Layout (Matching Screenshot 3) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left: Vehicle List (Matching Screenshot 3) */}
        <div className="lg:col-span-7 space-y-3.5">
          {filteredVehicles.length === 0 ? (
            <div className="p-8 text-center bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
              No vehicles found matching "{searchQuery}".
            </div>
          ) : (
            filteredVehicles.map((vehicle) => {
              const isSelected = selectedVehicle?.id === vehicle.id;
              return (
                <div
                  key={vehicle.id}
                  onClick={() => setSelectedVehicle(vehicle)}
                  className={`p-4 bg-white dark:bg-slate-900 rounded-2xl border transition-all cursor-pointer shadow-sm hover:shadow ${
                    isSelected
                      ? "border-blue-500 ring-2 ring-blue-500/20 shadow-md"
                      : "border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700"
                  }`}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 flex items-center justify-center">
                        <Truck className="w-5 h-5" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm font-bold text-slate-900 dark:text-white font-mono">
                            {vehicle.registration}
                          </span>
                          <span
                            className={`text-[10px] px-2 py-0.5 rounded-full font-bold flex items-center gap-1 ${
                              vehicle.status === "Moving"
                                ? "bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-400"
                                : vehicle.status === "Delayed"
                                ? "bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-400"
                                : vehicle.status === "Loading"
                                ? "bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-400"
                                : vehicle.status === "Broken Down"
                                ? "bg-red-100 text-red-800 dark:bg-red-950 dark:text-red-400"
                                : "bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300"
                            }`}
                          >
                            <span className="w-1.5 h-1.5 rounded-full bg-current" />
                            {vehicle.status}
                          </span>
                        </div>
                        <p className="text-xs text-slate-600 dark:text-slate-400 mt-0.5">
                          {vehicle.cargo} · {vehicle.vehicleType}
                        </p>
                        <p className="text-xs text-slate-500 dark:text-slate-400">
                          Driver: {vehicle.driverName}
                        </p>
                      </div>
                    </div>

                    <div className="text-right">
                      {vehicle.speedKmh ? (
                        <div className="flex items-center gap-1 text-emerald-600 dark:text-emerald-400 text-xs font-bold font-mono">
                          <Navigation className="w-3 h-3" />
                          <span>{vehicle.speedKmh} km/h</span>
                        </div>
                      ) : null}
                      <span className="text-[11px] text-slate-400 block mt-1">{vehicle.timeAgo}</span>
                    </div>
                  </div>

                  {/* Destination with GPS Coordinates (Matching Screenshot 3) */}
                  <div className="mt-3 pt-3 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs">
                    <div className="flex items-center gap-1.5 text-blue-600 dark:text-blue-400 font-semibold">
                      <Navigation className="w-3.5 h-3.5" />
                      <span>
                        To: {vehicle.destination.name}{" "}
                        <span className="font-mono text-slate-400 font-normal">
                          {vehicle.destination.lat.toFixed(3)}°N, {vehicle.destination.lng.toFixed(3)}°E
                        </span>
                      </span>
                    </div>

                    {vehicle.telemetry.cargoTempCelsius !== undefined && (
                      <span className="px-2 py-0.5 rounded bg-blue-50 dark:bg-blue-950 text-blue-600 dark:text-blue-400 font-mono text-[11px] font-bold">
                        Temp: {vehicle.telemetry.cargoTempCelsius}°C
                      </span>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* Right: Vehicle Detail Panel (Matching Screenshot 3 empty state & filled state) */}
        <div className="lg:col-span-5">
          {!selectedVehicle ? (
            <div className="p-12 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm text-center flex flex-col items-center justify-center min-h-[360px] text-slate-400">
              <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-slate-300 dark:text-slate-600 mb-3">
                <Truck className="w-8 h-8" />
              </div>
              <p className="text-sm font-semibold text-slate-600 dark:text-slate-400">
                Select a vehicle to see details
              </p>
              <p className="text-xs text-slate-400 mt-1 max-w-xs">
                Click any convoy card on the left to inspect live telemetry, fuel, cargo status, and driver contact.
              </p>
            </div>
          ) : (
            <div className="p-6 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm space-y-5">
              <div className="flex items-start justify-between border-b border-slate-100 dark:border-slate-800 pb-4">
                <div>
                  <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                    Vehicle Inspection HUD
                  </span>
                  <h3 className="text-lg font-bold text-slate-900 dark:text-white font-mono mt-0.5">
                    {selectedVehicle.registration}
                  </h3>
                  <p className="text-xs text-slate-500">
                    {selectedVehicle.cargo} · {selectedVehicle.vehicleType}
                  </p>
                </div>
                <span
                  className={`text-xs px-2.5 py-1 rounded-full font-bold ${
                    selectedVehicle.status === "Moving"
                      ? "bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-400"
                      : "bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-400"
                  }`}
                >
                  {selectedVehicle.status}
                </span>
              </div>

              {/* Telemetry Gauges */}
              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl border border-slate-100 dark:border-slate-800">
                  <div className="flex items-center gap-2 text-xs text-slate-500">
                    <Fuel className="w-3.5 h-3.5 text-amber-500" />
                    <span>Fuel Level</span>
                  </div>
                  <p className="text-lg font-bold font-mono text-slate-900 dark:text-white mt-1">
                    {selectedVehicle.telemetry.fuelPercent}%
                  </p>
                </div>

                <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl border border-slate-100 dark:border-slate-800">
                  <div className="flex items-center gap-2 text-xs text-slate-500">
                    <BatteryCharging className="w-3.5 h-3.5 text-emerald-500" />
                    <span>Aux Battery</span>
                  </div>
                  <p className="text-lg font-bold font-mono text-slate-900 dark:text-white mt-1">
                    {selectedVehicle.telemetry.batteryPercent}%
                  </p>
                </div>
              </div>

              {/* Cargo Temperature (if cold chain) */}
              {selectedVehicle.telemetry.cargoTempCelsius !== undefined && (
                <div className="p-3.5 bg-blue-50 dark:bg-blue-950/40 border border-blue-200 dark:border-blue-900/60 rounded-xl flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2">
                    <Thermometer className="w-4 h-4 text-blue-600" />
                    <div>
                      <p className="font-bold text-blue-900 dark:text-blue-300">Cold-Chain Cargo Temperature</p>
                      <p className="text-[11px] text-blue-700 dark:text-blue-400">Target Range: 2.0°C – 6.0°C</p>
                    </div>
                  </div>
                  <span className="text-base font-bold font-mono text-blue-600">
                    {selectedVehicle.telemetry.cargoTempCelsius}°C
                  </span>
                </div>
              )}

              {/* Driver & Location Info */}
              <div className="space-y-3 text-xs">
                <div className="flex items-center justify-between p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl">
                  <div>
                    <span className="text-slate-400 block text-[11px]">Assigned Driver</span>
                    <span className="font-bold text-slate-900 dark:text-white">{selectedVehicle.driverName}</span>
                  </div>
                  <a
                    href={`tel:${selectedVehicle.driverPhone}`}
                    className="flex items-center gap-1 px-3 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-700 text-white font-semibold text-xs transition-colors"
                  >
                    <Phone className="w-3.5 h-3.5" />
                    <span>Call Driver</span>
                  </a>
                </div>

                <div className="p-3 bg-slate-50 dark:bg-slate-800/60 rounded-xl space-y-1">
                  <div className="flex items-center gap-1.5 text-slate-400 text-[11px]">
                    <MapPin className="w-3.5 h-3.5 text-blue-500" />
                    <span>Current Position & Checkpoint</span>
                  </div>
                  <p className="font-semibold text-slate-900 dark:text-white">
                    {selectedVehicle.currentLocation.landmark}
                  </p>
                  <p className="font-mono text-slate-500 text-[11px]">
                    {selectedVehicle.currentLocation.lat.toFixed(4)}°N, {selectedVehicle.currentLocation.lng.toFixed(4)}°E
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
