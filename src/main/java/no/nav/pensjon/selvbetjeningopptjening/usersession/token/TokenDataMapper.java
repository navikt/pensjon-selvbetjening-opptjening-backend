package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.TokenResponseDto;

import static java.util.Objects.requireNonNull;

public class TokenDataMapper {

    public static TokenData map(TokenResponseDto dto) {
        requireNonNull(dto);

        return new TokenData(
                dto.getAccessToken(),
                dto.getIdToken(),
                dto.getRefreshToken());
    }
}
