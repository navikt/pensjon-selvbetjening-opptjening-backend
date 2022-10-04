package no.nav.pensjon.selvbetjeningopptjening.usersession.token;

import no.nav.pensjon.selvbetjeningopptjening.security.dto.TokenResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenDataMapperTest {

    @Test
    void map_maps_values() {
        var dto = new TokenResponseDto();
        dto.setAccessToken("access-token");
        dto.setIdToken("id-token");
        dto.setRefreshToken("refresh-token");

        TokenData tokenData = TokenDataMapper.map(dto);

        assertEquals(dto.getAccessToken(), tokenData.getAccessToken());
        assertEquals(dto.getIdToken(), tokenData.getIdToken());
        assertEquals(dto.getRefreshToken(), tokenData.getRefreshToken());
    }
}
