package com.prahlad.aijobportal.authservice.admin.dto.response;

import java.time.LocalDate;

/** DAY12 "Admin Dashboard: User Growth" — one day's real signup count. */
public record UserGrowthPointResponse(
        LocalDate date,
        long signupCount
) {
}
