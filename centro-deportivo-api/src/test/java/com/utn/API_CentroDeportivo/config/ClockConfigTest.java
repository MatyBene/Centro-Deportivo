package com.utn.API_CentroDeportivo.config;

import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClockConfigTest {

    @Test
    void clock_ShouldReturnSystemDefaultZone() {
        // Arrange
        ClockConfig clockConfig = new ClockConfig();

        // Act
        Clock clock = clockConfig.clock();

        // Assert
        assertEquals(Clock.systemDefaultZone().getZone(), clock.getZone());
    }
}
