"use client";

import FieldOfficerPwa from "@/components/FieldOfficerPwa";

export default function FieldReportsView() {
  const telemetry = {
    lat: 25.0440,
    lng: 92.3680,
    altitude: 420
  };

  return (
    <div className="p-6 max-w-4xl mx-auto space-y-6">
      <div>
        <h2 className="text-xl font-bold text-slate-900 dark:text-white">Field Officer Incident Reporting</h2>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
          Submit on-ground roadblock reconnaissance with camera geotagging, AES-256-GCM client encryption, and IndexedDB offline queuing
        </p>
      </div>

      <FieldOfficerPwa telemetry={telemetry} />
    </div>
  );
}
