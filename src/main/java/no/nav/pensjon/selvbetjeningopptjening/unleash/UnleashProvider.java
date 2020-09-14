package no.nav.pensjon.selvbetjeningopptjening.unleash;

import java.util.function.Supplier;

import no.finn.unleash.Unleash;

public class UnleashProvider {
    private static Unleash unleash;

    public static void initialize(Unleash unleash) {
        UnleashProvider.unleash = unleash;
    }

    public static Unleash get() {
        return unleash;
    }

    public static Toggle toggle(String toggle) {
        return new Toggle(toggle);
    }

    public static class Toggle {

        private final String toggle;

        Toggle(String toggle) {
            this.toggle = toggle;
        }

        public <E extends Throwable> void throwIfDisabled(Supplier<E> supplier) throws E {
            if(!unleash.isEnabled(toggle)) {
                throw supplier.get();
            }
        }

        public boolean isDisabled() {
            return !unleash.isEnabled(toggle);
        }

        public boolean isEnabled() {
            return unleash.isEnabled(toggle);
        }
    }
}
