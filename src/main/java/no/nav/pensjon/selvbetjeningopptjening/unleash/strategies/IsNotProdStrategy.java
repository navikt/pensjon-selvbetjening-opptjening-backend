package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import no.finn.unleash.strategy.Strategy;

public class IsNotProdStrategy implements Strategy {
    @Override
    public String getName() {
        return "isNotProd";
    }

    @Override
    public boolean isEnabled(Map<String, String> map) {
        return !"p".equals(System.getProperty("environment.name", "local")) &&
                !StringUtils.startsWithIgnoreCase(System.getenv("NAIS_CLUSTER_NAME"), "prod-");
    }
}