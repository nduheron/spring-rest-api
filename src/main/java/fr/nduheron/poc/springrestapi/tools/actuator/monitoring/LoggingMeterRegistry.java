package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.internal.DefaultLongTaskTimer;
import io.micrometer.core.instrument.internal.DefaultMeter;
import io.micrometer.core.instrument.step.*;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoggingMeterRegistry extends MeterRegistry {
    private static final Logger log = LoggerFactory.getLogger(LoggingMeterRegistry.class);

    private Duration delay;

    public LoggingMeterRegistry(TaskScheduler taskScheduler, long delayInSeconds) {
        super(Clock.SYSTEM);
        this.delay = Duration.ofSeconds(delayInSeconds);
        taskScheduler.scheduleWithFixedDelay(this::logMetrics, this.delay);
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        return new DefaultGauge<>(id, obj, valueFunction);
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        return new StepCounter(id, clock, delay.toMillis());
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        return new DefaultLongTaskTimer(id, clock);
    }

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        Timer timer = new StepTimer(id, clock, distributionStatisticConfig, pauseDetector, getBaseTimeUnit(),
                delay.toMillis(), false);
        HistogramGauges.registerWithCommonFormat(timer, this);
        return timer;
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        DistributionSummary summary = new StepDistributionSummary(id, clock, distributionStatisticConfig, scale,
                delay.toMillis(), false);
        HistogramGauges.registerWithCommonFormat(summary, this);
        return summary;
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        return new StepFunctionTimer<>(id, clock, delay.toMillis(), obj, countFunction, totalTimeFunction, totalTimeFunctionUnit, getBaseTimeUnit());
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        return new StepFunctionCounter<>(id, clock, delay.toMillis(), obj, countFunction);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        return new DefaultMeter(id, type, measurements);
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.builder()
                .expiry(delay)
                .build()
                .merge(DistributionStatisticConfig.DEFAULT);
    }

    private void logMetrics() {
        getMeters().stream().sorted(Comparator.comparing((Meter m3) -> m3.getId().getType()).thenComparing(m3 -> m3.getId().getName()))
                .forEach((m) -> m.use((gauge) -> {
                    log(m.getId(), String.format("\"value\":%s", DoubleFormat.decimalOrWhole(gauge.value())), m.getId().getBaseUnit());
                }, (counter) -> {
                    double count = counter.count();
                    if (count > 0) {
                        log(m.getId(), String.format("\"count\":%s", count), m.getId().getBaseUnit());
                    }
                }, (timer) -> {
                    HistogramSnapshot snapshot = timer.takeSnapshot();
                    long count = snapshot.count();
                    if (count > 0) {
                        log(m.getId(), String.format("\"count\":%s,\"max\":%s,\"avg\":%s", count, DoubleFormat.decimalOrWhole(snapshot.max(TimeUnit.MILLISECONDS)),
                                DoubleFormat.decimalOrWhole(snapshot.mean(TimeUnit.MILLISECONDS))), "milliseconds");
                    }
                }, (summary) -> {
                    HistogramSnapshot snapshot = summary.takeSnapshot();
                    long count = snapshot.count();
                    if (count > 0) {
                        log(m.getId(), String.format("\"count\":%s,\"max\":%s,\"avg\":%s", count, DoubleFormat.decimalOrWhole(snapshot.max(TimeUnit.MILLISECONDS)),
                                DoubleFormat.decimalOrWhole(snapshot.mean(TimeUnit.MILLISECONDS))), "milliseconds");
                    }
                }, (longTaskTimer) -> {
                    log(m.getId(), String.format("\"active\":%s,\"duration\":%s", longTaskTimer.activeTasks(), DoubleFormat.decimalOrWhole(longTaskTimer.duration(TimeUnit.SECONDS))), "seconds");
                }, (timeGauge) -> {
                    log(m.getId(), String.format("\"value\":%s", DoubleFormat.decimalOrWhole(timeGauge.value(TimeUnit.MILLISECONDS))), "milliseconds");
                }, (counter) -> {
                    double count = counter.count();
                    if (count > 0) {
                        log(m.getId(), String.format("\"count\":%s", count), m.getId().getBaseUnit());
                    }
                }, (timer) -> {
                    double count = timer.count();
                    if (count > 0) {
                        log(m.getId(), String.format("\"count\":%s,\"total\":%s,\"avg\":%s", count, DoubleFormat.decimalOrWhole(timer.totalTime(TimeUnit.MILLISECONDS)),
                                DoubleFormat.decimalOrWhole(timer.mean(TimeUnit.MILLISECONDS))), "milliseconds");
                    }
                }, (meter) -> {
                    log(m.getId(), StreamSupport.stream(meter.measure().spliterator(), false).map(ms ->
                            "\"" + ms.getStatistic().getTagValueRepresentation() + "\":" + DoubleFormat.decimalOrWhole(ms.getValue())).collect(Collectors.joining(",")), m.getId().getBaseUnit());
                }));
    }

    private void log(Meter.Id id, String values, String unit) {
        String tags = id.getTags().stream().map(t -> "\"" + t.getKey() + "\":\"" + t.getValue() + "\"").collect(Collectors.joining(","));
        String name = id.getName();

        log.info(String.format("%s={\"tags\":{%s}, \"values\":{%s}, \"unit\":\"%s\"}", name, tags, values, unit));
    }

}
