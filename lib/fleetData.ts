export interface FleetVehicle {
  id: string;
  registration: string;
  status: "Moving" | "Idle" | "Delayed" | "Broken Down" | "Loading" | "Unloading";
  cargo: string;
  vehicleType: string;
  driverName: string;
  driverPhone: string;
  speedKmh?: number;
  timeAgo: string;
  destination: {
    name: string;
    lat: number;
    lng: number;
  };
  telemetry: {
    fuelPercent: number;
    batteryPercent: number;
    cargoTempCelsius?: number;
    lastGpsUpdate: string;
  };
  currentLocation: {
    lat: number;
    lng: number;
    landmark: string;
  };
}

export const FLEET_VEHICLES: FleetVehicle[] = [
  {
    id: "V-01",
    registration: "AR-02-CD-5678",
    status: "Delayed",
    cargo: "Fuel",
    vehicleType: "tanker",
    driverName: "Tashi Norbu",
    driverPhone: "+91 943 601 2345",
    timeAgo: "2d ago",
    destination: {
      name: "Tawang",
      lat: 27.300,
      lng: 93.100
    },
    telemetry: {
      fuelPercent: 68,
      batteryPercent: 92,
      lastGpsUpdate: "2 mins ago"
    },
    currentLocation: {
      lat: 26.850,
      lng: 92.650,
      landmark: "Near Bhalukpong Border Checkpost"
    }
  },
  {
    id: "V-02",
    registration: "AS-01-AB-1234",
    status: "Moving",
    speedKmh: 45,
    cargo: "Food Supplies",
    vehicleType: "truck",
    driverName: "Biren Das",
    driverPhone: "+91 986 402 3456",
    timeAgo: "2d ago",
    destination: {
      name: "Itanagar",
      lat: 26.500,
      lng: 91.800
    },
    telemetry: {
      fuelPercent: 84,
      batteryPercent: 96,
      lastGpsUpdate: "Just now"
    },
    currentLocation: {
      lat: 26.320,
      lng: 92.150,
      landmark: "Kharupetia Highway Bypass"
    }
  },
  {
    id: "V-03",
    registration: "AS-07-MN-5678",
    status: "Loading",
    cargo: "Food Supplies",
    vehicleType: "truck",
    driverName: "Anil Gogoi",
    driverPhone: "+91 970 603 4567",
    timeAgo: "2d ago",
    destination: {
      name: "Dibrugarh",
      lat: 26.145,
      lng: 91.736
    },
    telemetry: {
      fuelPercent: 95,
      batteryPercent: 100,
      lastGpsUpdate: "10 mins ago"
    },
    currentLocation: {
      lat: 26.145,
      lng: 91.736,
      landmark: "Guwahati Central Staging Terminal"
    }
  },
  {
    id: "V-04",
    registration: "ML-05-EF-9012",
    status: "Moving",
    speedKmh: 38,
    cargo: "Cryogenic Oxygen Cylinders",
    vehicleType: "heavy transport",
    driverName: "P. Marak",
    driverPhone: "+91 943 610 8899",
    timeAgo: "1d ago",
    destination: {
      name: "Shillong Civil Hospital",
      lat: 25.578,
      lng: 91.893
    },
    telemetry: {
      fuelPercent: 72,
      batteryPercent: 88,
      lastGpsUpdate: "Just now"
    },
    currentLocation: {
      lat: 25.820,
      lng: 91.850,
      landmark: "Nongpoh Incline Corridor"
    }
  },
  {
    id: "V-05",
    registration: "AS-11-JK-3456",
    status: "Moving",
    speedKmh: 52,
    cargo: "Cold-Chain Insulin & Vaccines",
    vehicleType: "refrigerated 4x4",
    driverName: "R. Kalita",
    driverPhone: "+91 985 401 7766",
    timeAgo: "3h ago",
    destination: {
      name: "Silchar Relief Base",
      lat: 24.833,
      lng: 92.778
    },
    telemetry: {
      fuelPercent: 80,
      batteryPercent: 94,
      cargoTempCelsius: 3.4,
      lastGpsUpdate: "Just now"
    },
    currentLocation: {
      lat: 25.510,
      lng: 92.740,
      landmark: "Umrangso Dima Hasao Plateau"
    }
  },
  {
    id: "V-06",
    registration: "MZ-01-PQ-7890",
    status: "Broken Down",
    cargo: "Medical Triage Equipment",
    vehicleType: "4x4 utility",
    driverName: "L. Sailo",
    driverPhone: "+91 943 614 3322",
    timeAgo: "5h ago",
    destination: {
      name: "Aizawl Civil Hospital",
      lat: 23.730,
      lng: 92.717
    },
    telemetry: {
      fuelPercent: 42,
      batteryPercent: 35,
      lastGpsUpdate: "42 mins ago"
    },
    currentLocation: {
      lat: 24.150,
      lng: 92.720,
      landmark: "Vairengte Highland Pass"
    }
  },
  {
    id: "V-07",
    registration: "TR-01-XY-1234",
    status: "Idle",
    cargo: "Ready-to-Eat Emergency Rations",
    vehicleType: "medium truck",
    driverName: "S. Debbarma",
    driverPhone: "+91 943 650 9988",
    timeAgo: "1d ago",
    destination: {
      name: "Agartala Relief Hub",
      lat: 23.831,
      lng: 91.286
    },
    telemetry: {
      fuelPercent: 90,
      batteryPercent: 98,
      lastGpsUpdate: "15 mins ago"
    },
    currentLocation: {
      lat: 23.831,
      lng: 91.286,
      landmark: "Agartala Central Food Depot"
    }
  },
  {
    id: "V-08",
    registration: "NL-07-AB-4321",
    status: "Unloading",
    cargo: "Water Purification Units",
    vehicleType: "heavy truck",
    driverName: "K. Sema",
    driverPhone: "+91 943 600 5544",
    timeAgo: "2d ago",
    destination: {
      name: "Kohima District Hospital",
      lat: 25.675,
      lng: 94.108
    },
    telemetry: {
      fuelPercent: 65,
      batteryPercent: 90,
      lastGpsUpdate: "4 mins ago"
    },
    currentLocation: {
      lat: 25.675,
      lng: 94.108,
      landmark: "Kohima Staging Camp"
    }
  }
];
