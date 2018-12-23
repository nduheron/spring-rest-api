package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.StreamSupport;

@Controller
@RequestMapping("${management.endpoints.web.base-path:/actuator}")
public class ExportMetricsController {
    private static final char SEPARATOR = ';';
    private static final char NEW_LINE = '\n';
    private static final char QUOTE = '"';

    @Autowired
    private MeterRegistry registry;

    @RequestMapping("/metrics.csv")
    public ResponseEntity<String> exportMetrics() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");

        StringBuilder sb = new StringBuilder();
        sb.append(QUOTE).append("TYPE").append(QUOTE).append(SEPARATOR).append(QUOTE).append("SERVICE").append(QUOTE).append(SEPARATOR)
                .append(QUOTE).append("UNIT").append(QUOTE).append(SEPARATOR).append(QUOTE).append("COUNT").append(QUOTE)
                .append(SEPARATOR).append(QUOTE).append("TOTAL_TIME").append(QUOTE).append(SEPARATOR).append(QUOTE)
                .append("MAX").append(QUOTE).append(NEW_LINE);

        registry.getMeters().stream().filter(m -> m.getId().getName().equals("http.server.requests")).forEach(meter -> {
            sb.append(QUOTE).append("http").append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(meter.getId().getTag("method")).append(StringUtils.SPACE)
                    .append(meter.getId().getTag("uri")).append(StringUtils.SPACE)
                    .append(meter.getId().getTag("status")).append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(meter.getId().getBaseUnit()).append(QUOTE).append(SEPARATOR);
            Iterable<Measurement> measure = meter.measure();
            sb.append(QUOTE).append(getStatisticValue(measure, Statistic.COUNT)).append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(getStatisticValue(measure, Statistic.TOTAL_TIME)).append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(getStatisticValue(measure, Statistic.MAX)).append(QUOTE).append(NEW_LINE);
        });

        sb.append(buildRows("services"));
        sb.append(buildRows("mappers"));
        sb.append(buildRows("repositories"));
        sb.append(buildRows("custom"));
        sb.append(buildRows("EntityManager"));

        return new ResponseEntity<>(sb.toString(), responseHeaders, HttpStatus.OK);
    }


    private String buildRows(String service) {
        StringBuilder sb = new StringBuilder();

        registry.getMeters().stream().filter(m -> m.getId().getName().equals(service)).forEach(meter -> {
            sb.append(QUOTE).append(service).append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(meter.getId().getTag("class")).append(".").append(meter.getId().getTag("method"))
                    .append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(meter.getId().getBaseUnit()).append(QUOTE).append(SEPARATOR);
            Iterable<Measurement> measure = meter.measure();
            sb.append(QUOTE).append(getStatisticValue(measure, Statistic.COUNT)).append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(getStatisticValue(measure, Statistic.TOTAL_TIME)).append(QUOTE).append(SEPARATOR);
            sb.append(QUOTE).append(getStatisticValue(measure, Statistic.MAX)).append(QUOTE).append(NEW_LINE);
        });

        return sb.toString();
    }

    @RequestMapping("/caches.csv")
    public ResponseEntity<String> exportCacheMetrics() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");

        StringBuilder sb = new StringBuilder();
        sb.append(QUOTE).append("CACHE").append(QUOTE).append(SEPARATOR).append(QUOTE).append("COUNT").append(QUOTE)
                .append(SEPARATOR).append(QUOTE).append("MEMORY").append(NEW_LINE);

        registry.getMeters().stream().filter(
                m -> m.getId().getName().equals("cache.entry.memory") && "owned".equals(m.getId().getTag("ownership")))
                .forEach(meter -> {
                    sb.append(QUOTE).append(meter.getId().getTag("cache")).append(QUOTE).append(SEPARATOR);
                    Iterable<Measurement> measure = meter.measure();
                    sb.append(QUOTE).append(getNbItemsInCache(meter.getId().getTag("cache"))).append(QUOTE)
                            .append(SEPARATOR);
                    sb.append(QUOTE).append(getStatisticValue(measure, Statistic.VALUE)).append(QUOTE).append(NEW_LINE);
                });

        return new ResponseEntity<>(sb.toString(), responseHeaders, HttpStatus.OK);
    }

    private String getNbItemsInCache(String cacheName) {
        return registry.getMeters().stream()
                .filter(m -> m.getId().getName().equals("cache.size") && m.getId().getTag("cache").equals(cacheName))
                .findFirst().map(meter -> getStatisticValue(meter.measure(), Statistic.VALUE))
                .orElse(StringUtils.EMPTY);
    }

    private String getStatisticValue(Iterable<Measurement> measure, Statistic statistic) {
        return StreamSupport.stream(measure.spliterator(), false).filter(m -> m.getStatistic() == statistic).findFirst()
                .map(m -> String.valueOf(m.getValue())).orElse(StringUtils.EMPTY);
    }

}
