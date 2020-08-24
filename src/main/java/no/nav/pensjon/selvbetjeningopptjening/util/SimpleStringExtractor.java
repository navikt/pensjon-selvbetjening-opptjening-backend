package no.nav.pensjon.selvbetjeningopptjening.util;

import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;

public class SimpleStringExtractor implements StringExtractor {

    private final String value;

    public SimpleStringExtractor(String value) {
        this.value = value;
    }

    @Override
    public String extract() {
        return value;
    }
}
