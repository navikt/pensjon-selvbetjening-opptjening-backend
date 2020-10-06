package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class ByInstanceIdStrategy extends PropertyStrategy {

    private static final String INSTANCE_PROPERTY = "instance.id";

    @Override
    public String getName() {
        return "byInstanceId";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.ofNullable(parameters)
                .map(par -> par.get("instance"))
                .filter(s -> !s.isEmpty())
                .map(instance -> instance.split(","))
                .map(Arrays::stream)
                .map(instance -> instance.anyMatch(this::isCurrentInstance))
                .orElse(false);
    }

    private boolean isCurrentInstance(String instance) {
        return getProperty(INSTANCE_PROPERTY, "local").equals(instance);
    }
}
