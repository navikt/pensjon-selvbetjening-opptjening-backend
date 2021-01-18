package no.nav.pensjon.selvbetjeningopptjening.security.group;

import static java.util.Objects.requireNonNull;

public class Group {

    private final String id;

    public Group(String id) {
        this.id = requireNonNull(id);
    }

    public String getId() {
        return id;
    }
}
