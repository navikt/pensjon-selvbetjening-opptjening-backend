package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import static java.util.Optional.ofNullable;

import java.util.Arrays;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import no.finn.unleash.strategy.Strategy;

import no.nav.pensjon.selvbetjeningopptjening.config.SpringContext;
import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;

public class ByUserIdStrategy implements Strategy {

    @Override
    public String getName() {
        return "byUserId";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return ofNullable(parameters)
                .map(par -> par.get("user"))
                .filter(s -> !s.isEmpty())
                .map(instance -> instance.split(","))
                .map(Arrays::stream)
                .map(instance -> instance.anyMatch(this::isCurrentUser))
                .orElse(false);
    }

    private boolean isCurrentUser(String user) {
        String userId = SpringContext.getBean(StringExtractor.class).extract();
        return userId != null && (userId.equals(user) || isEncryptedUserId(userId, user));
    }

    private boolean isEncryptedUserId(String givenUserId, String encryptedUserId) {
        if (encryptedUserId.matches("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}")) {
            BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
            return bcryptEncoder.matches(givenUserId, encryptedUserId);
        }
        return false;
    }
}
