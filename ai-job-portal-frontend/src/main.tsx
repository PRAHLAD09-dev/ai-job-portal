import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "@/App";
import "@/styles/globals.css";
// Loaded globally (not lazily inside CompanyLocationMap) so Leaflet's own
// .leaflet-container { overflow: hidden } / pane sizing rules are always
// present before any map mounts — a per-route CSS chunk can otherwise load
// after the map's first paint and let its tile pane render unclipped.
import "leaflet/dist/leaflet.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
