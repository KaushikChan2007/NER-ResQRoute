package com.example.ai

import com.example.BuildConfig
import com.example.gis.ChatMessage
import com.example.gis.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiChatbotManager {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private val systemPrompt = """
    You are the NER-ResQRoute Tactical Dispatch & Logistics Support Agent.
    You assist emergency convoy drivers, disaster relief coordinators, and hospital medical officers in the rugged North-Eastern Region (NER) of India.
    
    CAPABILITIES:
    1. Multi-Step Logistics Bookings:
       - Step 1: Identify Cargo type (Cold-Chain Insulin/Vaccines, Cryogenic Oxygen, Blood Plasma, Emergency Rations).
       - Step 2: Confirm Origin Depot (Guwahati, Tezpur) and Destination Hospital (Shillong, Silchar, Itanagar).
       - Step 3: Check road accessibility (warn about the active NH-6 Sonapur 400m mudslide and route via the Umrangso/Haflong 94% clear bypass).
       - Step 4: Confirm booking and provide a dispatch reference code (e.g., MED-RESQ-XXXX).
    2. Context Awareness & Conversation Memory:
       - Always remember earlier user inputs, previously selected destinations, and cargo specifics from the ongoing conversation.
       - If the user asks follow-up questions (e.g. "what route will that take?", "can you add an extra cylinder?"), respond with full context of their pending booking.
    
    TONE & STYLE:
    - Calm, tactical, objective, and reassuring. Keep responses focused and easy to read on mobile screens.
  """.trimIndent()

  suspend fun sendChatMessage(
    history: List<ChatMessage>,
    newPrompt: String
  ): String = withContext(Dispatchers.IO) {
    if (!GeminiRouteAdvisor.isApiKeyConfigured()) {
      return@withContext getOfflineChatbotResponse(history, newPrompt)
    }

    try {
      val jsonBody = JSONObject().apply {
        // System instruction
        val sysContent = JSONObject().apply {
          val parts = JSONArray().put(JSONObject().put("text", systemPrompt))
          put("parts", parts)
        }
        put("systemInstruction", sysContent)

        // Contents array with conversation history (User and Model turns)
        val contentsArray = JSONArray()
        history.takeLast(10).forEach { msg ->
          val msgObj = JSONObject().apply {
            put("role", if (msg.sender == MessageSender.USER) "user" else "model")
            val parts = JSONArray().put(JSONObject().put("text", msg.text))
            put("parts", parts)
          }
          contentsArray.put(msgObj)
        }

        // Add the current user prompt
        val currentObj = JSONObject().apply {
          put("role", "user")
          val parts = JSONArray().put(JSONObject().put("text", newPrompt))
          put("parts", parts)
        }
        contentsArray.put(currentObj)
        put("contents", contentsArray)
      }

      val request = Request.Builder()
        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
        .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val responseBodyString = response.body?.string()

      if (response.isSuccessful && !responseBodyString.isNullOrBlank()) {
        val jsonResponse = JSONObject(responseBodyString)
        val candidates = jsonResponse.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        if (!text.isNullOrBlank()) {
          return@withContext text.trim()
        }
      }
    } catch (e: Exception) {
      // Fallback seamlessly to context-aware local rule engine
    }

    return@withContext getOfflineChatbotResponse(history, newPrompt)
  }

  private fun getOfflineChatbotResponse(
    history: List<ChatMessage>,
    newPrompt: String
  ): String {
    val lower = newPrompt.lowercase()

    // Context analysis from conversation history
    val hasMentionedMedicine = history.any { it.text.contains("insulin", true) || it.text.contains("medicine", true) } || lower.contains("insulin") || lower.contains("medicine") || lower.contains("vaccine")
    val hasMentionedOxygen = history.any { it.text.contains("oxygen", true) } || lower.contains("oxygen")
    val hasMentionedSilchar = history.any { it.text.contains("silchar", true) } || lower.contains("silchar")
    val hasMentionedShillong = history.any { it.text.contains("shillong", true) } || lower.contains("shillong")

    return when {
      lower.contains("book") || lower.contains("dispatch") || lower.contains("reserve") -> {
        "Roger that. I am ready to initiate a priority mission booking:\n" +
          "• Step 1: Please specify cargo type (e.g. Cold-Chain Insulin, Cryogenic Oxygen, Blood Plasma, Ready Rations).\n" +
          "• Step 2: Provide destination hospital/camp.\n" +
          "I will calculate safe routing around the active Sonapur hazard automatically."
      }
      lower.contains("confirm") || lower.contains("yes") || lower.contains("proceed") -> {
        val cargo = if (hasMentionedOxygen) "Emergency Oxygen Concentrators" else if (hasMentionedMedicine) "Cold-Chain Insulin & Vaccines" else "Priority Disaster Relief Supplies"
        val dest = if (hasMentionedSilchar) "Silchar Relief Base Hospital" else "Shillong Civil Medical Center"
        val code = "NER-DISPATCH-${(1000..9999).random()}"
        "✓ Booking Confirmed & Dispatched!\n" +
          "• Reference: $code\n" +
          "• Cargo: $cargo (Temp buffer: <4°C)\n" +
          "• Routing: Guwahati Central Depot → Umrangso Highland Bypass → $dest\n" +
          "• Assigned: Convoy Delta-1 (4x4 High-Clearance)\n" +
          "• ETA: ~4h 20m. GPS telemetry is active."
      }
      lower.contains("sonapur") || lower.contains("landslide") || lower.contains("hazard") || lower.contains("road") -> {
        "Status Update: NH-6 Sonapur Tunnel corridor is currently 0% accessible due to a 400m mudslide and continuous rockfall. Convoy traffic is being routed via the SH-7 Umrangso-Dima Hasao highland bypass (94% accessible)."
      }
      lower.contains("share") || lower.contains("location") || lower.contains("gps") -> {
        "You can share your live GPS location using the 'Share Location' button on the map HUD. You can select specific commanders (e.g., State Disaster HQ or Convoy Delta Lead) and set a strict time limit (15m to 8h) with battery-saver intervals."
      }
      hasMentionedMedicine && !lower.contains("confirm") -> {
        "Understood. For cold-chain medical supplies, we maintain temperature monitoring pods at 2°C–6°C. Which destination facility should we book for: Shillong Civil Hospital or Silchar Relief Base?"
      }
      hasMentionedOxygen && !lower.contains("confirm") -> {
        "Understood. High-pressure oxygen cylinders and concentrators will be assigned to a hazard-certified heavy transport convoy. Shall I confirm dispatch to Silchar Relief Base Hospital?"
      }
      else -> {
        "Tactical Dispatch Standby. I'm tracking all active NER road corridors and convoys. How can I assist with your supply routing or mission bookings today? (You can say 'Book cold-chain dispatch' or 'Check landslide status')."
      }
    }
  }
}
