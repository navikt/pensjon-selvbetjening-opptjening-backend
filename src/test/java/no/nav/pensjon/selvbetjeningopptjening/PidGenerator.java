package no.nav.pensjon.selvbetjeningopptjening;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.person.pid.PidValidator;

import static java.lang.Integer.parseInt;

public class PidGenerator {

    public static Pid generatePidAtAge(int age) {
        int currentYear = LocalDate.now().getYear();
        return generatePid(LocalDate.of(currentYear - age, Month.JANUARY, 1));
    }

    public static Pid generatePid(LocalDate date) {
        String dateAsString = date.format(DateTimeFormatter.ofPattern("ddMMyy"));
        return generatePid(dateAsString, LocalDate.of(2000, Month.JANUARY, 1).isAfter(date));
    }

    /**
     * Get a pid from a String representation. Due to the unsafe String signature, this is not public.
     *
     * @param date             a string representation of a Date.
     * @param isBornBefore2000 if the user is born after 2000, start at another index than 0
     * @return the {@link Pid}
     */
    private static Pid generatePid(String date, boolean isBornBefore2000) {
        var random = new Random();
        int start = isBornBefore2000 ? 0 : 900;
        int bound = isBornBefore2000 ? 500 : 100;

        for (int index = 0; index < 100; index++) {
            int nr = random.nextInt(bound) + start;
            String candidate = date + padIndividnummer(nr);
            candidate += getk1k2(candidate);

            if (PidValidator.INSTANCE.isValidPid(candidate, false)) {
                return new Pid(candidate);
            }
        }

        throw new RuntimeException("Could not generate a valid PID for " + date);
    }

    private static String padIndividnummer(int individnummer) {
        var builder = new StringBuilder();

        if (individnummer < 10) {
            builder.append("00");
        } else if (individnummer < 100) {
            builder.append("0");
        }

        builder.append(individnummer);
        return builder.toString();
    }

    private static String getk1k2(String fnr) {
        // Format: DDMMYYiiikk
        int d1 = parseInt(fnr.substring(0, 1));
        int d2 = parseInt(fnr.substring(1, 2));
        int m1 = parseInt(fnr.substring(2, 3));
        int m2 = parseInt(fnr.substring(3, 4));
        int a1 = parseInt(fnr.substring(4, 5));
        int a2 = parseInt(fnr.substring(5, 6));
        int i1 = parseInt(fnr.substring(6, 7));
        int i2 = parseInt(fnr.substring(7, 8));
        int i3 = parseInt(fnr.substring(8, 9));

        // control 1
        int v1 = 3 * d1 + 7 * d2 + 6 * m1 + m2 + 8 * a1 + 9 * a2 + 4 * i1 + 5 * i2 + 2 * i3;
        int tmp = v1 / 11;
        int rest1 = v1 - tmp * 11;
        int k1 = rest1 == 0 ? 0 : 11 - rest1;

        // control 2
        int v2 = 5 * d1 + 4 * d2 + 3 * m1 + 2 * m2 + 7 * a1 + 6 * a2 + 5 * i1 + 4 * i2 + 3 * i3 + 2 * k1;
        tmp = v2 / 11;
        int rest2 = v2 - tmp * 11;
        int k2 = rest2 == 0 ? 0 : 11 - rest2;

        return k1 + "" + k2;
    }
}
