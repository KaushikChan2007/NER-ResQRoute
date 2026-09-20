"use client";

import { useState, useRef, useEffect } from "react";
import { Bot, Send, User, Sparkles, X, CheckCircle, RefreshCw } from "lucide-react";

interface ChatMessage {
  id: string;
  role: "user" | "model";
  content: string;
}

interface TacticalChatbotProps {
  isOpen: boolean;
  onClose: () => void;
  onBookingConfirmed?: (bookingSummary: string) => void;
}

export default function TacticalChatbot({ isOpen, onClose, onBookingConfirmed }: TacticalChatbotProps) {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: "welcome",
      role: "model",
      content:
        "Tactical Dispatch Support ready. I have full context of active NER mountain roadblocks, monsoonal landslides, and strategic relief depots. How can I assist with your supply routing or priority convoy bookings today?"
    }
  ]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isLoading]);

  if (!isOpen) return null;

  const handleSend = async (textToSend?: string) => {
    const text = textToSend || input;
    if (!text.trim() || isLoading) return;

    const userMsg: ChatMessage = {
      id: "user-" + Date.now(),
      role: "user",
      content: text.trim()
    };

    const newHistory = [...messages, userMsg];
    setMessages(newHistory);
    setInput("");
    setIsLoading(true);

    try {
      const res = await fetch("/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ messages: newHistory })
      });

      const data = await res.json();
      const botMsg: ChatMessage = {
        id: "bot-" + Date.now(),
        role: "model",
        content: data.reply || "Unable to reach tactical dispatch network. Recheck connectivity."
      };

      setMessages((prev) => [...prev, botMsg]);

      // Check if booking was confirmed
      if (data.reply?.includes("Booking Confirmed") && onBookingConfirmed) {
        onBookingConfirmed(data.reply);
      }
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: "bot-err",
          role: "model",
          content: "Offline Heuristic Mode: Routing around Sonapur Tunnel via SH-7 Umrangso-Haflong highland detour."
        }
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const quickPrompts = [
    "Book cold-chain insulin dispatch",
    "Check NH-6 Sonapur status",
    "Reserve 20 oxygen cylinders for Silchar",
    "Suggest safe detour"
  ];

  return (
    <div className="fixed inset-y-0 right-0 z-50 w-full max-w-md bg-earth-950/95 border-l border-earth-700/60 shadow-2xl backdrop-blur-md flex flex-col animate-slideLeft">
      {/* Chatbot Header */}
      <div className="p-4 bg-earth-900 border-b border-earth-800 flex items-center justify-between text-earth-100">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-full bg-luxe-copper/20 border border-luxe-copper/40 flex items-center justify-center text-luxe-copper">
            <Bot className="w-4 h-4" />
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <h3 className="text-xs font-bold uppercase tracking-wider">Tactical Dispatch AI</h3>
              <Sparkles className="w-3 h-3 text-amber-400" />
            </div>
            <span className="text-[11px] text-earth-400">Gemini 3.5 Flash • Context Memory</span>
          </div>
        </div>

        <button
          onClick={onClose}
          className="p-1.5 rounded-lg text-earth-400 hover:text-earth-100 hover:bg-earth-800 transition-colors"
        >
          <X className="w-4 h-4" />
        </button>
      </div>

      {/* Message History */}
      <div className="flex-1 p-4 overflow-y-auto space-y-3">
        {messages.map((m) => (
          <div
            key={m.id}
            className={`flex gap-2.5 ${m.role === "user" ? "justify-end" : "justify-start"}`}
          >
            {m.role === "model" && (
              <div className="w-6 h-6 rounded-full bg-earth-800 border border-earth-700 flex items-center justify-center text-luxe-copper flex-shrink-0 mt-1">
                <Bot className="w-3.5 h-3.5" />
              </div>
            )}
            <div
              className={`p-3 rounded-2xl text-xs max-w-[85%] whitespace-pre-wrap leading-relaxed shadow-md ${
                m.role === "user"
                  ? "bg-luxe-copper text-white rounded-br-none"
                  : "bg-earth-900 border border-earth-800 text-earth-100 rounded-bl-none"
              }`}
            >
              {m.content}
            </div>
            {m.role === "user" && (
              <div className="w-6 h-6 rounded-full bg-luxe-copper/30 flex items-center justify-center text-white flex-shrink-0 mt-1">
                <User className="w-3.5 h-3.5" />
              </div>
            )}
          </div>
        ))}

        {isLoading && (
          <div className="flex gap-2.5 justify-start items-center text-xs text-earth-400 p-2">
            <RefreshCw className="w-3.5 h-3.5 animate-spin text-luxe-copper" />
            <span>Reasoning across NER road graph...</span>
          </div>
        )}
        <div ref={scrollRef} />
      </div>

      {/* Suggested Quick Prompt Chips */}
      <div className="p-2 border-t border-earth-800/80 flex gap-2 overflow-x-auto bg-earth-900/50">
        {quickPrompts.map((prompt, i) => (
          <button
            key={i}
            onClick={() => handleSend(prompt)}
            className="px-2.5 py-1 rounded-full bg-earth-800 hover:bg-earth-700 border border-earth-700 text-[11px] text-earth-200 whitespace-nowrap transition-colors"
          >
            {prompt}
          </button>
        ))}
      </div>

      {/* Input Bar */}
      <div className="p-3 bg-earth-900 border-t border-earth-800 flex items-center gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
          placeholder="Ask route question or book convoy..."
          className="flex-1 bg-earth-950 border border-earth-700 rounded-xl px-3 py-2 text-xs text-earth-100 placeholder-earth-500 focus:outline-none focus:border-luxe-copper"
        />
        <button
          onClick={() => handleSend()}
          disabled={!input.trim() || isLoading}
          className="p-2 bg-luxe-copper hover:bg-earth-600 disabled:opacity-40 text-white rounded-xl transition-all shadow-md"
        >
          <Send className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
