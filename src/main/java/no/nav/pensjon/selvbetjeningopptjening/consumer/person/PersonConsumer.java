package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import no.nav.pensjon.selvbetjeningopptjening.consumer.pen.AuthorizedPenConsumer;
import no.nav.pensjon.selvbetjeningopptjening.consumer.sts.ServiceTokenGetter;
import no.nav.pensjon.selvbetjeningopptjening.health.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.health.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.AfpHistorikkMapper;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.UforeHistorikkMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

@Component
public class PersonConsumer extends AuthorizedPenConsumer implements Pingable {

    private static final String ENDPOINT_PATH = "person";
    private static final String SERVICE_DESCRIPTION = PEN + " " + ENDPOINT_PATH;
    private static final String AFP_HISTORIKK_SERVICE = "PROPEN2602 getAfphistorikkForPerson";
    private static final String UFORE_HISTORIKK_SERVICE = "PROPEN2603 getUforehistorikkForPerson";
    private static final String AFP_HISTORIKK_RESOURCE = "afphistorikk";
    private static final String UFORE_HISTORIKK_RESOURCE = "uforehistorikk";
    private static final String PING_RESOURCE = "ping";
    private final WebClient webClient;
    private final String endpoint;

    public PersonConsumer(@Qualifier("epoch-support") WebClient webClient,
                          @Value("${pen.endpoint.url}") String endpoint,
                          ServiceTokenGetter tokenGetter) {
        super(tokenGetter);
        this.webClient = requireNonNull(webClient);
        this.endpoint = requireNonNull(endpoint);
    }

    public AfpHistorikk getAfpHistorikkForPerson(String fnr) {
        return getObject(this::getAfpHistorikk, fnr, AFP_HISTORIKK_SERVICE);
    }

    public UforeHistorikk getUforeHistorikkForPerson(String fnr) {
        return getObject(this::getUforeHistorikk, fnr, UFORE_HISTORIKK_SERVICE);
    }

    @Override
    public void ping() {
        getObject(this::ping, "pong", SERVICE_DESCRIPTION + " " + PING_RESOURCE);
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", SERVICE_DESCRIPTION, pingUri());
    }

    private AfpHistorikk getAfpHistorikk(String fnr, String authHeaderValue) {
        AfpHistorikkDto dto = getAfpHistorikkDto(fnr, authHeaderValue);
        return AfpHistorikkMapper.fromDto(dto);
    }

    private AfpHistorikkDto getAfpHistorikkDto(String fnr, String authHeaderValue) {
        return webClient
                .get()
                .uri(path(AFP_HISTORIKK_RESOURCE))
                .header(HttpHeaders.AUTHORIZATION, authHeaderValue)
                .header(PersonHttpHeaders.PID, fnr)
                .retrieve()
                .bodyToMono(AfpHistorikkDto.class)
                .block();
    }

    private UforeHistorikk getUforeHistorikk(String fnr, String authHeaderValue) {
        UforeHistorikkDto dto = getUforeHistorikkDto(fnr, authHeaderValue);
        return UforeHistorikkMapper.fromDto(dto);
    }

    private UforeHistorikkDto getUforeHistorikkDto(String fnr, String authHeaderValue) {
        return webClient
                .get()
                .uri(path(UFORE_HISTORIKK_RESOURCE))
                .header(HttpHeaders.AUTHORIZATION, authHeaderValue)
                .header(PersonHttpHeaders.PID, fnr)
                .retrieve()
                .bodyToMono(UforeHistorikkDto.class)
                .block();
    }

    private String ping(String defaultResponse, String authHeaderValue) {
        webClient
                .get()
                .uri(pingUri())
                .header(HttpHeaders.AUTHORIZATION, authHeaderValue)
                .retrieve()
                .toBodilessEntity()
                .block();

        return defaultResponse;
    }

    private String pingUri() {
        return path(PING_RESOURCE);
    }

    private String path(String end) {
        return UriComponentsBuilder.fromHttpUrl(endpoint)
                .pathSegment(ENDPOINT_PATH, end)
                .toUriString();
    }
}
