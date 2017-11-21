package io.micrometer.newrelic;

import com.newrelic.api.agent.NewRelic;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;

public class NewRelicCounter extends CumulativeCounter {
    private final MeterRegistry.Config config;
    private final HierarchicalNameMapper nameMapper = NewRelicHierarchicalNameMapper.INSTANCE;

    public NewRelicCounter(Id id, MeterRegistry.Config config) {
        super(id);
        this.config = config;
    }

    @Override
    public void increment(double amount) {
        super.increment(amount);

        // FIXME should we use recordMetric instead?
        String name = nameMapper.toHierarchicalName(getId(), config.namingConvention()) + "Count";
        NewRelic.incrementCounter(name, (int) amount);
        NewRelic.getAgent().getMetricAggregator().incrementCounter(name, (int) amount);
    }
}
