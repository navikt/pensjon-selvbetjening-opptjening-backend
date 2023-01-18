package no.nav.pensjon.selvbetjeningopptjening.security.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryStringParserTest {

    @Test
    void when_queryString_is_null_or_emptyString_or_noQueryString_then_getValue_returns_emptyString() {
        assertEquals("", QueryStringParser.getValue(null, "param1"));
        assertEquals("", QueryStringParser.getValue("", "param1"));
        assertEquals("", QueryStringParser.getValue("not-a-query-string", "param1"));
    }

    @Test
    void when_requestedParamIsNotInQueryString_then_getValue_returns_emptyString() {
        assertEquals("", QueryStringParser.getValue("param1=value1", "param2"));
    }

    @Test
    void when_queryStringOnlyContainsRequestedParam_then_getValue_returns_paramValue() {
        assertEquals("value1", QueryStringParser.getValue("param1=value1", "param1"));
    }

    @Test
    void when_queryStringContainsSeveralParams_then_getValue_returns_valueOfRequestedParam() {
        assertEquals("value1", QueryStringParser.getValue("param1=value1&param2=value2", "param1"));
        assertEquals("value2", QueryStringParser.getValue("param1=value1&param2=value2", "param2"));
    }

    @Test
    void when_queryStringContainsDuplicateParams_then_getValue_returns_valueOfRequestedParam() {
        assertEquals("value1", QueryStringParser.getValue("param1=value1&param1=value1", "param1"));
    }
}
