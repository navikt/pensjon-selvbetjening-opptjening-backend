package no.nav.pensjon.selvbetjeningopptjening.security.token;

import no.nav.pensjon.selvbetjeningopptjening.security.token.dto.ServiceTokenDataDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTokenDataMapperTest {

    @Test
    void from_maps_from_dto_to_domainObject() {
        var dto = new ServiceTokenDataDto();
        dto.setAccessToken("token");
        dto.setTokenType("type");
        dto.setExpiresIn(123L);
        dto.setAdditionalProperty("prop", "value");

        ServiceTokenData data = ServiceTokenDataMapper.from(dto, LocalDateTime.MIN);

        assertEquals("token", data.getAccessToken());
        assertEquals("type", data.getTokenType());
        assertEquals(123L, data.getExpiresInSeconds());
        assertEquals(LocalDateTime.MIN, data.getIssuedTime());
        assertEquals("ServiceUserToken{accessToken='token', expiresInSeconds=123, tokenType='type'}", data.toString());
    }

    @Test
    void from_throws_NullPointerException_when_dto_null() {
        var exception = assertThrows(NullPointerException.class, () -> ServiceTokenDataMapper.from(null, LocalDateTime.MIN));
        assertEquals("dto", exception.getMessage());
    }

    @Test
    void from_throws_NullPointerException_when_time_null() {
        var exception = assertThrows(NullPointerException.class, () -> ServiceTokenDataMapper.from(new ServiceTokenDataDto(), null));
        assertEquals("time", exception.getMessage());
    }
}
