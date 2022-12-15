package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import no.nav.pensjon.selvbetjeningopptjening.security.UserType;

import java.io.Serializable;

public record User(String id, UserType type) implements Serializable {
}
