"use client";

import { useState } from "react";
import { DownloadCloud, CheckCircle2, Trash2, ShieldCheck, X, HardDrive } from "lucide-react";
import { OFFLINE_REGIONS, OfflineRegion } from "@/lib/gisData";

interface OfflineCacheModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function OfflineCacheModal({ isOpen, onClose }: OfflineCacheModalProps) {
  const [regions, setRegions] = useState<OfflineRegion[]>(OFFLINE_REGIONS);
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleDownload = (id: string) => {
    setDownloadingId(id);
    let progress = 20;

    const interval = setInterval(() => {
      progress += 25;
      if (progress >= 100) {
        clearInterval(interval);
        setRegions((prev) =>
          prev.map((r) => (r.id === id ? { ...r, isDownloaded: true, downloadProgress: 100 } : r))
        );
        setDownloadingId(null);
      } else {
        setRegions((prev) =>
          prev.map((r) => (r.id === id ? { ...r, downloadProgress: progress } : r))
        );
      }
    }, 400);
  };

  const handleDelete = (id: string) => {
    setRegions((prev) =>
      prev.map((r) => (r.id === id ? { ...r, isDownloaded: false, downloadProgress: 0 } : r))
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md bg-earth-900 border border-earth-700/80 rounded-2xl shadow-2xl p-5 text-earth-100 space-y-4">
        <div className="flex items-center justify-between border-b border-earth-800 pb-3">
          <div className="flex items-center gap-2">
            <HardDrive className="w-5 h-5 text-luxe-copper" />
            <div>
              <h3 className="text-sm font-bold uppercase tracking-wider">Offline Map Packages</h3>
              <p className="text-[11px] text-earth-400">Encrypted Vector & Elevation Caches</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg text-earth-400 hover:text-earth-100 hover:bg-earth-800">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Security Banner */}
        <div className="p-3 bg-earth-950/80 border border-earth-800 rounded-xl text-xs text-earth-300 flex items-center gap-2">
          <ShieldCheck className="w-4 h-4 text-emerald-400 flex-shrink-0" />
          <span>Cached regions preserve elevation contours, hospital geofences, and offline AI detour algorithms.</span>
        </div>

        {/* Region List */}
        <div className="space-y-3 max-h-72 overflow-y-auto pr-1">
          {regions.map((region) => (
            <div
              key={region.id}
              className="p-3.5 bg-earth-950 border border-earth-800 rounded-xl space-y-2 text-xs"
            >
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-bold text-earth-100">{region.title}</h4>
                  <p className="text-[11px] text-earth-400">{region.coverage}</p>
                </div>
                <span className="px-2 py-0.5 rounded bg-earth-800 text-earth-300 font-mono text-[11px]">
                  {region.sizeMb} MB
                </span>
              </div>

              <div className="flex flex-wrap gap-1 text-[10px] text-earth-400">
                {region.features.map((f, i) => (
                  <span key={i} className="bg-earth-900 px-1.5 py-0.5 rounded border border-earth-800">
                    • {f}
                  </span>
                ))}
              </div>

              {downloadingId === region.id ? (
                <div className="space-y-1 pt-1">
                  <div className="w-full bg-earth-800 h-2 rounded-full overflow-hidden">
                    <div
                      className="bg-luxe-copper h-full transition-all duration-300"
                      style={{ width: `${region.downloadProgress}%` }}
                    />
                  </div>
                  <span className="text-[10px] text-luxe-copper font-semibold">
                    Encrypting vectors & caching... {region.downloadProgress}%
                  </span>
                </div>
              ) : (
                <div className="flex items-center justify-between pt-1">
                  {region.isDownloaded ? (
                    <span className="text-emerald-400 font-semibold flex items-center gap-1 text-[11px]">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Available Offline
                    </span>
                  ) : (
                    <span className="text-earth-500 text-[11px]">Not downloaded</span>
                  )}

                  {region.isDownloaded ? (
                    <button
                      onClick={() => handleDelete(region.id)}
                      className="px-2.5 py-1 rounded bg-red-950/40 text-red-400 border border-red-900/60 hover:bg-red-900/60 text-[11px] flex items-center gap-1 transition-colors"
                    >
                      <Trash2 className="w-3 h-3" />
                      <span>Remove</span>
                    </button>
                  ) : (
                    <button
                      onClick={() => handleDownload(region.id)}
                      className="px-3 py-1 rounded bg-luxe-copper hover:bg-earth-600 text-white font-semibold text-[11px] flex items-center gap-1 transition-colors shadow-md"
                    >
                      <DownloadCloud className="w-3 h-3" />
                      <span>Download</span>
                    </button>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
