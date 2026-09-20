import { NextResponse } from "next/server";
import { ACTIVE_HAZARDS, NER_POINTS } from "@/lib/gisData";

export async function GET() {
  return NextResponse.json({
    timestamp: Date.now(),
    hazardsCount: ACTIVE_HAZARDS.length,
    hazards: ACTIVE_HAZARDS,
    hubsCount: NER_POINTS.length,
    hubs: NER_POINTS
  });
}
