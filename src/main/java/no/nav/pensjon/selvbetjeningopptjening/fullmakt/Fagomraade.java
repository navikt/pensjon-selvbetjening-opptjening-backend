package no.nav.pensjon.selvbetjeningopptjening.fullmakt;

import java.util.HashMap;
import java.util.Map;

public enum Fagomraade {

    NONE("null", false),
    PEN("PEN", true), // Pensjon
    ALL("*", true);

    private static final Map<String, Fagomraade> VALUES_BY_CODE = new HashMap<>();

    static {
        for (Fagomraade value : values()) {
            VALUES_BY_CODE.put(value.code, value);
        }
    }

    public static Fagomraade findByCode(String code) {
        Fagomraade fagomraade = VALUES_BY_CODE.get(code);
        return fagomraade == null ? Fagomraade.NONE : fagomraade;
    }

    private final String code;
    private final boolean validForPensjon;

    Fagomraade(String code, boolean validForPensjon) {
        this.code = code;
        this.validForPensjon = validForPensjon;
    }

    public boolean validForPensjon() {
        return validForPensjon;
    }

    public String code() {
        return code;
    }
}
