package io.micrometer.newrelic;

import io.micrometer.core.instrument.config.MeterRegistryConfig;

public interface NewRelicConfig extends MeterRegistryConfig {
    @Override
    default String prefix() {
        return "newrelic";
    }

    default String licenseKey() {
        String v = get(prefix() + ".licenseKey");
        if(v == null)
            throw new IllegalStateException(prefix() + ".licenseKey must be set to report metrics to New Relic");
        return v;
    }

    default String appName() {
        return get(prefix() + ".appName");
    }

    default String proxyHost() {
        return get(prefix() + ".proxyHost");
    }

    default Integer proxyPort() {
        String v = get(prefix() + ".proxyPort");
        return v == null ? null : Integer.parseInt(v);
    }

    default String proxyUser() {
        return get(prefix() + ".proxyUser");
    }

    default String proxyPassword() {
        return get(prefix() + ".proxyPassword");
    }

    // FIXME -- OTHER PROPERTIES TO SUPPORT:
    // see: https://docs.newrelic.com/docs/agents/java-agent/configuration/java-agent-configuration-config-file
    // newrelic.config:
    //      send_data_on_exit
    //      high_security
}
