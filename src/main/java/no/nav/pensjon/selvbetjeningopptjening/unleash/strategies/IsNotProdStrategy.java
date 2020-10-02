package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

public class IsNotProdStrategy extends PropertyStrategy {

    @Override
    public String getName() {
        return "isNotProd";
    }

    @Override
    public boolean isEnabled(Map<String, String> map) {
        return !"p".equals(getProperty("environment.name", "local")) &&
                !startsWithIgnoreCase(getEnvironmentVariable("NAIS_CLUSTER_NAME"), "prod-");
    }
}
