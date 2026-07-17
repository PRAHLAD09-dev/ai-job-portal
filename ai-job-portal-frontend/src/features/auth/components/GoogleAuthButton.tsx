import { useEffect, useRef } from "react";
import { Loader2 } from "lucide-react";
import { useTheme } from "@/hooks/useTheme";
import { useGoogleIdentityScript } from "@/hooks/useGoogleIdentityScript";
import { useGoogleLogin } from "@/features/auth/hooks/useAuthMutations";
import { ENV } from "@/constants/env";
import type { GoogleAuthRequest } from "@/features/auth/types";

interface GoogleAuthButtonProps {
  /** Only used the first time this Google account signs in (new-account role). Ignored on later logins. */
  role: GoogleAuthRequest["role"];
}

/**
 * "Continue with Google" — Google Identity Services renders its own
 * branded button into `containerRef`; on success we hand the ID token to
 * POST /auth/oauth/google (see AuthController#loginWithGoogle).
 */
export function GoogleAuthButton({ role }: GoogleAuthButtonProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const isScriptReady = useGoogleIdentityScript();
  const googleLogin = useGoogleLogin();
  const { resolvedTheme } = useTheme();
  const roleRef = useRef(role);
  roleRef.current = role;

  useEffect(() => {
    if (!isScriptReady || !containerRef.current || !window.google || !ENV.GOOGLE_CLIENT_ID) return;

    window.google.accounts.id.initialize({
      client_id: ENV.GOOGLE_CLIENT_ID,
      ux_mode: "popup",
      callback: (response) => {
        googleLogin.mutate({ idToken: response.credential, role: roleRef.current });
      },
    });

    containerRef.current.innerHTML = "";
    window.google.accounts.id.renderButton(containerRef.current, {
      type: "standard",
      theme: resolvedTheme === "dark" ? "filled_black" : "outline",
      size: "large",
      text: "continue_with",
      shape: "rectangular",
      logo_alignment: "left",
      width: 360,
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isScriptReady, resolvedTheme]);

  if (!ENV.GOOGLE_CLIENT_ID) {
    return null; // Not configured for this environment — quietly omit rather than showing a broken button.
  }

  return (
    <div className="flex w-full flex-col items-center gap-2">
      <div ref={containerRef} className="flex w-full justify-center [&>div]:!w-full" />
      {(!isScriptReady || googleLogin.isPending) && (
        <div className="flex items-center gap-1.5 text-xs text-[hsl(var(--muted))]">
          <Loader2 className="h-3 w-3 animate-spin" />
          {googleLogin.isPending ? "Signing in…" : "Loading Google Sign-In…"}
        </div>
      )}
    </div>
  );
}
