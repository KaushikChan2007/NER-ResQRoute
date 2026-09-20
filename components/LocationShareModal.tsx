"use client";

import { useState, useEffect } from "react";
import { Share2, Clock, BatteryCharging, ShieldAlert, X, Check, Radio } from "lucide-react";
import { SHARE_CONTACTS, ShareContact } from "@/lib/gisData";

interface LocationShareModalProps {
  isOpen: boolean;
  onClose: () => void;
  telemetry: {
    lat: number;
    lng: number;
  };
  activeSession: {
    contactName: string;
    expiresAt: number;
    isBatterySaver: boolean;
  } | null;
  onStartShare: (contact: ShareContact, durationMins: number, batterySaver: boolean) => void;
  onStopShare: () => void;
}

export default function LocationShareModal({
  isOpen,
  onClose,
  telemetry,
  activeSession,
  onStartShare,
  onStopShare
}: LocationShareModalProps) {
  const [selectedContact, setSelectedContact] = useState<ShareContact>(SHARE_CONTACTS[0]);
  const [duration, setDuration] = useState<number>(60);
  const [batterySaver, setBatterySaver] = useState<boolean>(true);
  const [timeLeftStr, setTimeLeftStr] = useState<string>("");

  useEffect(() => {
    if (!activeSession) return;

    const interval = setInterval(() => {
      const now = Date.now();
      const diff = activeSession.expiresAt - now;
      if (diff <= 0) {
        onStopShare();
      } else {
        const m = Math.floor(diff / 60000);
        const s = Math.floor((diff % 60000) / 1000);
        setTimeLeftStr(`${m}m ${s}s`);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [activeSession, onStopShare]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-fadeIn">
      <div className="w-full max-w-md bg-earth-900 border border-earth-700/80 rounded-2xl shadow-2xl p-5 text-earth-100 space-y-4">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-earth-800 pb-3">
          <div className="flex items-center gap-2">
            <Radio className={`w-5 h-5 ${activeSession ? "text-emerald-400 animate-pulse" : "text-luxe-copper"}`} />
            <div>
              <h3 className="text-sm font-bold uppercase tracking-wider">Encrypted Location Sharing</h3>
              <p className="text-[11px] text-earth-400">AES-256-GCM Telemetry Beacon</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg text-earth-400 hover:text-earth-100 hover:bg-earth-800">
            <X className="w-4 h-4" />
          </button>
        </div>

        {activeSession ? (
          /* Active Broadcast Status & Revoke */
          <div className="space-y-4 py-2">
            <div className="p-4 rounded-xl bg-emerald-950/40 border border-emerald-500/40 space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-emerald-300 flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
                  Broadcasting Encrypted Coordinates
                </span>
                <span className="text-xs font-mono font-bold text-emerald-400">{timeLeftStr}</span>
              </div>
              <p className="text-xs text-earth-200">
                Authorized Recipient: <span className="font-semibold text-white">{activeSession.contactName}</span>
              </p>
              <p className="text-[11px] text-earth-400">
                Mode: {activeSession.isBatterySaver ? "30s Mesh Beacon (Battery Saver)" : "5s Real-time GPS"}
              </p>
            </div>

            {/* Kill Switch Button */}
            <button
              onClick={() => {
                onStopShare();
              }}
              className="w-full py-3 bg-red-600 hover:bg-red-700 text-white font-bold rounded-xl shadow-lg transition-all text-xs uppercase tracking-wider flex items-center justify-center gap-2"
            >
              <ShieldAlert className="w-4 h-4" />
              <span>Revoke / Go Dark Immediately</span>
            </button>
          </div>
        ) : (
          /* Configure & Start Sharing */
          <div className="space-y-4">
            {/* Recipient Selection */}
            <div>
              <label className="block text-xs font-medium text-earth-200 mb-2">Select Verified Commander</label>
              <div className="space-y-2">
                {SHARE_CONTACTS.map((contact) => (
                  <div
                    key={contact.id}
                    onClick={() => setSelectedContact(contact)}
                    className={`p-3 rounded-xl border text-xs cursor-pointer flex items-center justify-between transition-all ${
                      selectedContact.id === contact.id
                        ? "border-luxe-copper bg-earth-950 ring-1 ring-luxe-copper"
                        : "border-earth-800 bg-earth-950/50 text-earth-400 hover:border-earth-700"
                    }`}
                  >
                    <div>
                      <p className="font-bold text-earth-100">{contact.name}</p>
                      <p className="text-[11px] text-earth-400">{contact.role} • {contact.agency}</p>
                    </div>
                    {selectedContact.id === contact.id && (
                      <Check className="w-4 h-4 text-luxe-copper" />
                    )}
                  </div>
                ))}
              </div>
            </div>

            {/* Duration Buttons */}
            <div>
              <label className="block text-xs font-medium text-earth-200 mb-2">Session Expiry Limit</label>
              <div className="grid grid-cols-4 gap-2">
                {[
                  { mins: 15, label: "15m" },
                  { mins: 60, label: "1h" },
                  { mins: 240, label: "4h" },
                  { mins: 480, label: "8h" },
                ].map((d) => (
                  <button
                    key={d.mins}
                    type="button"
                    onClick={() => setDuration(d.mins)}
                    className={`py-2 rounded-lg text-xs font-semibold border transition-all ${
                      duration === d.mins
                        ? "border-luxe-copper bg-luxe-copper/20 text-white"
                        : "border-earth-800 bg-earth-950 text-earth-400"
                    }`}
                  >
                    {d.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Battery Saver Toggle */}
            <div className="p-3 rounded-xl bg-earth-950 border border-earth-800 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <BatteryCharging className="w-4 h-4 text-emerald-400" />
                <div>
                  <p className="text-xs font-bold text-earth-200">Battery-Saver Mesh Beacon</p>
                  <p className="text-[10px] text-earth-400">Transmit in 30s bursts (Saves 85% battery)</p>
                </div>
              </div>
              <input
                type="checkbox"
                checked={batterySaver}
                onChange={(e) => setBatterySaver(e.target.checked)}
                className="w-4 h-4 rounded text-luxe-copper focus:ring-0 cursor-pointer"
              />
            </div>

            {/* Start Button */}
            <button
              onClick={() => {
                onStartShare(selectedContact, duration, batterySaver);
              }}
              className="w-full py-3 bg-gradient-to-r from-luxe-copper to-earth-600 hover:from-earth-600 hover:to-luxe-copper text-white font-bold rounded-xl shadow-lg transition-all text-xs uppercase tracking-wider flex items-center justify-center gap-2"
            >
              <Share2 className="w-4 h-4" />
              <span>Broadcast Live Coordinates ({duration}m)</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
