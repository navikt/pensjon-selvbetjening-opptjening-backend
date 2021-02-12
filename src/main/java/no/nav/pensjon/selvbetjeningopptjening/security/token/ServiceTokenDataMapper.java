package no.nav.pensjon.selvbetjeningopptjening.security.token;

import no.nav.pensjon.selvbetjeningopptjening.security.token.dto.ServiceTokenDataDto;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

public class ServiceTokenDataMapper {

    public static ServiceTokenData from(ServiceTokenDataDto dto, LocalDateTime time) {
        requireNonNull(dto, "dto");

        return new ServiceTokenData(
                dto.getAccessToken(),
                dto.getTokenType(),
                requireNonNull(time, "time"),
                dto.getExpiresIn());
    }
}
