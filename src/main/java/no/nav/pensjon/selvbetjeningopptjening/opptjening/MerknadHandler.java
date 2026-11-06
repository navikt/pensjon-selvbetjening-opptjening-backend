package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.merknad.Merknadkode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.opptjening.OmsorgTypes.OMSORG_BARN_UNDER_6;
import static no.nav.pensjon.selvbetjeningopptjening.opptjening.OmsorgTypes.OMSORG_BARN_UNDER_7;
import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

public class MerknadHandler {

    static void setMerknadOmsorgsopptjeningPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng) {
        if (opptjening.hasMerknad(Merknadkode.OMSORGSOPPTJENING) || !pensjonspoeng.hasOmsorg()) {
            return;
        }
        opptjening.addMerknad(Merknadkode.OMSORGSOPPTJENING);
    }

    static void setMerknadOverforOmsorgsopptjeningPensjonspoeng(Opptjening opptjening, Pensjonspoeng pensjonspoeng, List<Uttaksgrad> uttaksgrader) {
        if (opptjening.hasMerknad(Merknadkode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        if (isTypeOmsorgBarn(pensjonspoeng.getOmsorg()) && (uttaksgrader == null || uttaksgrader.isEmpty() ||
                opptjening.isOmsorgspoengLessThanPensjonspoeng())) {
            opptjening.addMerknad(Merknadkode.OVERFORE_OMSORGSOPPTJENING);
        }
    }

    static void addMerknaderOnOpptjening(int year,
                                         Opptjening opptjening,
                                         List<Beholdning> beholdninger,
                                         List<Uttaksgrad> uttaksgrader,
                                         AfpHistorikk afpHistorikk,
                                         UforeHistorikk uforehistorikk) {
        List<Merknadkode> merknader = new ArrayList<>();
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

    private static void addMerknadDagpengerAndForstegangstjeneste(int year, List<Merknadkode> merknader, List<Beholdning> beholdninger) {
        beholdninger
                .stream()
                .filter(beholdning -> mottattDagpenger(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> merknader.add(Merknadkode.DAGPENGER));

        beholdninger
                .stream()
                .filter(beholdning -> mottattForstegangstjeneste(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> merknader.add(Merknadkode.FORSTEGANGSTJENESTE));
    }

    public static boolean mottattDagpenger(int year, Beholdning beholdning) {
        return (mottattDagpengerFiskere(beholdning) || mottattDagpenger(beholdning))
                && year == beholdning.getDagpengeopptjening().getAar();
    }

    private static boolean mottattDagpengerFiskere(Beholdning beholdning) {
        Dagpengeopptjening belop = beholdning.getDagpengeopptjening();

        return belop != null && belop.getFiskerBeloep() > 0;
    }

    private static boolean mottattDagpenger(Beholdning beholdning) {
        Dagpengeopptjening belop = beholdning.getDagpengeopptjening();
        return belop != null && belop.getOrdinaertBeloep() > 0;
    }

    public static boolean mottattForstegangstjeneste(int year, Beholdning beholdning) {
        Foerstegangstjenesteopptjening belop = beholdning.getFoerstegangstjenesteopptjening();

        return belop != null
                && belop.getBeloep() > 0
                && belop.getAar() == year;
    }

    private static void addMerknadAfp(int year, List<Merknadkode> merknader, AfpHistorikk afpHistorikk) {
        if (afpHistorikk == null) {
            return;
        }

        int firstYearToBeMarkedWithAfp = afpHistorikk.getStartYear();
        int lastYearToBeMarkedWithAfp = afpHistorikk.getEndYearOrDefault(() -> LocalDate.now().getYear() - 1);

        if (firstYearToBeMarkedWithAfp <= year && year <= lastYearToBeMarkedWithAfp) {
            merknader.add(Merknadkode.AFP);
        }
    }

    private static void addMerknadUforegrad(int year, UforeHistorikk uforehistorikk, Opptjening opptjening, List<Merknadkode> merknader) {
        if (uforehistorikk == null) {
            return;
        }

        Integer maxUforegrad = getMaxUforegrad(year, uforehistorikk);

        if (maxUforegrad == null || maxUforegrad <= 0) {
            return;
        }

        opptjening.setMaxUforegrad(maxUforegrad);
        merknader.add(Merknadkode.UFOREGRAD);
    }

    public static Integer getMaxUforegrad(int year, UforeHistorikk historikk) {
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

    private static void addMerknadReform2010(int year, List<Merknadkode> merknader) {
        if (year == REFORM_2010) {
            merknader.add(Merknadkode.REFORM);
        }
    }

    private static void addMerknadIngenOpptjening(Opptjening opptjening, List<Merknadkode> merknader) {
        if (merknader.contains(Merknadkode.REFORM)) {
            return;
        }

        if (opptjening.isNotPositive()) {
            merknader.add(Merknadkode.INGEN_OPPTJENING);
        }
    }

    private static void addMerknadOmsorgFromPensjonsbeholdning(int year,
                                                               Opptjening opptjening,
                                                               List<Merknadkode> merknader,
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

    public static boolean hasOmsorgsopptjening(int year, Beholdning beholdning) {
        Omsorgsopptjening opptjening = beholdning.getOmsorgsopptjening();

        return opptjening != null
                && opptjening.getBeloep() > 0
                && opptjening.getAar() == year;
    }

    private static boolean hasOverforeOmsorgsopptjening(int year, Beholdning beholdning) {
        Omsorgsopptjening opptjening = beholdning.getOmsorgsopptjening();
        return beholdningHasOpptjeningOmsorgBarn(opptjening) && opptjening.getAar() == year;
    }

    private static void addOmsorgsopptjeningMerknad(Opptjening opptjening, List<Merknadkode> merknader) {
        if (opptjening.hasMerknad(Merknadkode.OMSORGSOPPTJENING)) {
            return;
        }

        merknader.add(Merknadkode.OMSORGSOPPTJENING);
    }

    private static void addOverforeOmsorgsopptjeningMerknad(Opptjening opptjening, List<Merknadkode> merknader) {
        if (opptjening.hasMerknad(Merknadkode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        merknader.add(Merknadkode.OVERFORE_OMSORGSOPPTJENING);
    }

    private static Boolean beholdningHasOpptjeningOmsorgBarn(Omsorgsopptjening opptjening) {
        if (opptjening == null) {
            return false;
        }

        return opptjening
                .getOmsorgListe()
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

    private static void addMerknadGradertAlderspensjon(int year, List<Uttaksgrad> uttaksgrader, List<Merknadkode> merknader) {
        uttaksgrader
                .stream()
                .filter(uttaksgrad -> uttaksgrad.dekkerAaret(year))
                .forEach(uttaksgrad -> addUttakMerknad(merknader, uttaksgrad));
    }

    private static void addUttakMerknad(List<Merknadkode> merknader, Uttaksgrad uttaksgrad) {
        if (uttaksgrad.getErGradert() && !merknader.contains(Merknadkode.GRADERT_UTTAK)) {
            merknader.add(Merknadkode.GRADERT_UTTAK);
            return;
        }

        if (!merknader.contains(Merknadkode.HELT_UTTAK)) {
            merknader.add(Merknadkode.HELT_UTTAK);
        }
    }
}
