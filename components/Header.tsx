"use client";

import { Wifi, Activity, Sparkles, Radio, Share2 } from "lucide-react";

interface HeaderProps {
  title: string;
  onOpenChat: () => void;
  onOpenLocationShare: () => void;
  isSharingLocation?: boolean;
}

export default function Header({
  title,
  onOpenChat,
  onOpenLocationShare,
  isSharingLocation = false
}: HeaderProps) {
  return (
    <header className="px-6 py-4 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800/80 flex items-center justify-between transition-colors">
      <div>
        <h1 className="text-xl font-bold text-slate-900 dark:text-white tracking-tight">{title}</h1>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
          North Eastern Region · Smart Logistics & Accessibility Intelligence
        </p>
      </div>

      <div className="flex items-center gap-3">
        {/* System Online Pill (Matching Screenshot 2) */}
        <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-800/60 text-xs font-semibold shadow-sm">
          <Wifi className="w-3.5 h-3.5" />
          <span>System Online</span>
        </div>

        {/* Date Pill (Matching Screenshot 2) */}
        <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700 text-xs font-semibold shadow-sm">
          <Activity className="w-3.5 h-3.5 text-blue-500" />
          <span>20 Sept 2026</span>
        </div>

        {/* Real-time Location Share HUD Trigger */}
        <button
          onClick={onOpenLocationShare}
          title="Encrypted Location Sharing"
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full border text-xs font-semibold transition-all ${
            isSharingLocation
              ? "bg-emerald-500 text-white border-emerald-600 shadow-md shadow-emerald-500/20 animate-pulse"
              : "bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700 hover:border-slate-400"
          }`}
        >
          <Radio className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">{isSharingLocation ? "Broadcasting" : "Share GPS"}</span>
        </button>

        {/* Gemini Tactical AI Chatbot Trigger */}
        <button
          onClick={onOpenChat}
          className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white text-xs font-bold shadow-md shadow-blue-600/20 transition-all"
        >
          <Sparkles className="w-3.5 h-3.5" />
          <span>Tactical AI</span>
        </button>
      </div>
    </header>
  );
}
