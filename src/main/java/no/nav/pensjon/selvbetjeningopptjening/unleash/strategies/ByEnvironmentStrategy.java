package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

public class ByEnvironmentStrategy extends PropertyStrategy {

    private static final String ENVIRONMENT_PROPERTY = "environment.name";
    private static final String APP_ENVIRONMENT = "APP_ENVIRONMENT";
    private static final String DEFAULT_ENVIRONMENT = "local";

    @Override
    public String getName() {
        return "byEnvironment";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.of(parameters)
                .map(params -> params.get("miljø"))
                .filter(env -> !env.isEmpty())
                .map(env -> env.split(","))
                .map(Arrays::stream)
                .map(env -> env.anyMatch(this::isCurrentEnvironment))
                .orElse(false);
    }

    private boolean isCurrentEnvironment(String environment) {
        return hasText(environment) && (
                environment.equalsIgnoreCase(getProperty(ENVIRONMENT_PROPERTY, DEFAULT_ENVIRONMENT)) ||
                        environment.equalsIgnoreCase(getEnvironmentVariable(APP_ENVIRONMENT)));
    }
}
