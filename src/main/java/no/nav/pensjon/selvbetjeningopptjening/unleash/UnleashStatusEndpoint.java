package no.nav.pensjon.selvbetjeningopptjening.unleash;

import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static no.nav.pensjon.selvbetjeningopptjening.unleash.UnleashProvider.toggle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

@RestController
@RequestMapping("api")
@ProtectedWithClaims(issuer = ISSUER) // Use @Unprotected when running with laptop/uimage profile
public class UnleashStatusEndpoint {

    @PostMapping("/unleash")
    public UnleashStatusResponse getUnleashStatus(@RequestBody UnleashStatusRequest request){
        UnleashStatusResponse response = new UnleashStatusResponse();
        Map<String, Boolean> unleashStatus = new HashMap<>();

        if(request.getToggleList() != null) {
            request.getToggleList()
                    .forEach(toggleString -> unleashStatus.put(toggleString, toggle(toggleString).isEnabled()));

            response.setUnleash(unleashStatus);
        }
        return response;
    }
}
