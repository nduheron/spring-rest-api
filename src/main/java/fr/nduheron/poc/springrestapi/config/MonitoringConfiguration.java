package fr.nduheron.poc.springrestapi.config;

import fr.nduheron.poc.springrestapi.tools.actuator.monitoring.LoggingMeterRegistry;
import fr.nduheron.poc.springrestapi.tools.actuator.monitoring.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.Aspects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

@Configuration
public class MonitoringConfiguration {

    @Bean
    MeterRegistry loggingMeterRegistry(@Qualifier("backgroundTaskScheduler") TaskScheduler backgroundTaskScheduler, @Value("${monitoring.log.delay:30}") long delay) {
        return new LoggingMeterRegistry(backgroundTaskScheduler, delay);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        // This will barf at runtime if the weaver isn't working (probably a
        // good thing)
        TimedAspect timedAspect = Aspects.aspectOf(TimedAspect.class);
        timedAspect.setRegistry(meterRegistry);
        return timedAspect;
    }
}
