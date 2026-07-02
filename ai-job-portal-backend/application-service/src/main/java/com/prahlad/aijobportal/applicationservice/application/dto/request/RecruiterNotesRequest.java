package com.prahlad.aijobportal.applicationservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruiterNotesRequest(

        @NotBlank(message = "Notes must not be blank")
        @Size(max = 4000, message = "Notes must not exceed 4000 characters")
        String notes
) {
}
