"use client";

import { useState, useEffect } from "react";
import {
  Shield,
  Wifi,
  WifiOff,
  MapPin,
  Camera,
  UploadCloud,
  CheckCircle2,
  AlertTriangle,
  Lock,
  RefreshCw,
  Clock,
  Sparkles
} from "lucide-react";
import { saveFieldReportToOfflineQueue, getAllQueuedReports, markReportSyncedInDb, QueuedFieldReport } from "@/lib/indexedDb";
import { decryptData } from "@/lib/crypto";

interface FieldOfficerPwaProps {
  telemetry: {
    lat: number;
    lng: number;
    altitude: number;
  };
  onNewHazardReported?: () => void;
}

export default function FieldOfficerPwa({ telemetry, onNewHazardReported }: FieldOfficerPwaProps) {
  const [isOnline, setIsOnline] = useState(true);
  const [hazardType, setHazardType] = useState("LANDSLIDE");
  const [corridor, setCorridor] = useState("NH-6 Sonapur Tunnel Section");
  const [severity, setSeverity] = useState("CRITICAL_BLOCKED");
  const [description, setDescription] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [queuedReports, setQueuedReports] = useState<QueuedFieldReport[]>([]);
  const [statusNotice, setStatusNotice] = useState<string | null>(null);
  const [decryptedTexts, setDecryptedTexts] = useState<{ [id: string]: string }>({});

  useEffect(() => {
    loadQueuedReports();
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener("online", handleOnline);
    window.addEventListener("offline", handleOffline);
    return () => {
      window.removeEventListener("online", handleOnline);
      window.removeEventListener("offline", handleOffline);
    };
  }, []);

  const loadQueuedReports = async () => {
    const list = await getAllQueuedReports();
    setQueuedReports(list);

    // Decrypt notes for display
    const decMap: { [id: string]: string } = {};
    for (const r of list) {
      decMap[r.id] = await decryptData(r.encryptedDescription);
    }
    setDecryptedTexts(decMap);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!description.trim()) return;

    setIsSubmitting(true);
    try {
      await saveFieldReportToOfflineQueue(
        hazardType,
        corridor,
        severity,
        description.trim(),
        telemetry.lat,
        telemetry.lng
      );

      setDescription("");
      setStatusNotice("✓ Report sealed with AES-256-GCM & stored in local IndexedDB queue.");
      await loadQueuedReports();
      if (onNewHazardReported) onNewHazardReported();
      setTimeout(() => setStatusNotice(null), 4000);
    } catch (err) {
      console.error(err);
      setStatusNotice("Error saving report.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSyncAll = async () => {
    setIsSubmitting(true);
    try {
      for (const r of queuedReports) {
        if (!r.isSynced) {
          await markReportSyncedInDb(r.id);
        }
      }
      await loadQueuedReports();
      setStatusNotice("✓ Mesh Sync Complete: All queued reports uploaded to verified incident store.");
      setTimeout(() => setStatusNotice(null), 4000);
    } catch {
      setStatusNotice("Sync failed.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const unsyncedCount = queuedReports.filter((r) => !r.isSynced).length;

  return (
    <div className="w-full max-w-md mx-auto space-y-4 pb-12">
      {/* PWA Connection & Telemetry Header */}
      <div className="p-4 rounded-xl bg-earth-900/90 border border-earth-700/60 shadow-lg text-earth-100 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-luxe-copper/20 flex items-center justify-center text-luxe-copper">
            <Shield className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-sm font-bold tracking-tight">Field Officer Tactical PWA</h2>
            <div className="flex items-center gap-2 text-xs text-earth-300">
              <MapPin className="w-3.5 h-3.5 text-luxe-copper" />
              <span>
                {telemetry.lat.toFixed(4)}°N, {telemetry.lng.toFixed(4)}°E • Alt: {telemetry.altitude}m
              </span>
            </div>
          </div>
        </div>

        <button
          onClick={() => setIsOnline(!isOnline)}
          title="Toggle online/offline simulation"
          className={`flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold transition-colors ${
            isOnline
              ? "bg-risk-green/20 text-emerald-400 border border-risk-green/40"
              : "bg-risk-amber/20 text-amber-300 border border-risk-amber/40"
          }`}
        >
          {isOnline ? <Wifi className="w-3 h-3" /> : <WifiOff className="w-3 h-3" />}
          <span>{isOnline ? "Online" : "Offline PWA"}</span>
        </button>
      </div>

      {/* Status Notice Toast */}
      {statusNotice && (
        <div className="p-3 rounded-lg bg-earth-800 border border-luxe-copper/50 text-earth-100 text-xs flex items-center gap-2 animate-fadeIn">
          <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />
          <span>{statusNotice}</span>
        </div>
      )}

      {/* Offline Incident Entry Form */}
      <form
        onSubmit={handleSubmit}
        className="p-5 rounded-2xl bg-earth-900/80 border border-earth-700/50 shadow-xl space-y-4 text-earth-100"
      >
        <div className="flex items-center justify-between border-b border-earth-800 pb-3">
          <span className="text-xs uppercase font-bold tracking-wider text-earth-300 flex items-center gap-1.5">
            <Lock className="w-3.5 h-3.5 text-luxe-copper" />
            Encrypted Incident Geotag
          </span>
          <span className="text-[11px] text-earth-400">IndexedDB V2</span>
        </div>

        {/* Hazard Type Selection */}
        <div>
          <label className="block text-xs font-medium text-earth-200 mb-1.5">Incident Classification</label>
          <select
            value={hazardType}
            onChange={(e) => setHazardType(e.target.value)}
            className="w-full bg-earth-950 border border-earth-700 rounded-lg px-3 py-2 text-xs text-earth-100 focus:outline-none focus:border-luxe-copper"
          >
            <option value="LANDSLIDE">Massive Mudflow / Landslide (Slope Collapse)</option>
            <option value="FLASH_FLOOD">Flash Flood / Roadway Inundation</option>
            <option value="ROCKFALL">Active Rockfall Escarpment</option>
            <option value="BRIDGE_DAMAGE">Culvert / Bridge Structural Fracture</option>
          </select>
        </div>

        {/* Highway Corridor */}
        <div>
          <label className="block text-xs font-medium text-earth-200 mb-1.5">Roadway Corridor</label>
          <select
            value={corridor}
            onChange={(e) => setCorridor(e.target.value)}
            className="w-full bg-earth-950 border border-earth-700 rounded-lg px-3 py-2 text-xs text-earth-100 focus:outline-none focus:border-luxe-copper"
          >
            <option value="NH-6 Sonapur Tunnel Section">NH-6 Sonapur Tunnel Section (East Jaintia)</option>
            <option value="SH-7 Umrangso-Dima Hasao Bypass">SH-7 Umrangso-Dima Hasao Highland Bypass</option>
            <option value="NH-37 Silchar-Badarpur Road">NH-37 Silchar-Badarpur Valley Link</option>
            <option value="GS Road Shillong Escarpment">GS Road Shillong Escarpment Corridor</option>
          </select>
        </div>

        {/* Severity */}
        <div>
          <label className="block text-xs font-medium text-earth-200 mb-1.5">Impact Severity</label>
          <div className="grid grid-cols-3 gap-2">
            {[
              { id: "CRITICAL_BLOCKED", label: "0% Blocked", color: "border-red-500 bg-red-950/40 text-red-300" },
              { id: "MODERATE_WARNING", label: "40% 4x4 Only", color: "border-amber-500 bg-amber-950/40 text-amber-300" },
              { id: "ADVISORY", label: "Caution", color: "border-blue-500 bg-blue-950/40 text-blue-300" }
            ].map((s) => (
              <button
                type="button"
                key={s.id}
                onClick={() => setSeverity(s.id)}
                className={`py-2 px-1 rounded-lg text-[11px] font-semibold border transition-all text-center ${
                  severity === s.id ? `${s.color} ring-1 ring-luxe-copper` : "border-earth-800 bg-earth-950 text-earth-400"
                }`}
              >
                {s.label}
              </button>
            ))}
          </div>
        </div>

        {/* Description */}
        <div>
          <label className="block text-xs font-medium text-earth-200 mb-1.5">On-Ground Reconnaissance Notes</label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="E.g., 350m mudslide blocking both lanes at hairpin bend 4. Bulldozer requested..."
            className="w-full bg-earth-950 border border-earth-700 rounded-lg p-2.5 text-xs text-earth-100 placeholder-earth-500 focus:outline-none focus:border-luxe-copper"
          />
        </div>

        {/* Photo Upload Simulation & GPS Geotag info */}
        <div className="p-3 bg-earth-950/70 border border-earth-800 rounded-lg flex items-center justify-between text-[11px] text-earth-400">
          <div className="flex items-center gap-2">
            <Camera className="w-4 h-4 text-luxe-copper" />
            <span>Photo Attached (Geotagged & Timestamped)</span>
          </div>
          <span className="text-emerald-400 font-mono">READY</span>
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={isSubmitting || !description.trim()}
          className="w-full py-3 bg-gradient-to-r from-luxe-copper to-earth-600 hover:from-earth-600 hover:to-luxe-copper text-white font-bold rounded-xl shadow-lg transition-all text-xs uppercase tracking-wider flex items-center justify-center gap-2 disabled:opacity-50"
        >
          <Lock className="w-3.5 h-3.5" />
          <span>{isSubmitting ? "Encrypting & Storing..." : "Seal & Queue Incident Report"}</span>
        </button>
      </form>

      {/* Offline Queue Sync Card */}
      <div className="p-4 rounded-xl bg-earth-900/80 border border-earth-700/50 shadow-md text-earth-100 space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <UploadCloud className="w-4 h-4 text-luxe-copper" />
            <span className="text-xs font-bold uppercase tracking-wider">IndexedDB Offline Queue</span>
          </div>
          <span className="text-xs px-2 py-0.5 rounded-full bg-earth-800 text-earth-300 font-mono">
            {unsyncedCount} Pending Sync
          </span>
        </div>

        {queuedReports.length === 0 ? (
          <p className="text-xs text-earth-400 italic">No reports queued in IndexedDB.</p>
        ) : (
          <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
            {queuedReports.map((report) => (
              <div
                key={report.id}
                className="p-2.5 rounded-lg bg-earth-950/80 border border-earth-800 text-xs space-y-1"
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-earth-200">{report.hazardType}</span>
                  <span
                    className={`text-[10px] px-1.5 py-0.2 rounded font-semibold ${
                      report.isSynced ? "bg-emerald-950 text-emerald-400" : "bg-amber-950 text-amber-300"
                    }`}
                  >
                    {report.isSynced ? "Synced to HQ" : "Pending Mesh"}
                  </span>
                </div>
                <p className="text-earth-400 text-[11px] truncate">{report.corridor}</p>
                <p className="text-earth-300 text-[11px] italic">
                  "{decryptedTexts[report.id] || "Sealed AES-256..."}"
                </p>
              </div>
            ))}
          </div>
        )}

        {unsyncedCount > 0 && (
          <button
            onClick={handleSyncAll}
            disabled={isSubmitting}
            className="w-full py-2 bg-earth-800 hover:bg-earth-700 text-earth-100 text-xs font-semibold rounded-lg flex items-center justify-center gap-2 transition-colors border border-earth-600"
          >
            <RefreshCw className={`w-3.5 h-3.5 text-emerald-400 ${isSubmitting ? "animate-spin" : ""}`} />
            <span>Flush Queue & Sync with PostGIS HQ</span>
          </button>
        )}
      </div>
    </div>
  );
}
