package fr.nduheron.poc.springrestapi.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MonitoringConfiguration {

    @Bean
    MeterRegistry meterRegistry() {
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();

        compositeRegistry.add(new SimpleMeterRegistry());
        compositeRegistry.add(new LoggingMeterRegistry(new LoggingRegistryConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public Duration step() {
                String v = get(prefix() + ".step");
                return v == null ? Duration.ofMinutes(5) : Duration.parse(v);
            }
        }, Clock.SYSTEM));

        return compositeRegistry;
    }
}
