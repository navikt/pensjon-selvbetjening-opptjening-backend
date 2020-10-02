package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.nav.pensjon.selvbetjeningopptjening.config.StringExtractor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class ByUserIdStrategy extends BeanStrategy {

    @Override
    public String getName() {
        return "byUserId";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.ofNullable(parameters)
                .map(par -> par.get("user"))
                .filter(s -> !s.isEmpty())
                .map(user -> user.split(","))
                .map(Arrays::stream)
                .map(user -> user.anyMatch(this::isCurrentUser))
                .orElse(false);
    }

    private boolean isCurrentUser(String user) {
        String userId = getBean(StringExtractor.class).extract();
        return userId != null && (userId.equals(user) || isEncryptedUserId(userId, user));
    }

    private boolean isEncryptedUserId(String givenUserId, String encryptedUserId) {
        if (!encryptedUserId.matches("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}")) {
            return false;
        }

        return new BCryptPasswordEncoder().matches(givenUserId, encryptedUserId);
    }
}
