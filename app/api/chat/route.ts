import { NextRequest, NextResponse } from "next/server";

const SYSTEM_PROMPT = `
You are the NER-ResQRoute Tactical Dispatch & Logistics Support Agent for the North-Eastern Region (NER) of India.
You assist emergency convoy drivers, disaster relief coordinators, and hospital medical officers navigating monsoonal landslides and road blockages across Assam, Meghalaya, and the Barak Valley.

CORE CAPABILITIES:
1. Multi-Step Logistics Bookings:
   - Step 1: Identify Cargo type (Cold-Chain Insulin/Vaccines, Cryogenic Oxygen, Blood Plasma, Emergency Rations).
   - Step 2: Confirm Origin Depot (Guwahati Central Depot, Tezpur Base) and Destination Hospital (Shillong Civil Hospital, Silchar Relief Base, Itanagar Emergency Hospital).
   - Step 3: Check road accessibility (warn about the active NH-6 Sonapur 400m mudslide and route via the Umrangso/Haflong 94% clear highland bypass).
   - Step 4: Confirm booking and provide a dispatch reference code (e.g., MED-RESQ-XXXX).
2. Context Awareness & Conversation Memory:
   - Always remember earlier user messages, previously selected destinations, and cargo specifications from earlier in this chat session.
   - If user asks follow-up questions (e.g. "what route will that take?", "can you add an extra cylinder?"), respond with full awareness of their pending booking.

TONE & STYLE:
- Calm, tactical, objective, and reassuring. Keep responses focused, structured, and easy to read.
`;

export async function POST(req: NextRequest) {
  try {
    const { messages } = await req.json();

    if (!messages || !Array.isArray(messages) || messages.length === 0) {
      return NextResponse.json({ error: "Missing messages array" }, { status: 400 });
    }

    const apiKey = process.env.GEMINI_API_KEY;
    const latestMessage = messages[messages.length - 1].content;

    // If API key is available, call Gemini 3.5 Flash REST API
    if (apiKey && apiKey !== "MY_GEMINI_API_KEY") {
      try {
        const contents = [
          ...messages.slice(-8).map((m: { role: string; content: string }) => ({
            role: m.role === "user" ? "user" : "model",
            parts: [{ text: m.content }]
          }))
        ];

        const response = await fetch(
          `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${apiKey}`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              systemInstruction: {
                parts: [{ text: SYSTEM_PROMPT }]
              },
              contents
            })
          }
        );

        if (response.ok) {
          const data = await response.json();
          const replyText = data?.candidates?.[0]?.content?.parts?.[0]?.text;
          if (replyText) {
            return NextResponse.json({ reply: replyText.trim(), source: "gemini-3.5-flash" });
          }
        }
      } catch (apiErr) {
        console.warn("Gemini API call failed, falling back to local reasoning engine:", apiErr);
      }
    }

    // Heuristic Context-Aware Fallback Engine
    const lower = latestMessage.toLowerCase();
    const historyText = messages.map((m: any) => m.content.toLowerCase()).join(" ");

    const hasMentionedMedicine = historyText.includes("insulin") || historyText.includes("vaccine") || historyText.includes("medicine");
    const hasMentionedOxygen = historyText.includes("oxygen") || historyText.includes("cylinder");
    const hasMentionedSilchar = historyText.includes("silchar");
    const hasMentionedShillong = historyText.includes("shillong");

    let reply = "";
    if (lower.includes("book") || lower.includes("dispatch") || lower.includes("reserve")) {
      reply = "Roger that. Initiating priority mission booking:\n\n• Step 1: Please specify cargo type (e.g. Cold-Chain Insulin/Vaccines, Cryogenic Oxygen, Blood Plasma, Ready Rations).\n• Step 2: Provide destination hospital/camp.\n\nI will calculate safe routing around the active Sonapur landslide automatically.";
    } else if (lower.includes("confirm") || lower.includes("yes") || lower.includes("proceed")) {
      const cargo = hasMentionedOxygen ? "Cryogenic Oxygen Cylinders" : hasMentionedMedicine ? "Cold-Chain Insulin & Pediatric Vaccines" : "Critical Relief Supplies";
      const dest = hasMentionedSilchar ? "Silchar Relief Base Hospital" : "Shillong Civil Medical Center";
      const code = `NER-DISPATCH-${Math.floor(1000 + Math.random() * 9000)}`;
      reply = `✓ Booking Confirmed & Dispatched!\n\n• Reference Code: ${code}\n• Cargo: ${cargo} (Target Temp: <4°C)\n• Routing: Guwahati Central Depot → Umrangso Highland Bypass → ${dest}\n• Assigned Escort: Convoy Delta-1 (4x4 High-Clearance)\n• ETA: ~4h 20m. Real-time GPS telemetry is active.`;
    } else if (lower.includes("sonapur") || lower.includes("landslide") || lower.includes("road") || lower.includes("hazard")) {
      reply = "Status Update: NH-6 Sonapur Tunnel Km 141 corridor is currently 0% accessible due to a 400m mudslide and continuous rockfall. Convoy traffic is routed via the SH-7 Umrangso-Dima Hasao highland bypass (94% accessible).";
    } else if (lower.includes("share") || lower.includes("gps") || lower.includes("location")) {
      reply = "You can share live GPS coordinates using the 'Share Location' button on the dashboard. Choose from verified commanders (State Disaster HQ, Convoy Lead) with enforced time limits (15m–8h) and battery-saver mesh beacon mode.";
    } else if (hasMentionedMedicine && !lower.includes("confirm")) {
      reply = "Understood. For cold-chain medical supplies, we maintain temperature-monitored pods at 2°C–6°C. Which destination facility should we book for: Shillong Civil Hospital or Silchar Relief Base?";
    } else if (hasMentionedOxygen && !lower.includes("confirm")) {
      reply = "Understood. High-pressure oxygen cylinders and concentrators will be assigned to a hazard-certified heavy transport convoy. Shall I confirm dispatch to Silchar Relief Base Hospital?";
    } else {
      reply = "Tactical Dispatch Standby. I am monitoring all active NER road corridors, elevation gradients, and convoys. How can I assist with your supply routing or mission bookings today? (Try: 'Book cold-chain dispatch' or 'Check Sonapur status').";
    }

    return NextResponse.json({ reply, source: "offline-heuristic-engine" });
  } catch (err: any) {
    return NextResponse.json({ error: err.message || "Internal server error" }, { status: 500 });
  }
}
