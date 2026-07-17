package com.prahlad.aijobportal.common.email;

/**
 * DAY12 "Email Templates": a single, table-based responsive HTML shell
 * shared by every transactional e-mail this platform sends, from both
 * Auth Service (registration, verification, password reset) and
 * Notification Service (application lifecycle, AI results). Kept in
 * {@code common} — a plain, framework-agnostic string builder, so it
 * carries no JPA/Spring dependency and is safe for every service to use
 * — so the two services never drift into two different-looking "brands"
 * of e-mail.
 *
 * Table-based layout + inline styles (rather than flexbox/grid or a
 * {@code <style>} block for anything structural) is deliberate: many
 * e-mail clients (Outlook desktop in particular, still Word-rendering-
 * engine based) only reliably support that subset of HTML/CSS. A
 * {@code <style>} block is used only for the few things that degrade
 * gracefully if stripped (body background, font stack).
 *
 * Colors match 01_UI_DESIGN.md's palette (Indigo primary, Slate text) so
 * e-mail and product UI feel like the same brand.
 */
public final class EmailLayout {

    private static final String PRIMARY = "#4f46e5";
    private static final String PRIMARY_DARK = "#4338ca";
    private static final String TEXT = "#1e293b";
    private static final String MUTED = "#64748b";
    private static final String BORDER = "#e2e8f0";
    private static final String BACKGROUND = "#f1f5f9";
    private static final String SURFACE = "#ffffff";

    private EmailLayout() {
    }

    /** Wraps {@code bodyHtml} (plain content, e.g. a few {@code <p>} tags) in the full branded shell. */
    public static String render(String preheader, String bodyHtml) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <title>AI Job Portal</title>
                <style>
                  body { margin: 0; padding: 0; background-color: %s; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
                  a { color: %s; }
                  @media only screen and (max-width: 600px) {
                    .email-container { width: 100%% !important; }
                    .email-padding { padding-left: 20px !important; padding-right: 20px !important; }
                  }
                </style>
                </head>
                <body style="margin:0; padding:0; background-color:%s;">
                  <!-- Preheader: shown as inbox preview text, hidden in the body -->
                  <div style="display:none; max-height:0; overflow:hidden; opacity:0; font-size:1px; line-height:1px; color:%s;">
                    %s
                  </div>
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:%s;">
                    <tr>
                      <td align="center" style="padding: 32px 16px;">
                        <table role="presentation" class="email-container" width="600" cellpadding="0" cellspacing="0" style="width:600px; max-width:100%%;">
                          <tr>
                            <td align="center" style="padding-bottom: 24px;">
                              <span style="font-size:20px; font-weight:700; color:%s; letter-spacing:-0.02em;">AI Job Portal</span>
                            </td>
                          </tr>
                          <tr>
                            <td class="email-padding" style="background-color:%s; border:1px solid %s; border-radius:12px; padding:32px 40px;">
                              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="font-size:15px; line-height:1.6; color:%s;">
                                    %s
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding-top: 24px;">
                              <p style="margin:0; font-size:12px; line-height:1.6; color:%s;">
                                AI Job Portal &middot; This is an automated message, please don't reply directly to this e-mail.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                BACKGROUND, PRIMARY,
                BACKGROUND,
                MUTED, escapePreheader(preheader),
                BACKGROUND,
                PRIMARY_DARK,
                SURFACE, BORDER,
                TEXT, bodyHtml,
                MUTED
        );
    }

    /** A branded, inline-styled call-to-action button/link — splice its output directly into a body fragment. */
    public static String button(String label, String url) {
        return """
                <table role="presentation" cellpadding="0" cellspacing="0" style="margin: 20px 0;">
                  <tr>
                    <td align="center" style="border-radius:8px; background-color:%s;">
                      <a href="%s" target="_blank" style="display:inline-block; padding:12px 28px; font-size:14px; font-weight:600; color:#ffffff; text-decoration:none; border-radius:8px;">%s</a>
                    </td>
                  </tr>
                </table>
                """.formatted(PRIMARY, url, label);
    }

    /** A small muted note, e.g. for "link expires in..." or "if you didn't request this...". */
    public static String note(String text) {
        return "<p style=\"margin:16px 0 0; font-size:13px; line-height:1.6; color:%s;\">%s</p>".formatted(MUTED, text);
    }

    private static String escapePreheader(String preheader) {
        return preheader == null ? "" : preheader.replace("<", "&lt;").replace(">", "&gt;");
    }
}
