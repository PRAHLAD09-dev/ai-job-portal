package com.prahlad.aijobportal.recruiterservice.company.dto.request;

import com.prahlad.aijobportal.recruiterservice.company.enums.CompanySize;
import com.prahlad.aijobportal.recruiterservice.company.enums.Industry;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Year;

public record UpdateCompanyRequest(

        @NotBlank(message = "Company name is required")
        @Size(max = 200, message = "Company name must not exceed 200 characters")
        String name,

        @Size(max = 4000, message = "Description must not exceed 4000 characters")
        String description,

        @NotNull(message = "Industry is required")
        Industry industry,

        @NotNull(message = "Company size is required")
        CompanySize companySize,

        @Min(value = 1800, message = "Founded year must be realistic")
        Integer foundedYear,

        @Size(max = 500, message = "Website URL must not exceed 500 characters")
        String websiteUrl,

        @Email(message = "Email must be a valid email address")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Pattern(regexp = "^\\+?[0-9\\-\\s()]{7,20}$", message = "Phone number must be a valid phone number")
        String phoneNumber
) {
    @AssertTrue(message = "Founded year cannot be in the future")
    public boolean isFoundedYearValid() {
        return foundedYear == null || foundedYear <= Year.now().getValue();
    }
}
