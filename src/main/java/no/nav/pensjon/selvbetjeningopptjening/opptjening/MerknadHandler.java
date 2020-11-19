package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.opptjening.OmsorgTypes.OMSORG_BARN_UNDER_6;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.OmsorgTypes.OMSORG_BARN_UNDER_7;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.*;
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

//TODO: Denne klassen må gjennomgås når man skal utvide mvp for å finne ut hvordan merknadene skal håndteres.
//      For mvp er de fleste merknader kommentert bort da mvp håndterer disse i EndringPensjonspptjeningCalculator i stedet.
public class MerknadHandler {

    static void setMerknadOmsorgsopptjeningPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng) {
        if (opptjening.hasMerknad(MerknadCode.OMSORGSOPPTJENING) || !pensjonspoeng.hasOmsorg()) {
            return;
        }

        if (opptjening.isOmsorgspoengLessThanOrEqualToPensjonspoeng()) {
            opptjening.addMerknad(MerknadCode.OMSORGSOPPTJENING);
        }
    }

    static void setMerknadOverforOmsorgsopptjeningPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng) {
        if (opptjening.hasMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        if (isTypeOmsorgBarn(pensjonspoeng.getOmsorg())) {
            opptjening.addMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING);
        }
    }

    static void addMerknaderOnOpptjening(int year,
                                         Opptjening opptjening,
                                         List<Beholdning> beholdninger,
                                         List<Uttaksgrad> uttaksgradhistorikk,
                                         AfpHistorikk afpHistorikk,
                                         UforeHistorikk uforehistorikk) {
        List<MerknadCode> merknader = new ArrayList<>();
        //addMerknadAfp(year, merknader, afpHistorikk);
        //addMerknadUforegrad(year, uforehistorikk, opptjening, merknader);

        //When beholdninger is null the user is not in Usergroup 4 or 5 and these merknads do not apply
        if (beholdninger != null) {
            addMerknadReform2010(year, merknader);
            addMerknadOmsorgFromPensjonsbeholdning(year, opptjening, merknader, beholdninger);
        }

        //addMerknadGradertAlderspensjon(year, uttaksgradhistorikk, merknader);
        addMerknadIngenOpptjening(opptjening, merknader);
        opptjening.addMerknader(merknader);
    }

    private static void addMerknadAfp(int year, List<MerknadCode> merknader, AfpHistorikk afpHistorikk) {
        if (afpHistorikk == null) {
            return;
        }

        int firstYearToBeMarkedWithAfp = afpHistorikk.getVirkningFom().getYear();
        int lastYearToBeMarkedWithAfp = afpHistorikk.getVirkningTom() == null ? LocalDate.now().getYear() - 1 : afpHistorikk.getVirkningTom().getYear();

        if (firstYearToBeMarkedWithAfp <= year && year <= lastYearToBeMarkedWithAfp) {
            merknader.add(MerknadCode.AFP);
        }
    }

    //TODO: Utvidelse av MVP. I MVP blir ikke ufore-merknaden brukt. Vurder om det samme kan gjøres i utvidelsen av MVP slik at denne koden kan fjernes fra MerknadHandler.
    private static void addMerknadUforegrad(int year, UforeHistorikk uforehistorikk, Opptjening opptjening, List<MerknadCode> merknader) {
        if (uforehistorikk == null) {
            return;
        }

        Integer maxUforegrad = getMaxUforegrad(year, uforehistorikk);

        if (maxUforegrad == null || maxUforegrad <= 0) {
            return;
        }

        opptjening.setMaxUforegrad(maxUforegrad);
        merknader.add(MerknadCode.UFOREGRAD);
    }

    private static Integer getMaxUforegrad(int year, UforeHistorikk historikk) {
        Integer maxUforegrad = null;

        for (Uforeperiode periode : historikk.getUforeperioder()) {
            //TODO: isRealUforeperiode seems superfluous in next line
            if (isRealUforeperiode(periode) && isStrictRealUforeperiode(periode) && isUforeperiodeVirkFomBeforeGrunnlagsAr(year, periode)) {
                if (maxUforegrad == null || periode.getUforegrad() > maxUforegrad) {
                    maxUforegrad = periode.getUforegrad();
                }
            }
        }

        return maxUforegrad;
    }

    private static boolean isUforeperiodeVirkFomBeforeGrunnlagsAr(int grunnlagsar, Uforeperiode periode) {
        LocalDate firstDayInGrunnlagsar = LocalDate.of(grunnlagsar, 1, 1);
        LocalDate virkFom = periode.getUfgFom();

        return virkFom.getYear() == grunnlagsar ||
                isDateBeforeOrEqual(periode.getUfgFom(), firstDayInGrunnlagsar) &&
                        (periode.getUfgTom() == null || grunnlagsArBeforeUforeTom(firstDayInGrunnlagsar, periode.getUfgTom()));
    }

    private static boolean grunnlagsArBeforeUforeTom(LocalDate firstDayInGrunnlagsar, LocalDate tom) {
        return firstDayInGrunnlagsar.getYear() == tom.getYear() || isDateBeforeOrEqual(firstDayInGrunnlagsar, tom);
    }

    private static boolean isDateBeforeOrEqual(LocalDate date, LocalDate otherDate) {
        return date.isBefore(otherDate) || date.isEqual(otherDate);
    }

    private static boolean isRealUforeperiode(Uforeperiode periode) {
        UforeTypeCode type = periode.getUforetype();

        return UforeTypeCode.UF_M_YRKE.equals(type) ||
                UforeTypeCode.UFORE.equals(type) ||
                UforeTypeCode.YRKE.equals(type);
    }

    private static boolean isStrictRealUforeperiode(Uforeperiode periode) {
        UforeTypeCode type = periode.getUforetype();

        return UforeTypeCode.UFORE.equals(type)
                || UforeTypeCode.UF_M_YRKE.equals(type);
    }

    private static void addMerknadReform2010(int year, List<MerknadCode> merknader) {
        if (year == REFORM_2010) {
            merknader.add(MerknadCode.REFORM);
        }
    }

    private static void addMerknadIngenOpptjening(Opptjening opptjening, List<MerknadCode> merknader) {
        if (merknader.contains(MerknadCode.REFORM)) {
            return;
        }

        if (opptjening.isNotPositive()) {
            merknader.add(MerknadCode.INGEN_OPPTJENING);
        }
    }

    private static void addMerknadOmsorgFromPensjonsbeholdning(int year, Opptjening opptjening, List<MerknadCode> merknader, List<Beholdning> beholdninger) {
//        beholdninger
//                .stream()
//                .filter(beholdning -> hasOmsorgsopptjening(year, beholdning))
//                .findFirst()
//                .ifPresent(beholdning -> addOmsorgsopptjeningMerknad(opptjening, merknader));

        beholdninger
                .stream()
                .filter(beholdning -> hasOverforeOmsorgsopptjening(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> addOverforeOmsorgsopptjeningMerknad(opptjening, merknader));
    }

    private static boolean hasOmsorgsopptjening(int year, Beholdning beholdning) {
        Omsorgsopptjening opptjening = beholdning.getOmsorgsopptjening();

        return opptjening != null
                && opptjening.getBelop() > 0
                && opptjening.getYear() == year;
    }

    private static boolean hasOverforeOmsorgsopptjening(int year, Beholdning beholdning) {
        Omsorgsopptjening opptjening = beholdning.getOmsorgsopptjening();
        return beholdningHasOpptjeningOmsorgBarn(opptjening) && opptjening.getYear() == year;
    }

    private static void addOmsorgsopptjeningMerknad(Opptjening opptjening, List<MerknadCode> merknader) {
        if (opptjening.hasMerknad(MerknadCode.OMSORGSOPPTJENING)) {
            return;
        }

        merknader.add(MerknadCode.OMSORGSOPPTJENING);
    }

    private static void addOverforeOmsorgsopptjeningMerknad(Opptjening opptjening, List<MerknadCode> merknader) {
        if (opptjening.hasMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        merknader.add(MerknadCode.OVERFORE_OMSORGSOPPTJENING);
    }

    private static Boolean beholdningHasOpptjeningOmsorgBarn(Omsorgsopptjening opptjening) {
        if (opptjening == null) {
            return false;
        }

        return opptjening
                .getOmsorger()
                .stream()
                .anyMatch(MerknadHandler::isTypeOmsorgBarn);
    }

    private static boolean isTypeOmsorgBarn(Omsorg omsorg) {
        return omsorg != null && isTypeOmsorgBarn(omsorg.getType());
    }

    private static boolean isTypeOmsorgBarn(String type) {
        return OMSORG_BARN_UNDER_7.equals(type) ||
                OMSORG_BARN_UNDER_6.equals(type);
    }

    private static void addMerknadGradertAlderspensjon(int year, List<Uttaksgrad> uttaksgrader, List<MerknadCode> merknader) {
        uttaksgrader
                .stream()
                .filter(uttaksgrad -> uttaksgradCoversYear(year, uttaksgrad))
                .forEach(uttaksgrad -> addUttakMerknad(merknader, uttaksgrad));
    }

    private static boolean uttaksgradCoversYear(int year, Uttaksgrad uttaksgrad) {
        return uttaksgrad.getFomDato().getYear() <= year
                && uttaksgradEndsAtOrAfter(year, uttaksgrad);
    }

    private static boolean uttaksgradEndsAtOrAfter(int year, Uttaksgrad uttaksgrad) {
        LocalDate tom = uttaksgrad.getTomDato();
        return tom == null || year <= tom.getYear();
    }

    private static void addUttakMerknad(List<MerknadCode> merknader, Uttaksgrad uttaksgrad) {
        Integer grad = uttaksgrad.getUttaksgrad();

        if (0 < grad && grad < 100 && !merknader.contains(MerknadCode.GRADERT_UTTAK)) {
            merknader.add(MerknadCode.GRADERT_UTTAK);
            return;
        }

        if (!merknader.contains(MerknadCode.HELT_UTTAK)) {
            merknader.add(MerknadCode.HELT_UTTAK);
        }
    }
}
