package no.nav.pensjon.selvbetjeningopptjening.security.oauth2;

import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigClient;
import no.nav.pensjon.selvbetjeningopptjening.security.impersonal.Oauth2ConfigGetter;
import no.nav.pensjon.selvbetjeningopptjening.security.oauth2.key.Oauth2KeyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Support for multiple issuers of JSON Web Tokens.
 * The issuers are identified by the "iss" claim in the JWT.
 */
@Component
public class MultiIssuerSupport {

    private static final Logger log = LoggerFactory.getLogger(MultiIssuerSupport.class);
    private final List<Oauth2BasicData> oauth2Basics;
    private final ConcurrentHashMap<String, Oauth2Handler> handlersByIssuer = new ConcurrentHashMap<>();

    public MultiIssuerSupport(@Qualifier("external-user") Oauth2BasicData externalUserOauth2BasicData,
                              @Qualifier("internal-user") Oauth2BasicData internalUserOauth2BasicData) {
        this.oauth2Basics = List.of(
                externalUserOauth2BasicData,
                internalUserOauth2BasicData);
    }

    public Oauth2Handler getOauth2HandlerForIssuer(String issuer) {
        return handlersByIssuer.computeIfAbsent(issuer, this::freshOauth2Handler);
    }

    private Oauth2Handler freshOauth2Handler(String issuer) {
        int index = 0;
        boolean found;
        Oauth2ConfigGetter configGetter;
        WebClient webClient;

        do {
            webClient = WebClient.create();
            configGetter = new Oauth2ConfigClient(webClient, oauth2Basics.get(index).getWellKnownUrl());
            found = configGetter.getIssuer().equals(issuer);
        } while (!found && ++index < oauth2Basics.size());

        if (!found) {
            String message = format("Invalid issuer '%s'", issuer);
            log.error(message);
            throw new Oauth2Exception(message);
        }

        Oauth2BasicData oauth2Data = oauth2Basics.get(index);

        return new Oauth2Handler(
                new Oauth2KeyClient(webClient, configGetter),
                oauth2Data.getAcceptedAudience(),
                oauth2Data.getAudienceClaimKey(),
                oauth2Data.getUserIdClaimKey(),
                oauth2Data.getUserType());
    }
}
