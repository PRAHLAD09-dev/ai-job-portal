import { useEffect, useMemo } from "react";
import { MapContainer, Marker, Popup, TileLayer, useMap } from "react-leaflet";
import L from "leaflet";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import { MapPin } from "lucide-react";
import { EmptyState } from "@/components/common/EmptyState";
import { cn } from "@/lib/cn";

// Vite bundles Leaflet's default marker images as hashed URLs, which breaks
// Leaflet's own asset-path detection — point it at the bundled assets instead.
const defaultIcon = L.icon({
  iconUrl: markerIcon,
  iconRetinaUrl: markerIcon2x,
  shadowUrl: markerShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

/**
 * Leaflet measures its container's size once at mount. Inside a Radix
 * Dialog (fade/scale-in transition, or a lazily-rendered tab), that
 * measurement can happen before the container has its final layout size,
 * leaving the map's internal tile-pane sizing wrong — which is what makes
 * tiles paint outside the intended box instead of being clipped to it.
 * Forcing a re-measure shortly after mount (and once more after the
 * dialog's ~150ms transition finishes) fixes it reliably.
 */
function MapSizeFix() {
  const map = useMap();
  useEffect(() => {
    const t1 = setTimeout(() => map.invalidateSize(), 0);
    const t2 = setTimeout(() => map.invalidateSize(), 200);
    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
    };
  }, [map]);
  return null;
}

export interface MapLocation {
  id: string;
  city: string;
  state?: string | null;
  country: string;
  addressLine?: string | null;
  headquarters?: boolean;
  latitude: number | null;
  longitude: number | null;
}

interface PlottedLocation extends MapLocation {
  latitude: number;
  longitude: number;
}

interface CompanyLocationMapProps {
  locations: MapLocation[];
  /** Fixed map height in px. */
  height?: number;
  className?: string;
}

/**
 * DAY07/DAY11 "Company Location Map": renders office locations on a
 * Leaflet map (OpenStreetMap tiles — free, no API key required).
 * Supports zoom, per-location markers, and popups. Only locations with
 * both latitude and longitude are plotted; locations missing
 * coordinates are silently skipped rather than guessed at.
 */
export function CompanyLocationMap({ locations, height = 320, className }: CompanyLocationMapProps) {
  const plottable = useMemo(
    () => locations.filter((l): l is PlottedLocation => l.latitude != null && l.longitude != null),
    [locations],
  );

  const center = useMemo((): [number, number] => {
    const hq = plottable.find((l) => l.headquarters);
    if (hq) return [hq.latitude, hq.longitude];
    if (plottable.length > 0) return [plottable[0].latitude, plottable[0].longitude];
    return [20, 0];
  }, [plottable]);

  if (plottable.length === 0) {
    return (
      <EmptyState
        icon={<MapPin className="h-8 w-8" />}
        title="No map coordinates yet"
        message="Add latitude and longitude to a location to see it plotted on the map."
      />
    );
  }

  return (
    // relative + overflow-hidden + an explicit height are all belt-and-suspenders:
    // even if Leaflet's own .leaflet-container clipping is ever undermined by CSS
    // load order, this wrapper still hard-clips the tile pane to this box.
    <div className={cn("relative w-full overflow-hidden rounded-lg", className)} style={{ height }}>
      <MapContainer
        key={plottable.map((l) => l.id).join(",")}
        center={center}
        zoom={plottable.length > 1 ? 3 : 11}
        scrollWheelZoom={false}
        className="h-full w-full"
        style={{ height: "100%", width: "100%" }}
      >
        <MapSizeFix />
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {plottable.map((location) => (
          <Marker key={location.id} position={[location.latitude, location.longitude]} icon={defaultIcon}>
            <Popup>
              <p className="font-medium">
                {location.city}
                {location.state ? `, ${location.state}` : ""}, {location.country}
                {location.headquarters && <span className="ml-1 text-primary-600">(HQ)</span>}
              </p>
              {location.addressLine && <p className="text-xs text-slate-500">{location.addressLine}</p>}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
