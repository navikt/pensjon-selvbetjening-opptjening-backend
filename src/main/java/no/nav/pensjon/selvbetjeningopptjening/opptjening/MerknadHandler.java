package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.opptjening.OmsorgTypes.OMSORG_BARN_UNDER_6;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.OmsorgTypes.OMSORG_BARN_UNDER_7;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

public class MerknadHandler {

    static void setMerknadOmsorgsopptjeningPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng) {
        if (opptjening.hasMerknad(MerknadCode.OMSORGSOPPTJENING) || !pensjonspoeng.hasOmsorg()) {
            return;
        }
        opptjening.addMerknad(MerknadCode.OMSORGSOPPTJENING);
    }

    static void setMerknadOverforOmsorgsopptjeningPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng, List<Uttaksgrad> uttaksgrader) {
        if (opptjening.hasMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        if (isTypeOmsorgBarn(pensjonspoeng.getOmsorg()) && (uttaksgrader == null || uttaksgrader.isEmpty() ||
                opptjening.isOmsorgspoengLessThanPensjonspoeng())) {
            opptjening.addMerknad(MerknadCode.OVERFORE_OMSORGSOPPTJENING);
        }
    }

    static void addMerknaderOnOpptjening(int year,
                                         Opptjening opptjening,
                                         List<Beholdning> beholdninger,
                                         List<Uttaksgrad> uttaksgrader,
                                         AfpHistorikk afpHistorikk,
                                         UforeHistorikk uforehistorikk) {
        List<MerknadCode> merknader = new ArrayList<>();
        addMerknadAfp(year, merknader, afpHistorikk);
        addMerknadUforegrad(year, uforehistorikk, opptjening, merknader);

        //When beholdninger is null the user is not in Usergroup 4 or 5 and these merknads do not apply
        if (beholdninger != null) {
            addMerknadReform2010(year, merknader);
            addMerknadOmsorgFromPensjonsbeholdning(year, opptjening, merknader, beholdninger, uttaksgrader);
            addMerknadDagpengerAndForstegangstjeneste(year, merknader, beholdninger);
        }

        //addMerknadGradertAlderspensjon(year, uttaksgradhistorikk, merknader);
        addMerknadIngenOpptjening(opptjening, merknader);
        opptjening.addMerknader(merknader);
    }

    private static void addMerknadDagpengerAndForstegangstjeneste(int year, List<MerknadCode> merknader, List<Beholdning> beholdninger) {
        beholdninger
                .stream()
                .filter(beholdning -> mottattDagpenger(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> merknader.add(MerknadCode.DAGPENGER));

        beholdninger
                .stream()
                .filter(beholdning -> mottattForstegangstjeneste(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> merknader.add(MerknadCode.FORSTEGANGSTJENESTE));
    }

    private static boolean mottattDagpenger(int year, Beholdning beholdning) {
        return (mottattDagpengerFiskere(beholdning) || mottattDagpenger(beholdning))
                && year == beholdning.getDagpengeopptjening().getYear();
    }

    private static boolean mottattDagpengerFiskere(Beholdning beholdning) {
        Dagpengeopptjening belop = beholdning.getDagpengeopptjening();

        return belop != null && belop.getFiskerBelop() > 0;
    }

    private static boolean mottattDagpenger(Beholdning beholdning) {
        Dagpengeopptjening belop = beholdning.getDagpengeopptjening();
        return belop != null && belop.getOrdinartBelop() > 0;
    }

    private static boolean mottattForstegangstjeneste(int year, Beholdning beholdning) {
        Forstegangstjenesteopptjening belop = beholdning.getForstegangstjenesteopptjening();

        return belop != null
                && belop.getBelop() > 0
                && belop.getYear() == year;
    }

    private static void addMerknadAfp(int year, List<MerknadCode> merknader, AfpHistorikk afpHistorikk) {
        if (afpHistorikk == null) {
            return;
        }

        int firstYearToBeMarkedWithAfp = afpHistorikk.getStartYear();
        int lastYearToBeMarkedWithAfp = afpHistorikk.getEndYearOrDefault(() -> LocalDate.now().getYear() - 1);

        if (firstYearToBeMarkedWithAfp <= year && year <= lastYearToBeMarkedWithAfp) {
            merknader.add(MerknadCode.AFP);
        }
    }

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
            //TODO: isReal check seems superfluous in next line
            if (periode.isReal() && periode.isStrictReal() && periode.isVirkFomBeforeGrunnlagYear(year)) {
                if (maxUforegrad == null || periode.getUforegrad() > maxUforegrad) {
                    maxUforegrad = periode.getUforegrad();
                }
            }
        }

        return maxUforegrad;
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

    private static void addMerknadOmsorgFromPensjonsbeholdning(int year,
                                                               Opptjening opptjening,
                                                               List<MerknadCode> merknader,
                                                               List<Beholdning> beholdninger,
                                                               List<Uttaksgrad> uttaksgrader) {
        beholdninger
                .stream()
                .filter(beholdning -> hasOmsorgsopptjening(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> addOmsorgsopptjeningMerknad(opptjening, merknader));

        beholdninger
                .stream()
                .filter(beholdning -> hasOverforeOmsorgsopptjening(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> {
                    if (!beholdning.isOmsorgGrunnlagForBeholdning() || uttaksgrader == null || uttaksgrader.isEmpty())
                        addOverforeOmsorgsopptjeningMerknad(opptjening, merknader);
                });
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
                .filter(uttaksgrad -> uttaksgrad.coversYear(year))
                .forEach(uttaksgrad -> addUttakMerknad(merknader, uttaksgrad));
    }

    private static void addUttakMerknad(List<MerknadCode> merknader, Uttaksgrad uttaksgrad) {
        if (uttaksgrad.isGradert() && !merknader.contains(MerknadCode.GRADERT_UTTAK)) {
            merknader.add(MerknadCode.GRADERT_UTTAK);
            return;
        }

        if (!merknader.contains(MerknadCode.HELT_UTTAK)) {
            merknader.add(MerknadCode.HELT_UTTAK);
        }
    }
}
