package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.TokenResponseDto;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

public class TokenDataMapper {

    public static TokenData map(TokenResponseDto dto) {
        requireNonNull(dto);

        return new TokenData(
                dto.getAccessToken(),
                dto.getIdToken(),
                dto.getRefreshToken(),
                LocalDateTime.now(),
                3600L);
    }

    public static TokenData map(TokenResponseDto dto, LocalDateTime time) {
        requireNonNull(dto);

        return new TokenData(
                dto.getAccessToken(),
                dto.getIdToken(),
                dto.getRefreshToken(),
                requireNonNull(time, "time"),
                (long) dto.getExpiresIn());
    }
}
