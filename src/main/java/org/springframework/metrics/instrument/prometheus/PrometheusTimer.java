/**
 * Copyright 2017 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.metrics.instrument.prometheus;

import io.prometheus.client.Summary;
import org.springframework.metrics.instrument.Clock;
import org.springframework.metrics.instrument.internal.AbstractTimer;

import java.util.concurrent.TimeUnit;

public class PrometheusTimer extends AbstractTimer {
    private Summary.Child summary;

    public PrometheusTimer(String name, Summary.Child summary, Clock clock) {
        super(name, clock);
        this.summary = summary;
    }

    @Override
    public void record(long amount, TimeUnit unit) {
        if (amount >= 0) {
            final double nanos = TimeUnit.NANOSECONDS.convert(amount, unit);

            // Prometheus prefers to receive everything in base units, i.e. seconds
            summary.observe(nanos / 10e8);
        }
    }

    @Override
    public long count() {
        return (long) summary.get().count;
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return secondsToUnit(summary.get().sum, unit);
    }
}
