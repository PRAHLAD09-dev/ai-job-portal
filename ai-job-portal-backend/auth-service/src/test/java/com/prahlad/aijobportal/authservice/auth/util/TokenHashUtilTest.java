package com.prahlad.aijobportal.authservice.auth.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenHashUtilTest {

    @Test
    void generateRawToken_producesNonNullUrlSafeValue() {
        String token = TokenHashUtil.generateRawToken();

        assertThat(token).isNotBlank();
        assertThat(token).doesNotContain("+", "/");
    }

    @Test
    void generateRawToken_producesUniqueValuesAcrossCalls() {
        String first = TokenHashUtil.generateRawToken();
        String second = TokenHashUtil.generateRawToken();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void hash_isDeterministicForSameInput() {
        String raw = "sample-raw-token-value";

        String hash1 = TokenHashUtil.hash(raw);
        String hash2 = TokenHashUtil.hash(raw);

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void hash_producesDifferentHashesForDifferentInputs() {
        String hash1 = TokenHashUtil.hash("token-a");
        String hash2 = TokenHashUtil.hash("token-b");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void hash_producesSixtyFourCharacterHexString() {
        String hash = TokenHashUtil.hash("any-value");

        assertThat(hash).hasSize(64);
        assertThat(hash).matches("[0-9a-f]+");
    }
}
