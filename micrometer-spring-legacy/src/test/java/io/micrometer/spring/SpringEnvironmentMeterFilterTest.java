/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.histogram.HistogramConfig;
import io.micrometer.core.instrument.histogram.pause.PauseDetector;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.lang.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
    "management.metrics.filter.enabled=false", // turn off all metrics by default
    "management.metrics.filter.my.timer.enabled=true",
    "management.metrics.filter.my.timer.maximumExpectedValue=PT10S",
    "management.metrics.filter.my.timer.minimumExpectedValue=1ms",
    "management.metrics.filter.my.timer.percentiles=0.5,0.95",
    "management.metrics.filter.my.timer.that.is.misconfigured.enabled=troo",

    "management.metrics.filter.my.summary.enabled=true",
    "management.metrics.filter.my.summary.maximumExpectedValue=100",
})
public class SpringEnvironmentMeterFilterTest {
    private HistogramConfig histogramConfig;

    private MeterRegistry registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock()) {
        @Override
        protected Timer newTimer(@NonNull Meter.Id id, @NonNull HistogramConfig conf, @NonNull PauseDetector pauseDetector) {
            histogramConfig = conf;
            return super.newTimer(id, conf, pauseDetector);
        }

        @Override
        protected DistributionSummary newDistributionSummary(@NonNull Meter.Id id, @NonNull HistogramConfig conf) {
            histogramConfig = conf;
            return super.newDistributionSummary(id, conf);
        }
    };

    @Autowired
    private SpringEnvironmentMeterFilter filter;

    @Before
    public void configureRegistry() {
        registry.config().meterFilter(filter);
    }

    @Test
    public void disable() {
        registry.counter("my.counter");
        assertThat(registry.find("my.counter").counter()).isNull();
    }

    @Test
    public void enable() {
        registry.timer("my.timer");
        registry.mustFind("my.timer").timer();
    }

    @Test
    public void timerHistogramConfig() {
        registry.timer("my.timer");
        assertThat(histogramConfig.getMaximumExpectedValue()).isEqualTo(Duration.ofSeconds(10).toNanos());
        assertThat(histogramConfig.getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(1).toNanos());
        assertThat(histogramConfig.getPercentiles()).containsExactly(0.5, 0.95);
    }

    @Test
    public void summaryHistogramConfig() {
        registry.summary("my.summary");
        assertThat(histogramConfig.getMaximumExpectedValue()).isEqualTo(100);
    }

    @Test
    public void configErrorMessage(){
        assertThatThrownBy(() -> registry.timer("my.timer.that.is.misconfigured"))
            .isInstanceOf(ConfigurationException.class)
            .hasMessage("Invalid configuration for 'my.timer.that.is.misconfigured.enabled' value 'troo' as class java.lang.Boolean");
    }

    @Configuration
    static class FilterTestConfiguration {
        @Bean
        public SpringEnvironmentMeterFilter filter(Environment environment) {
            return new SpringEnvironmentMeterFilter(environment);
        }
    }
}
