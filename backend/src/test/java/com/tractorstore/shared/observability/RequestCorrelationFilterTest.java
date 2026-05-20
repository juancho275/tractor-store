package com.tractorstore.shared.observability;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("RequestCorrelationFilter")
class RequestCorrelationFilterTest {

    private SimpleMeterRegistry meterRegistry;
    private RequestCorrelationFilter filter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        filter = new RequestCorrelationFilter(meterRegistry);
        MDC.clear();
    }

    @Test
    @DisplayName("sets X-Trace-Id header and delegates to filter chain")
    void setsTraceIdHeaderAndDelegates() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/catalog/products");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getHeader("X-Trace-Id")).isNotNull().hasSize(16);
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("clears MDC after request completes")
    void clearsMdcAfterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/catalog/products");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(MDC.get("traceId")).isNull();
    }

    @Test
    @DisplayName("increments error counter when response status is 404")
    void incrementsErrorCounterOn4xxResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/missing");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(inv -> {
            ((HttpServletResponse) inv.getArgument(1)).setStatus(404);
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);

        assertThat(meterRegistry.counter("tractor.api.errors",
                "status", "404", "uri", "/api/missing", "app", "tractor-store-backend").count())
                .isEqualTo(1.0);
    }

    @Test
    @DisplayName("does not increment error counter for 200 OK responses")
    void doesNotIncrementErrorCounterFor2xx() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/catalog/products");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(meterRegistry.getMeters()).isEmpty();
    }

    @Test
    @DisplayName("increments error counter when response status is 500")
    void incrementsErrorCounterOn5xxResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(inv -> {
            ((HttpServletResponse) inv.getArgument(1)).setStatus(500);
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);

        assertThat(meterRegistry.counter("tractor.api.errors",
                "status", "500", "uri", "/api/orders", "app", "tractor-store-backend").count())
                .isEqualTo(1.0);
    }
}
