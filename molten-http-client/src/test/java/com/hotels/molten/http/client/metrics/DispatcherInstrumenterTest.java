/*
 * Copyright (c) 2020 Expedia, Inc.
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

package com.hotels.molten.http.client.metrics;

import static com.hotels.molten.core.metrics.MetricsSupport.GRAPHITE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import okhttp3.Dispatcher;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.hotels.molten.core.metrics.MoltenMetrics;

/**
 * Unit test for {@link DispatcherInstrumenter}.
 */
@Listeners(MockitoTestNGListener.class)
public class DispatcherInstrumenterTest {
    private static final String CLIENT_ID = "clientId";
    private DispatcherInstrumenter instrumenter;
    private MeterRegistry meterRegistry;
    @Mock
    private Dispatcher dispatcher;

    @BeforeMethod
    public void initContext() {
        meterRegistry = new SimpleMeterRegistry();
        instrumenter = new DispatcherInstrumenter(meterRegistry, CLIENT_ID);
        when(dispatcher.queuedCallsCount()).thenReturn(3);
        when(dispatcher.runningCallsCount()).thenReturn(2);
        when(dispatcher.getMaxRequests()).thenReturn(4);
    }

    @Test
    public void should_register_hierarchical_metrics() {
        // Given
        MoltenMetrics.setDimensionalMetricsEnabled(false);
        // When
        instrumenter.instrument(dispatcher);
        // Then
        assertThat(meterRegistry.get("client.clientId.http-dispatcher.queued").gauge().value()).isEqualTo(3D);
        assertThat(meterRegistry.get("client.clientId.http-dispatcher.running").gauge().value()).isEqualTo(2D);
        assertThat(meterRegistry.get("client.clientId.http-dispatcher.max").gauge().value()).isEqualTo(4D);
    }

    @Test
    public void should_register_dimensional_metrics() {
        // Given
        MoltenMetrics.setDimensionalMetricsEnabled(true);
        MoltenMetrics.setGraphiteIdMetricsLabelEnabled(true);
        // When
        instrumenter.instrument(dispatcher);
        // Then
        assertThat(meterRegistry.get("http_client_dispatcher_queued_calls")
            .tag("client", CLIENT_ID)
            .tag(GRAPHITE_ID, "client.clientId.http-dispatcher.queued")
            .gauge().value()).isEqualTo(3D);
        assertThat(meterRegistry.get("http_client_dispatcher_running_calls")
            .tag("client", CLIENT_ID)
            .tag(GRAPHITE_ID, "client.clientId.http-dispatcher.running")
            .gauge().value()).isEqualTo(2D);
        assertThat(meterRegistry.get("http_client_dispatcher_max_calls")
            .tag("client", CLIENT_ID)
            .tag(GRAPHITE_ID, "client.clientId.http-dispatcher.max")
            .gauge().value()).isEqualTo(4D);
    }
}
