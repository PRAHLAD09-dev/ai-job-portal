package com.prahlad.aijobportal.adminservice.dashboard.dto.response;

/** A single labeled value in a distribution/breakdown chart (e.g. one slice of a pie chart, one bar). */
public record ChartDataPoint(String label, long value) {
}
