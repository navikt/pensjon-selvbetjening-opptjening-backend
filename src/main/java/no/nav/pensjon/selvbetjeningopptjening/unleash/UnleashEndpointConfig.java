package no.nav.pensjon.selvbetjeningopptjening.unleash;

import static java.lang.Long.parseLong;
import static java.lang.System.getProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.repository.HttpToggleFetcher;
import no.finn.unleash.repository.ToggleFetcher;
import no.finn.unleash.strategy.Strategy;
import no.finn.unleash.util.UnleashConfig;


import no.nav.pensjon.selvbetjeningopptjening.unleash.strategies.ByEnvironmentStrategy;
import no.nav.pensjon.selvbetjeningopptjening.unleash.strategies.ByInstanceIdStrategy;
import no.nav.pensjon.selvbetjeningopptjening.unleash.strategies.ByProfileStrategy;
import no.nav.pensjon.selvbetjeningopptjening.unleash.strategies.ByUserIdStrategy;
import no.nav.pensjon.selvbetjeningopptjening.unleash.strategies.IsNotProdStrategy;

@Configuration
public class UnleashEndpointConfig {

    @Value("${unleash.endpoint.url}")
    private String endpoint;
    @Value("${unleash.toggle.interval}")
    private String togglesInterval;

    @Bean
    public UnleashConfig unleashConfig() {
        String envName = getProperty("environment.name");
        String environmentName = null != envName ? envName : "local";
        String instanceId = getProperty("instance.id");

        if (null == instanceId) {
            instanceId = "local";
        }

        return UnleashConfig.builder()
                .appName("pensjon-selvbetjening-opptjening-backend")
                .environment(environmentName)
                .instanceId(instanceId)
                .fetchTogglesInterval(parseLong(togglesInterval))
                .unleashAPI(endpoint)
                .build();
    }

    @Bean
    @Autowired
    public Unleash defaultUnleash(UnleashConfig unleashConfig) {
        Strategy[] strategies = {new IsNotProdStrategy(), new ByEnvironmentStrategy(), new ByInstanceIdStrategy(), new ByUserIdStrategy(), new ByProfileStrategy()};
        DefaultUnleash unleash = new DefaultUnleash(unleashConfig, strategies);
        UnleashProvider.initialize(unleash);
        return unleash;
    }

    @Bean("cons.selvbetjening.opptjening.backend.UnleashConsumerService")
    public UnleashConsumerService unleashService() {
        return new UnleashBean();
    }

    @Bean
    @Autowired
    public ToggleFetcher unleashHttpToggleFetcher(UnleashConfig unleashConfig) {
        return new HttpToggleFetcher(unleashConfig);
    }
}