package no.nav.pensjon.selvbetjeningopptjening.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;

@Component
public class NavCallIdFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            MDC.put(NAV_CALL_ID, getCorrelationId());
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(NAV_CALL_ID);
        }
    }

    private String getCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
