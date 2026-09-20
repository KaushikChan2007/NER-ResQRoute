import { NextRequest, NextResponse } from "next/server";
import { findOptimalRoute } from "@/lib/routingEngine";

export async function POST(req: NextRequest) {
  try {
    const { startId = "GH-01", targetId = "SL-05", ignoreBlockages = false } = await req.json();
    const result = findOptimalRoute(startId, targetId, ignoreBlockages);
    return NextResponse.json({
      success: true,
      algorithm: "A* / Dijkstra with Terrain Slope & Rainfall Mm/h Weighting",
      data: result
    });
  } catch (err: any) {
    return NextResponse.json({ success: false, error: err.message }, { status: 500 });
  }
}
