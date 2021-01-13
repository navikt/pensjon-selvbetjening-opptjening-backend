package no.nav.pensjon.selvbetjeningopptjening.consumer.person;

import no.nav.pensjon.selvbetjeningopptjening.common.selftest.PingInfo;
import no.nav.pensjon.selvbetjeningopptjening.common.selftest.Pingable;
import no.nav.pensjon.selvbetjeningopptjening.consumer.FailedCallingExternalServiceException;
import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikkDto;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.AfpHistorikkMapper;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.mapping.UforeHistorikkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.PEN;

public class PersonConsumer implements Pingable {

    private String endpoint;
    private RestTemplate restTemplate;
    //TODO Migrate to WebClient

    public PersonConsumer(String endpoint) {
        this.endpoint = endpoint;
    }

    public AfpHistorikk getAfpHistorikkForPerson(String fnr) {
        try {
            AfpHistorikkDto historikk = restTemplate.exchange(
                    endpoint + "/person/afphistorikk",
                    HttpMethod.GET,
                    prepareHttpHeaders(fnr),
                    AfpHistorikkDto.class)
                    .getBody();

            return AfpHistorikkMapper.fromDto(historikk);
        } catch (RestClientResponseException e) {
            throw handle(e, "PROPEN2602 getAfphistorikkForPerson");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN2602 getAfphistorikkForPerson", "An error occurred in the consumer", e);
        }
    }

    public UforeHistorikk getUforeHistorikkForPerson(String fnr) {
        try {
            UforeHistorikkDto historikk = restTemplate.exchange(
                    endpoint + "/person/uforehistorikk",
                    HttpMethod.GET,
                    prepareHttpHeaders(fnr),
                    UforeHistorikkDto.class)
                    .getBody();

            return UforeHistorikkMapper.fromDto(historikk);
        } catch (RestClientResponseException e) {
            throw handle(e, "PROPEN2603 getUforehistorikkForPerson");
        } catch (Exception e) {
            throw new FailedCallingExternalServiceException(PEN, "PROPEN2603 getUforehistorikkForPerson", "An error occurred in the consumer", e);
        }
    }

    @Override
    public void ping() {
        try {
                restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).path("/person/ping").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class).getBody();
        } catch (RestClientResponseException rce) {
            throw handle(rce, "Error in PEN Person");
        }
    }

    @Override
    public PingInfo getPingInfo() {
        return new PingInfo("REST", "PEN", UriComponentsBuilder.fromHttpUrl(endpoint).path("/person/ping").toUriString());
    }

    private FailedCallingExternalServiceException handle(RestClientResponseException e, String serviceIdentifier) {
        if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 401 UNAUTHORIZED", e);
        }

        if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the provider, received 500 INTERNAL SERVER ERROR", e);
        }

        if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
            return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "Received 400 BAD REQUEST", e);
        }

        return new FailedCallingExternalServiceException(PEN, serviceIdentifier, "An error occurred in the consumer", e);
    }

    @Autowired
    @Qualifier("conf.opptjening.resttemplate.oidc")
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static HttpEntity<HttpHeaders> prepareHttpHeaders(String fnr) {
        var headers = new HttpHeaders();
        headers.add("pid", fnr);
        return new HttpEntity<>(headers);
    }
}
