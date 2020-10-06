package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.finn.unleash.strategy.Strategy;

public abstract class PropertyStrategy implements Strategy {

    protected String getEnvironmentVariable(String name) {
        return System.getenv(name);
    }

    protected String getProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
}
