package com.prahlad.aijobportal.aiservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * WebClient used exclusively by the Gemini client (com.prahlad.aijobportal.aiservice.gemini)
 * for outbound calls to the external Generative Language API. Kept
 * separate from OpenFeign, which is used only for internal
 * service-to-service calls per DECISIONS.md.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient geminiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    /**
     * WebClient used exclusively by {@code ResumeTextExtractionServiceImpl}
     * to download a candidate's resume file from its Cloudinary URL before
     * running it through PDFBox. The in-memory buffer cap here (12 MB, a
     * margin above {@code app.resume-extraction.max-file-size-bytes}'s
     * default 10 MB) is what actually stops an oversized/runaway download —
     * once the response body exceeds it, WebClient fails the request with a
     * {@code DataBufferLimitException} rather than buffering an unbounded
     * file into memory.
     */
    @Bean
    public WebClient resumeFileWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(15));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(12 * 1024 * 1024))
                .build();
    }
}
