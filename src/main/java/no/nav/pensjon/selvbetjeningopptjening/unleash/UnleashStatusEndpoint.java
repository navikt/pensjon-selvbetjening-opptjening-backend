package no.nav.pensjon.selvbetjeningopptjening.unleash;

import no.nav.security.token.support.core.api.ProtectedWithClaims;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.pensjon.selvbetjeningopptjening.unleash.UnleashProvider.toggle;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.ISSUER;

@RestController
@RequestMapping("api")
@ProtectedWithClaims(issuer = ISSUER) // Use @Unprotected when running with laptop/uimage profile
public class UnleashStatusEndpoint {

    @PostMapping("/unleash")
    public UnleashStatusResponse getUnleashStatus(@RequestBody UnleashStatusRequest request) {
        var response = new UnleashStatusResponse();
        List<String> toggles = request.getToggleList();

        if (toggles == null) {
            return response;
        }

        response.setToggles(getStatesByToggle(toggles));
        return response;
    }

    private static Map<String, Boolean> getStatesByToggle(List<String> toggles) {
        Map<String, Boolean> statesByToggle = new HashMap<>();
        toggles.forEach(toggle -> statesByToggle.put(toggle, toggle(toggle).isEnabled()));
        return statesByToggle;
    }
}
