package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.ServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class FilterChainUtilTest {

    private static final String ATTRIBUTE_NAME = "FilterChainData";
    private static final String ATTRIBUTE_VALUE = "value1";

    @Mock
    private ServletRequest request;

    @Test
    void getAttribute_returns_filterChainData() {
        when(request.getAttribute(ATTRIBUTE_NAME))
                .thenReturn(FilterChainData.instanceWhenRequestIsForUnprotectedResource());

        FilterChainData data = FilterChainUtil.getAttribute(request);

        assertTrue(data.requestIsForUnprotectedResource());
    }

    @Test
    void getAttribute_returns_defaultData_when_no_FilterChainData_attribute_in_request() {
        when(request.getAttribute(ATTRIBUTE_NAME)).thenReturn(null);
        FilterChainData data = FilterChainUtil.getAttribute(request);
        assertFalse(data.requestIsForUnprotectedResource());
    }

    @Test
    void setAttribute_sets_value_of_FilterChainData_attribute() {
        FilterChainUtil.setAttribute(request, ATTRIBUTE_VALUE);
        verify(request, times(1)).setAttribute(ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    }
}
