import { encryptData, decryptData } from "./crypto";

export interface QueuedFieldReport {
  id: string;
  hazardType: string;
  corridor: string;
  severity: string;
  encryptedDescription: string;
  lat: number;
  lng: number;
  timestamp: number;
  isSynced: boolean;
}

const DB_NAME = "NER_ResQRoute_DB";
const STORE_NAME = "field_reports_queue";

function openDb(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    if (typeof window === "undefined") return;
    const request = indexedDB.open(DB_NAME, 1);

    request.onupgradeneeded = (event: any) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: "id" });
      }
    };

    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

export async function saveFieldReportToOfflineQueue(
  hazardType: string,
  corridor: string,
  severity: string,
  description: string,
  lat: number,
  lng: number
): Promise<QueuedFieldReport> {
  const encryptedDesc = await encryptData(description);
  const report: QueuedFieldReport = {
    id: "REP-" + Date.now(),
    hazardType,
    corridor,
    severity,
    encryptedDescription: encryptedDesc,
    lat,
    lng,
    timestamp: Date.now(),
    isSynced: false
  };

  const db = await openDb();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, "readwrite");
    const store = tx.objectStore(STORE_NAME);
    store.put(report);
    tx.oncomplete = () => resolve(report);
    tx.onerror = () => reject(tx.error);
  });
}

export async function getAllQueuedReports(): Promise<QueuedFieldReport[]> {
  if (typeof window === "undefined") return [];
  try {
    const db = await openDb();
    return new Promise((resolve) => {
      const tx = db.transaction(STORE_NAME, "readonly");
      const store = tx.objectStore(STORE_NAME);
      const req = store.getAll();
      req.onsuccess = () => resolve(req.result || []);
      req.onerror = () => resolve([]);
    });
  } catch {
    return [];
  }
}

export async function markReportSyncedInDb(id: string): Promise<void> {
  const db = await openDb();
  return new Promise((resolve) => {
    const tx = db.transaction(STORE_NAME, "readwrite");
    const store = tx.objectStore(STORE_NAME);
    const req = store.get(id);
    req.onsuccess = () => {
      const report = req.result;
      if (report) {
        report.isSynced = true;
        store.put(report);
      }
      resolve();
    };
    req.onerror = () => resolve();
  });
}
