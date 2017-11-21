package io.micrometer.newrelic;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.histogram.HistogramConfig;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * The agent's normal harvest cycle is 60 seconds
 *
 * @author Jon Schneider
 */
public class NewRelicMeterRegistry extends MeterRegistry {
    public NewRelicMeterRegistry(NewRelicConfig config, Clock clock) {
        super(clock);

        config().namingConvention(NamingConvention.upperCamelCase);

        System.setProperty("newrelic.config.license_key", config.licenseKey());

        if(config.proxyHost() != null) {
            System.setProperty("newrelic.config.proxy_host", config.proxyHost());
        }
        if(config.proxyPort() != null) {
            System.setProperty("newrelic.config.proxy_port", config.proxyPort().toString());
        }
        if(config.proxyUser() != null) {
            System.setProperty("newrelic.config.proxy_user", config.proxyUser());

        }
        if(config.proxyPassword() != null) {
            System.setProperty("newrelic.config.proxy_password", config.proxyPassword());
        }
    }

//        NewRelic.incrementCounter("");
//        NewRelic.recordResponseTimeMetric();
//        NewRelic.recordMetric();

    @Override
    protected <T> Gauge newGauge(Meter.Id id, T obj, ToDoubleFunction<T> f) {
        return null;
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        return new NewRelicCounter(id, config());
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        return null;
    }

    @Override
    protected Timer newTimer(Meter.Id id, HistogramConfig histogramConfig) {
        return null;
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, HistogramConfig histogramConfig) {
        return null;
    }

    @Override
    protected void newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return null;
    }
}
