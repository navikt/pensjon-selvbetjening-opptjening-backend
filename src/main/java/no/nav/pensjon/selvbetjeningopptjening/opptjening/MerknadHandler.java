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

public class MerknadHandler {

    static void setMerknadOmsorgsopptjeningPensjonspoeng(OpptjeningDto opptjening, Pensjonspoeng pensjonspoeng) {
        if (opptjening.getMerknader().contains(MerknadCode.OMSORGSOPPTJENING)
                || pensjonspoeng.getOmsorg() == null) {
            return;
        }

        if (isOmsorgspoengLessThanOrEqualToPensjonspoeng(opptjening)) {
            opptjening.addMerknader(List.of(MerknadCode.OMSORGSOPPTJENING));
        }
    }

    static void setMerknadOverforOmsorgsopptjeningPensjonspoeng(OpptjeningDto opptjening, Pensjonspoeng pensjonspoeng) {
        if (opptjening.getMerknader().contains(MerknadCode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        if (isTypeOmsorgBarn(pensjonspoeng.getOmsorg())) {
            opptjening.addMerknader(List.of(MerknadCode.OVERFORE_OMSORGSOPPTJENING));
        }
    }

    static void addMerknaderOnOpptjening(int year, OpptjeningDto opptjening,
                                         List<Beholdning> beholdninger,
                                         List<Uttaksgrad> uttaksgradhistorikk,
                                         AfpHistorikk afpHistorikk,
                                         UforeHistorikk uforehistorikk) {
        List<MerknadCode> merknader = new ArrayList<>();
        addMerknadAfp(year, merknader, afpHistorikk);
        addMerknadUforegrad(year, uforehistorikk, opptjening, merknader);

        //When beholdninger is null the user is not in Usergroup 4 or 5 and these merknads do not apply
        if (beholdninger != null) {
            addMerknadReform2010(year, merknader);
            addMerknadDagpengerAndForstegangstjeneste(year, merknader, beholdninger);
            addMerknadOmsorgFromPensjonsbeholdning(year, opptjening, merknader, beholdninger);
        }

        addMerknadGradertAlderspensjon(year, uttaksgradhistorikk, merknader);
        addMerknadIngenOpptjening(opptjening, merknader);
        opptjening.addMerknader(merknader);
    }

    private static boolean isOmsorgspoengLessThanOrEqualToPensjonspoeng(OpptjeningDto opptjening) {
        return opptjening.getOmsorgspoeng() != null
                && opptjening.getPensjonspoeng() != null
                && opptjening.getOmsorgspoeng() <= opptjening.getPensjonspoeng();
    }

    private static void addMerknadAfp(int year, List<MerknadCode> merknader, AfpHistorikk afpHistorikk) {
        if (afpHistorikk == null) {
            return;
        }

        int firstYearToBeMarkedWithAfp = afpHistorikk.getVirkFom().getYear();
        int lastYearToBeMarkedWithAfp = afpHistorikk.getVirkTom() == null ? LocalDate.now().getYear() - 1 : afpHistorikk.getVirkTom().getYear();

        if (firstYearToBeMarkedWithAfp <= year && year <= lastYearToBeMarkedWithAfp) {
            merknader.add(MerknadCode.AFP);
        }
    }

    private static void addMerknadUforegrad(int year, UforeHistorikk uforehistorikk, OpptjeningDto opptjening, List<MerknadCode> merknader) {
        if (uforehistorikk == null) {
            return;
        }

        Integer maxUforegrad = getMaxUforegrad(year, uforehistorikk);

        if (maxUforegrad == null || maxUforegrad <= 0) {
            return;
        }

        opptjening.setMaksUforegrad(maxUforegrad);
        merknader.add(MerknadCode.UFOREGRAD);
    }

    private static Integer getMaxUforegrad(int year, UforeHistorikk uforehistorikk) {
        Integer maxUforegrad = null;

        for (Uforeperiode periode : uforehistorikk.getUforeperiodeListe()) {
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

    private static void addMerknadIngenOpptjening(OpptjeningDto opptjening, List<MerknadCode> merknader) {
        if (merknader.contains(MerknadCode.REFORM)) {
            return;
        }

        if (pensjonsgivendeInntektErNull(opptjening) &&
                pensjonsbeholdningErNull(opptjening) &&
                pensjonspoengErNull(opptjening)) {
            merknader.add(MerknadCode.INGEN_OPPTJENING);
        }
    }

    private static boolean pensjonsgivendeInntektErNull(OpptjeningDto opptjening) {
        return opptjening.getPensjonsgivendeInntekt() == null || opptjening.getPensjonsgivendeInntekt() <= 0;
    }

    private static boolean pensjonsbeholdningErNull(OpptjeningDto opptjening) {
        return opptjening.getPensjonsbeholdning() == null || opptjening.getPensjonsbeholdning() <= 0;
    }

    private static boolean pensjonspoengErNull(OpptjeningDto opptjening) {
        return opptjening.getPensjonspoeng() == null || opptjening.getPensjonspoeng() <= 0;
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
                && year == beholdning.getDagpengerOpptjeningBelop().getAr();
    }

    private static boolean mottattDagpengerFiskere(Beholdning beholdning) {
        DagpengerOpptjeningBelop belop = beholdning.getDagpengerOpptjeningBelop();

        return belop != null
                && belop.getBelopFiskere() != null
                && belop.getBelopFiskere() > 0;
    }

    private static boolean mottattDagpenger(Beholdning beholdning) {
        DagpengerOpptjeningBelop belop = beholdning.getDagpengerOpptjeningBelop();
        return belop != null && belop.getBelopOrdinar() > 0;
    }

    private static boolean mottattForstegangstjeneste(int year, Beholdning beholdning) {
        ForstegangstjenesteOpptjeningBelop belop = beholdning.getForstegangstjenesteOpptjeningBelop();

        return belop != null
                && belop.getBelop() > 0
                && belop.getAr() == year;
    }

    private static void addMerknadOmsorgFromPensjonsbeholdning(int year, OpptjeningDto opptjening, List<MerknadCode> merknader, List<Beholdning> beholdninger) {
        beholdninger
                .stream()
                .filter(beholdning -> hasOmsorgsopptjening(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> addOmsorgsopptjeningMerknad(opptjening, merknader));

        beholdninger
                .stream()
                .filter(beholdning -> hasOverforeOmsorgsopptjening(year, beholdning))
                .findFirst()
                .ifPresent(beholdning -> addOverforeOmsorgsopptjeningMerknad(opptjening, merknader));
    }

    private static boolean hasOmsorgsopptjening(int year, Beholdning beholdning) {
        OmsorgOpptjeningBelop belop = beholdning.getOmsorgOpptjeningBelop();

        return belop != null
                && belop.getBelop() != null
                && belop.getBelop() > 0
                && belop.getAr() == year;
    }

    private static boolean hasOverforeOmsorgsopptjening(int year, Beholdning beholdning) {
        OmsorgOpptjeningBelop belop = beholdning.getOmsorgOpptjeningBelop();
        return beholdningHasOpptjeningOmsorgBarn(belop) && belop.getAr() == year;
    }

    private static void addOmsorgsopptjeningMerknad(OpptjeningDto opptjening, List<MerknadCode> merknader) {
        if (opptjening.getMerknader().contains(MerknadCode.OMSORGSOPPTJENING)) {
            return;
        }

        merknader.add(MerknadCode.OMSORGSOPPTJENING);
    }

    private static void addOverforeOmsorgsopptjeningMerknad(OpptjeningDto opptjening, List<MerknadCode> merknader) {
        if (opptjening.getMerknader().contains(MerknadCode.OVERFORE_OMSORGSOPPTJENING)) {
            return;
        }

        merknader.add(MerknadCode.OVERFORE_OMSORGSOPPTJENING);
    }

    private static Boolean beholdningHasOpptjeningOmsorgBarn(OmsorgOpptjeningBelop belop) {
        if (belop == null) {
            return false;
        }

        return belop
                .getOmsorgListe()
                .stream()
                .anyMatch(MerknadHandler::isTypeOmsorgBarn);
    }

    private static boolean isTypeOmsorgBarn(Omsorg omsorg) {
        return omsorg != null && isTypeOmsorgBarn(omsorg.getOmsorgType());
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
