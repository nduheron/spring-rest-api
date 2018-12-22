package fr.nduheron.poc.springrestapi.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfiguration {

    @Bean
    MeterRegistry meterRegistry() {
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();
        compositeRegistry.add(new SimpleMeterRegistry());
        compositeRegistry.add(new LoggingMeterRegistry());
        return compositeRegistry;
    }
}
