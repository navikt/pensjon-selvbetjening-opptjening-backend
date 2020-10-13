package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class ByEnvironmentStrategy extends PropertyStrategy {

    private static final String ENVIRONMENT_PROPERTY = "environment.name";
    private static final String APP_ENVIRONMENT = "APP_ENVIRONMENT";

    @Override
    public String getName() {
        return "byEnvironment";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.ofNullable(parameters)
                .map(par -> par.get("miljø"))
                .filter(s -> !s.isEmpty())
                .map(env -> env.split(","))
                .map(Arrays::stream)
                .map(env -> env.anyMatch(this::isCurrentEnvironment))
                .orElse(false);
    }

    private boolean isCurrentEnvironment(String environment) {
        return getProperty(ENVIRONMENT_PROPERTY, "local").equals(environment) ||
                equalsIgnoreCase(environment, getEnvironmentVariable(APP_ENVIRONMENT));
    }
}