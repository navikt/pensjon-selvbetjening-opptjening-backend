package no.nav.pensjon.selvbetjeningopptjening.unleash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import no.finn.unleash.Unleash;
import no.finn.unleash.repository.ToggleFetcher;

public class UnleashBean implements UnleashConsumerService {

    private Unleash unleash;
    private ToggleFetcher toggleFetcher;
    private String endpointUrl;

    @Override
    public boolean isEnabled(String feature) {
        return unleash.isEnabled(feature);
    }

    @Value(value = "${unleash.endpoint.url}")
    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Autowired
    public void setUnleash(Unleash unleash) {
        this.unleash = unleash;
    }

    @Autowired
    public void setToggleFetcher(ToggleFetcher toggleFetcher) {
        this.toggleFetcher = toggleFetcher;
    }
}