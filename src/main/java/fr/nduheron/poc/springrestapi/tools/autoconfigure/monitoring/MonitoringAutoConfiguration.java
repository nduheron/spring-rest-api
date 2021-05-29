package fr.nduheron.poc.springrestapi.tools.autoconfigure.monitoring;

import fr.nduheron.poc.springrestapi.tools.actuator.monitoring.LoggingMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ConditionalOnClass(MeterRegistry.class)
public class MonitoringAutoConfiguration {

    public static final String BACKGROUND_TASK_SCHEDULER = "backgroundTaskScheduler";

    @Bean(name = BACKGROUND_TASK_SCHEDULER)
    @ConditionalOnMissingBean(name = BACKGROUND_TASK_SCHEDULER)
    public TaskScheduler backgroundTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix(BACKGROUND_TASK_SCHEDULER);
        return threadPoolTaskScheduler;
    }

    @Bean
    @ConditionalOnBean(name = BACKGROUND_TASK_SCHEDULER)
    @ConditionalOnMissingBean
    MeterRegistry loggingMeterRegistry(@Value("${monitoring.log.delay:300}") long delay) {
        return new LoggingMeterRegistry(backgroundTaskScheduler(), delay);
    }


}
