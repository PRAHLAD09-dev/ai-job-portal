package com.prahlad.aijobportal.jobservice.job.service.impl;

import com.prahlad.aijobportal.jobservice.job.dto.request.JobAlertRequest;
import com.prahlad.aijobportal.jobservice.job.dto.response.JobAlertResponse;
import com.prahlad.aijobportal.jobservice.job.entity.JobAlert;
import com.prahlad.aijobportal.jobservice.job.enums.JobAlertFrequency;
import com.prahlad.aijobportal.jobservice.job.exception.JobAlertNotFoundException;
import com.prahlad.aijobportal.jobservice.job.repository.JobAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobAlertServiceImplTest {

    @Mock private JobAlertRepository jobAlertRepository;

    @InjectMocks
    private JobAlertServiceImpl jobAlertService;

    private UUID userId;
    private JobAlertRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        request = new JobAlertRequest("java developer", null, null, null, null, "Bangalore", JobAlertFrequency.DAILY);
    }

    @Test
    void createAlert_savesAndReturnsResponse() {
        ArgumentCaptor<JobAlert> captor = ArgumentCaptor.forClass(JobAlert.class);
        JobAlert saved = JobAlert.builder()
                .userId(userId).keyword(request.keyword()).frequency(request.frequency())
                .city(request.city()).active(true).build();
        saved.setId(UUID.randomUUID());
        when(jobAlertRepository.save(any(JobAlert.class))).thenReturn(saved);

        JobAlertResponse response = jobAlertService.createAlert(userId, request);

        verify(jobAlertRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getKeyword()).isEqualTo("java developer");
        assertThat(response.keyword()).isEqualTo("java developer");
        assertThat(response.active()).isTrue();
    }

    @Test
    void updateAlert_throws_whenNotFound() {
        UUID alertId = UUID.randomUUID();
        when(jobAlertRepository.findByIdAndUserId(alertId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobAlertService.updateAlert(userId, alertId, request))
                .isInstanceOf(JobAlertNotFoundException.class);
    }

    @Test
    void deleteAlert_deletes_whenFound() {
        UUID alertId = UUID.randomUUID();
        JobAlert alert = JobAlert.builder().userId(userId).build();
        when(jobAlertRepository.findByIdAndUserId(alertId, userId)).thenReturn(Optional.of(alert));

        jobAlertService.deleteAlert(userId, alertId);

        verify(jobAlertRepository).delete(alert);
    }

    @Test
    void getMyAlerts_returnsMappedList() {
        JobAlert alert = JobAlert.builder().userId(userId).keyword("test").frequency(JobAlertFrequency.WEEKLY).active(true).build();
        alert.setId(UUID.randomUUID());
        when(jobAlertRepository.findByUserId(userId)).thenReturn(List.of(alert));

        List<JobAlertResponse> result = jobAlertService.getMyAlerts(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keyword()).isEqualTo("test");
    }
}
