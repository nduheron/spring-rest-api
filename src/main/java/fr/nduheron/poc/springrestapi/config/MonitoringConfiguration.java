package fr.nduheron.poc.springrestapi.config;

import fr.nduheron.poc.springrestapi.tools.actuator.monitoring.LoggingMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

@Configuration
public class MonitoringConfiguration {

    @Bean
    MeterRegistry loggingMeterRegistry(@Qualifier("backgroundTaskScheduler") TaskScheduler backgroundTaskScheduler, @Value("${monitoring.log.delay:300}") long delay) {
        return new LoggingMeterRegistry(backgroundTaskScheduler, delay);
    }
}
