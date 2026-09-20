import { GisPoint, HazardIncident, NER_POINTS } from "./gisData";

export interface GraphEdge {
  fromId: string;
  toId: string;
  distanceKm: number;
  slopePercent: number;
  rainfallMmPerHour: number;
  isBlocked: boolean;
  blockageReason?: string;
}

export const CORRIDOR_EDGES: GraphEdge[] = [
  { fromId: "GH-01", toId: "SH-02", distanceKm: 98, slopePercent: 6.2, rainfallMmPerHour: 35, isBlocked: false },
  { fromId: "SH-02", toId: "JW-03", distanceKm: 64, slopePercent: 5.1, rainfallMmPerHour: 55, isBlocked: false },
  { fromId: "JW-03", toId: "SN-04", distanceKm: 52, slopePercent: 7.8, rainfallMmPerHour: 84, isBlocked: true, blockageReason: "NH-6 Sonapur 400m Mudslide" },
  { fromId: "SN-04", toId: "SL-05", distanceKm: 98, slopePercent: 4.0, rainfallMmPerHour: 62, isBlocked: false },
  // Resilient Bypass via Umrangso and Haflong
  { fromId: "SH-02", toId: "UM-06", distanceKm: 112, slopePercent: 4.5, rainfallMmPerHour: 22, isBlocked: false },
  { fromId: "UM-06", toId: "HF-07", distanceKm: 62, slopePercent: 5.0, rainfallMmPerHour: 28, isBlocked: false },
  { fromId: "HF-07", toId: "SL-05", distanceKm: 76, slopePercent: 3.8, rainfallMmPerHour: 30, isBlocked: false },
  // Northern feeder
  { fromId: "GH-01", toId: "TZ-08", distanceKm: 175, slopePercent: 1.2, rainfallMmPerHour: 18, isBlocked: false },
  { fromId: "TZ-08", toId: "IT-09", distanceKm: 145, slopePercent: 4.9, rainfallMmPerHour: 42, isBlocked: false },
  { fromId: "SL-05", toId: "BH-10", distanceKm: 28, slopePercent: 1.0, rainfallMmPerHour: 65, isBlocked: false },
];

/**
 * Calculates weighted cost for an edge factoring in distance, terrain slope,
 * monsoonal rainfall, and landslide obstructions.
 */
export function calculateEdgeCost(edge: GraphEdge): number {
  if (edge.isBlocked) {
    return Infinity; // Infinite cost for impassable routes
  }
  const slopePenalty = 1 + edge.slopePercent / 100;
  const rainfallPenalty = 1 + edge.rainfallMmPerHour / 60;
  return edge.distanceKm * slopePenalty * rainfallPenalty;
}

/**
 * Implements Dijkstra / A* route calculation across the NER GIS graph.
 */
export function findOptimalRoute(
  startId: string,
  targetId: string,
  ignoreBlockages: boolean = false
): {
  path: GisPoint[];
  totalDistanceKm: number;
  totalDurationMinutes: number;
  accessibilityScore: number;
  isBlocked: boolean;
} {
  const pointsMap = new Map<string, GisPoint>();
  NER_POINTS.forEach(p => pointsMap.set(p.id, p));

  const distances = new Map<string, number>();
  const previous = new Map<string, string | null>();
  const unvisited = new Set<string>();

  NER_POINTS.forEach(p => {
    distances.set(p.id, Infinity);
    previous.set(p.id, null);
    unvisited.add(p.id);
  });

  distances.set(startId, 0);

  while (unvisited.size > 0) {
    let currentId: string | null = null;
    let lowestDistance = Infinity;

    unvisited.forEach(nodeId => {
      const d = distances.get(nodeId) ?? Infinity;
      if (d < lowestDistance) {
        lowestDistance = d;
        currentId = nodeId;
      }
    });

    if (currentId === null || lowestDistance === Infinity || currentId === targetId) {
      break;
    }

    unvisited.delete(currentId);

    // Find neighboring edges
    const neighbors = CORRIDOR_EDGES.filter(
      e => (e.fromId === currentId || e.toId === currentId)
    );

    for (const edge of neighbors) {
      const neighborId = edge.fromId === currentId ? edge.toId : edge.fromId;
      if (!unvisited.has(neighborId)) continue;

      const edgeCost = ignoreBlockages
        ? edge.distanceKm * (1 + edge.slopePercent / 100)
        : calculateEdgeCost(edge);

      if (edgeCost === Infinity) continue;

      const newDistance = (distances.get(currentId) ?? 0) + edgeCost;
      if (newDistance < (distances.get(neighborId) ?? Infinity)) {
        distances.set(neighborId, newDistance);
        previous.set(neighborId, currentId);
      }
    }
  }

  // Reconstruct path
  const path: GisPoint[] = [];
  let curr: string | null = targetId;
  let hasBlockedEdge = false;

  while (curr !== null) {
    const point = pointsMap.get(curr);
    if (point) path.unshift(point);
    curr = previous.get(curr) ?? null;
  }

  // Calculate actual distance & metrics
  let totalKm = 0;
  for (let i = 0; i < path.length - 1; i++) {
    const from = path[i].id;
    const to = path[i + 1].id;
    const edge = CORRIDOR_EDGES.find(
      e => (e.fromId === from && e.toId === to) || (e.fromId === to && e.toId === from)
    );
    if (edge) {
      totalKm += edge.distanceKm;
      if (edge.isBlocked) hasBlockedEdge = true;
    }
  }

  const durationMin = Math.round((totalKm / 45) * 60);
  const accessibility = hasBlockedEdge ? 24 : 94;

  return {
    path,
    totalDistanceKm: totalKm,
    totalDurationMinutes: durationMin,
    accessibilityScore: accessibility,
    isBlocked: hasBlockedEdge
  };
}
