"use client";

import { useState } from "react";
import Sidebar, { NavTabId } from "@/components/Sidebar";
import Header from "@/components/Header";
import DashboardView from "@/components/views/DashboardView";
import GisMapView from "@/components/views/GisMapView";
import VehicleTrackingView from "@/components/views/VehicleTrackingView";
import AlertsCenterView from "@/components/views/AlertsCenterView";
import RouteIntelligenceView from "@/components/views/RouteIntelligenceView";
import DeliveriesView from "@/components/views/DeliveriesView";
import FieldReportsView from "@/components/views/FieldReportsView";
import TacticalChatbot from "@/components/TacticalChatbot";
import LocationShareModal from "@/components/LocationShareModal";
import { ShareContact } from "@/lib/gisData";

export default function HomePage() {
  const [activeTab, setActiveTab] = useState<NavTabId>("route-intelligence");
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [isLocationShareOpen, setIsLocationShareOpen] = useState(false);

  // Active Location Sharing state
  const [activeShareSession, setActiveShareSession] = useState<{
    contactName: string;
    expiresAt: number;
    isBatterySaver: boolean;
  } | null>(null);

  const handleRefreshData = () => {
    setIsRefreshing(true);
    setTimeout(() => {
      setIsRefreshing(false);
    }, 1200);
  };

  const handleStartShare = (contact: ShareContact, durationMins: number, isBatterySaver: boolean) => {
    setActiveShareSession({
      contactName: contact.name,
      expiresAt: Date.now() + durationMins * 60 * 1000,
      isBatterySaver
    });
  };

  const handleStopShare = () => {
    setActiveShareSession(null);
  };

  const getTitle = () => {
    switch (activeTab) {
      case "dashboard":
        return "Dashboard";
      case "gis-map":
        return "GIS Map";
      case "vehicle-tracking":
        return "Vehicle Tracking";
      case "alerts-center":
        return "Alerts Center";
      case "route-intelligence":
        return "Route Intelligence";
      case "deliveries":
        return "Deliveries";
      case "field-reports":
        return "Field Reports";
    }
  };

  return (
    <div className="flex min-h-screen bg-slate-50 dark:bg-[#070E1A] text-slate-900 dark:text-slate-100 font-sans transition-colors">
      {/* Dark Navy Sidebar (Matching Screenshot 1) */}
      <Sidebar
        activeTab={activeTab}
        onSelectTab={(tab) => setActiveTab(tab)}
        unacknowledgedAlertsCount={2}
        onRefreshData={handleRefreshData}
        isRefreshing={isRefreshing}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-y-auto">
        {/* Top Header (Matching Screenshot 2) */}
        <Header
          title={getTitle()}
          onOpenChat={() => setIsChatOpen(true)}
          onOpenLocationShare={() => setIsLocationShareOpen(true)}
          isSharingLocation={!!activeShareSession}
        />

        {/* Dynamic View Component */}
        <main className="flex-1">
          {activeTab === "dashboard" && (
            <DashboardView
              onNavigateToGisMap={() => setActiveTab("gis-map")}
              onNavigateToRouteIntelligence={() => setActiveTab("route-intelligence")}
              onNavigateToVehicles={() => setActiveTab("vehicle-tracking")}
              onNavigateToAlerts={() => setActiveTab("alerts-center")}
            />
          )}

          {activeTab === "gis-map" && <GisMapView />}

          {activeTab === "vehicle-tracking" && <VehicleTrackingView />}

          {activeTab === "alerts-center" && <AlertsCenterView />}

          {activeTab === "route-intelligence" && <RouteIntelligenceView />}

          {activeTab === "deliveries" && (
            <DeliveriesView onOpenBookingAssistant={() => setIsChatOpen(true)} />
          )}

          {activeTab === "field-reports" && <FieldReportsView />}
        </main>
      </div>

      {/* Global Tactical Chatbot Assistant (Gemini 3.5 Flash) */}
      <TacticalChatbot
        isOpen={isChatOpen}
        onClose={() => setIsChatOpen(false)}
      />

      {/* Location Sharing Dialog */}
      <LocationShareModal
        isOpen={isLocationShareOpen}
        onClose={() => setIsLocationShareOpen(false)}
        telemetry={{ lat: 25.42, lng: 92.18 }}
        activeSession={activeShareSession}
        onStartShare={handleStartShare}
        onStopShare={handleStopShare}
      />
    </div>
  );
}
