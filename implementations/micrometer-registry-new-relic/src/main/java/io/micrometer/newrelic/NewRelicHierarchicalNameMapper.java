package io.micrometer.newrelic;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;

import java.util.stream.Collectors;

public class NewRelicHierarchicalNameMapper implements HierarchicalNameMapper {
    public static final NewRelicHierarchicalNameMapper INSTANCE = new NewRelicHierarchicalNameMapper();

    private NewRelicHierarchicalNameMapper() {}

    @Override
    public String toHierarchicalName(Meter.Id id, NamingConvention convention) {
        return "Custom/" + id.getConventionName(convention) + "/" +
            id.getConventionTags(convention).stream()
                .map(t -> t.getKey() + "/" + t.getValue())
                .collect(Collectors.joining("/"));
    }
}
