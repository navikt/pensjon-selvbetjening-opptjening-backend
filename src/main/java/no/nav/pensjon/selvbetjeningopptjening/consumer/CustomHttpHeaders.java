package no.nav.pensjon.selvbetjeningopptjening.consumer;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.NAV_CALL_ID;

public final class CustomHttpHeaders {
    public static final String AKTOER_NUMMER = "aktorNr"; // aktørnummer
    public static final String BEHANDLINGSNUMMER = "behandlingsnummer";
    public static final String FNR = "pid"; // fødselsnummer
    public static final String FOM = "fom"; // fra og med
    public static final String CALL_ID = NAV_CALL_ID;
    public static final String CONSUMER_ID = "Nav-Consumer-Id";
    public static final String CONSUMER_TOKEN = "Nav-Consumer-Token";
    public static final String PERSON_IDENT = "Nav-Personident";
    public static final String PERSON_IDENTER = "Nav-Personidenter";
    public static final String USER_ID = "Nav-User-Id";
    public static final String PID = "pid"; // personidentifikator
    public static final String THEME = "Tema";
    public static final String FULLMAKTSGIVER_PID = "fullmaktsgiverPid"; // PID = person identifier

    private CustomHttpHeaders() {
    }
}
