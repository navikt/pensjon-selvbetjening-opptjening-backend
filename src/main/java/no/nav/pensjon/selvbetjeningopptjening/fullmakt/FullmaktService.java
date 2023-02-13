package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.FullmaktClient;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FullmaktService implements FullmaktApi {

    private final FullmaktClient client;

    public FullmaktService(FullmaktClient client) {
        this.client = client;
    }

    @Override
    public boolean harFullmaktsforhold(String fullmaktsgiverPid, String fullmektigPid) {
        return client.harFullmaktsforhold(fullmaktsgiverPid, fullmektigPid);
    }
}
