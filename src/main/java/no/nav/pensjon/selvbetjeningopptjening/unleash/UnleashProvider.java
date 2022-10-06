package no.nav.pensjon.selvbetjeningopptjening.unleash;

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

        public boolean isEnabled() {
            return unleash.isEnabled(toggle);
        }
    }
}
