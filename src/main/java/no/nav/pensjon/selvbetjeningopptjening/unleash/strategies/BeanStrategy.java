package no.nav.pensjon.selvbetjeningopptjening.unleash.strategies;

import no.finn.unleash.strategy.Strategy;
import no.nav.pensjon.selvbetjeningopptjening.config.SpringContext;

public abstract class BeanStrategy implements Strategy {

    protected <T> T getBean(Class<T> beanClass) {
        return SpringContext.getBean(beanClass);
    }
}
