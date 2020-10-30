package no.nav.pensjon.selvbetjeningopptjening;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;

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
        Random random = new Random();
        int start = isBornBefore2000 ? 0 : 900;
        int bound = isBornBefore2000 ? 500 : 100;
        for (int i = 0; i < 100; i++) {
            int nr = random.nextInt(bound) + start;
            String pidCandidate = date + getIndividNr(nr);
            pidCandidate = pidCandidate + getk1k2(pidCandidate);
            if (Pid.isValidPid(pidCandidate)) {
                return new Pid(pidCandidate);
            }
        }
        throw new RuntimeException("Could not generate a valid Pid for " + date);
    }

    private static String getIndividNr(int individnummer) {
        StringBuilder sb = new StringBuilder();
        if (individnummer < 10) {
            sb.append("00");
        } else if (individnummer < 100) {
            sb.append("0");
        }
        sb.append(individnummer);
        return sb.toString();
    }

    private static String getk1k2(String fnr) {
        // FORMAT: DDMMYYiiikk
        int d1 = Integer.parseInt(fnr.substring(0, 1));
        int d2 = Integer.parseInt(fnr.substring(1, 2));
        int m1 = Integer.parseInt(fnr.substring(2, 3));
        int m2 = Integer.parseInt(fnr.substring(3, 4));
        int a1 = Integer.parseInt(fnr.substring(4, 5));
        int a2 = Integer.parseInt(fnr.substring(5, 6));
        int i1 = Integer.parseInt(fnr.substring(6, 7));
        int i2 = Integer.parseInt(fnr.substring(7, 8));
        int i3 = Integer.parseInt(fnr.substring(8, 9));

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
