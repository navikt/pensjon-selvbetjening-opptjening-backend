package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.AfpHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Omsorg;
import no.nav.pensjon.selvbetjeningopptjening.model.Pensjonspoeng;
import no.nav.pensjon.selvbetjeningopptjening.model.UforeHistorikk;
import no.nav.pensjon.selvbetjeningopptjening.model.Uforeperiode;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.UforeTypeCode;

public class MerknadHandler {

    public void setMerknadOmsorgsopptjeningPensjonspoeng(OpptjeningDto opptjening, Pensjonspoeng pensjonspoeng) {
        if (!opptjening.getMerknader().contains(MerknadCode.OMSORGSOPPTJENING) && pensjonspoeng.getOmsorg() != null && isOmsorgspoengLessOrEqualThanPensjonspoeng(opptjening)) {
            opptjening.addMerknader(List.of(MerknadCode.OMSORGSOPPTJENING));
        }
    }

    private boolean isOmsorgspoengLessOrEqualThanPensjonspoeng(OpptjeningDto opptjening) {
        return opptjening.getOmsorgspoeng() != null && opptjening.getPensjonspoeng() != null && opptjening.getPensjonspoeng() >= opptjening.getOmsorgspoeng();
    }

    public void setMerknadOverforOmsorgsopptjeningPensjonspoeng(OpptjeningDto opptjening, Pensjonspoeng pensjonspoeng) {
        if (!opptjening.getMerknader().contains(MerknadCode.OVERFORE_OMSORGSOPPTJENING) && isOmsorgspoengTypeOBU7OrOBU6(pensjonspoeng.getOmsorg())) {
            opptjening.addMerknader(List.of(MerknadCode.OVERFORE_OMSORGSOPPTJENING));
        }
    }

    private boolean isOmsorgspoengTypeOBU7OrOBU6(Omsorg omsorg) {
        return omsorg != null && ("OBU6".equals(omsorg.getOmsorgType()) || "OBU7".equals(omsorg.getOmsorgType()));
    }

    public void addMerknaderOnOpptjening(int year, OpptjeningDto opptjening, List<Beholdning> pensjonsbeholdningList, List<Uttaksgrad> uttaksgradhistorikk,
            AfpHistorikk afpHistorikk, UforeHistorikk uforehistorikk) {

        List<MerknadCode> merknadList = new ArrayList<>();

        addMerknadAFP(year, merknadList, afpHistorikk);
        addMerknadUforegrad(year, uforehistorikk, opptjening, merknadList);

        //When pensjonsbeholdningList is null the user is not in Usergroup 4 or 5 and these merknads do not apply
        if (pensjonsbeholdningList != null) {
            addMerknadReform2010(year, merknadList);
            addMerknadDagpengerAndForstegangsteneste(year, merknadList, pensjonsbeholdningList);
            addMerknadOmsorgFromPensjonsbeholdning(year, opptjening, merknadList, pensjonsbeholdningList);
        }

        addMerknadGradertAlderspensjon(year, uttaksgradhistorikk, merknadList);
        addMerknadIngenOpptjening(opptjening, merknadList);
        opptjening.addMerknader(merknadList);
    }

    private void addMerknadAFP(int year, List<MerknadCode> merknadList, AfpHistorikk afpHistorikk) {
        if (afpHistorikk != null) {
            int firstYearToBeMarkedWithAfp = afpHistorikk.getVirkFom().getYear();
            int lastYearToBeMarkedWithAFP = afpHistorikk.getVirkTom() == null ? LocalDate.now().getYear() - 1 : afpHistorikk.getVirkTom().getYear();

            if (firstYearToBeMarkedWithAfp <= year && year <= lastYearToBeMarkedWithAFP) {
                merknadList.add(MerknadCode.AFP);
            }
        }
    }

    private void addMerknadUforegrad(int year, UforeHistorikk uforehistorikk, OpptjeningDto opptjening, List<MerknadCode> merknadList) {
        if (uforehistorikk != null) {
            Integer maxUforegrad = null;

            for (Uforeperiode periode : uforehistorikk.getUforeperiodeListe()) {
                if (isRealUforeperiode(periode) && (periode.getUforetype().equals(UforeTypeCode.UFORE) || periode.getUforetype().equals(UforeTypeCode.UF_M_YRKE))) {
                    if (isUforeperiodeVirkFomBeforeGrunnlagsAr(year, periode)) {
                        if (maxUforegrad == null || periode.getUforegrad() > maxUforegrad) {
                            maxUforegrad = periode.getUforegrad();
                        }
                    }
                }
            }
            if (maxUforegrad != null && maxUforegrad > 0) {
                opptjening.setMaksUforegrad(maxUforegrad);
                merknadList.add(MerknadCode.UFOREGRAD);
            }
        }
    }

    private boolean isUforeperiodeVirkFomBeforeGrunnlagsAr(int grunnlagsar, Uforeperiode periode) {
        LocalDate firstDayInGrunnlagsar = LocalDate.of(grunnlagsar, 1, 1);
        LocalDate virkFom = periode.getUfgFom();

        return virkFom.getYear() == grunnlagsar ||
                isDateBeforeOrEqual(periode.getUfgFom(), firstDayInGrunnlagsar) &&
                        (periode.getUfgTom() == null || grunnlagsArBeforeUforeTom(firstDayInGrunnlagsar, periode.getUfgTom()));
    }

    private boolean grunnlagsArBeforeUforeTom(LocalDate firstDayInGrunnlagsar, LocalDate tom) {
        return firstDayInGrunnlagsar.getYear() == tom.getYear() || isDateBeforeOrEqual(firstDayInGrunnlagsar, tom);
    }

    private boolean isDateBeforeOrEqual(LocalDate date, LocalDate otherDate) {
        return date.isBefore(otherDate) || date.isEqual(otherDate);
    }

    private boolean isRealUforeperiode(Uforeperiode uforeperiode) {
        UforeTypeCode uforeType = uforeperiode.getUforetype();
        return uforeType != null && (uforeType.equals(UforeTypeCode.UF_M_YRKE) || uforeType.equals(UforeTypeCode.UFORE) || uforeType.equals(UforeTypeCode.YRKE));
    }

    private void addMerknadReform2010(int year, List<MerknadCode> merknadList) {
        if (year == REFORM_2010) {
            merknadList.add(MerknadCode.REFORM);
        }
    }

    private void addMerknadIngenOpptjening(OpptjeningDto opptjening, List<MerknadCode> merknadList) {
        if (pensjonsgivendeInntektErNull(opptjening) && pensjonsbeholdningErNull(opptjening)
                && pensjonspoengErNull(opptjening) && !merknadList.contains(MerknadCode.REFORM)) {
            merknadList.add(MerknadCode.INGEN_OPPTJENING);
        }
    }

    private boolean pensjonsgivendeInntektErNull(OpptjeningDto opptjening) {
        return opptjening.getPensjonsgivendeInntekt() == null
                || opptjening.getPensjonsgivendeInntekt() <= 0;
    }

    private boolean pensjonsbeholdningErNull(OpptjeningDto opptjening) {
        return opptjening.getPensjonsbeholdning() == null || opptjening.getPensjonsbeholdning() <= 0;
    }

    private boolean pensjonspoengErNull(OpptjeningDto opptjening) {
        return opptjening.getPensjonspoeng() == null || opptjening.getPensjonspoeng() <= 0;
    }

    private void addMerknadDagpengerAndForstegangsteneste(int year, List<MerknadCode> merknadList, List<Beholdning> pensjonsbeholdningList) {
        pensjonsbeholdningList.stream().filter(beholdning ->
                (mottattDagpengerFiskere(beholdning) || mottattDagpenger(beholdning)) && year == beholdning.getDagpengerOpptjeningBelop().getAr())
                .findFirst().ifPresent(beholdning -> merknadList.add(MerknadCode.DAGPENGER));

        pensjonsbeholdningList.stream().filter(beholdning ->
                mottattForstegangstjenesteBelop(beholdning) && year == beholdning.getForstegangstjenesteOpptjeningBelop().getAr())
                .findFirst().ifPresent(beholdning -> merknadList.add(MerknadCode.FORSTEGANGSTJENESTE));
    }

    private boolean mottattDagpengerFiskere(Beholdning beholdning) {
        return beholdning.getDagpengerOpptjeningBelop() != null
                && beholdning.getDagpengerOpptjeningBelop().getBelopFiskere() != null
                && beholdning.getDagpengerOpptjeningBelop().getBelopFiskere() > 0;
    }

    private boolean mottattDagpenger(Beholdning beholdning) {
        return beholdning.getDagpengerOpptjeningBelop() != null && beholdning.getDagpengerOpptjeningBelop().getBelopOrdinar() > 0;
    }

    private boolean mottattForstegangstjenesteBelop(Beholdning beholdning) {
        return beholdning.getForstegangstjenesteOpptjeningBelop() != null
                && beholdning.getForstegangstjenesteOpptjeningBelop().getBelop() > 0;
    }

    private void addMerknadOmsorgFromPensjonsbeholdning(int year, OpptjeningDto opptjening, List<MerknadCode> merknadList, List<Beholdning> pensjonsbeholdningList) {
        pensjonsbeholdningList.stream()
                .filter(beholdning -> omsorgopptjeningsbelopGreaterThanZero(beholdning) && year == beholdning.getOmsorgOpptjeningBelop().getAr())
                .findFirst().ifPresent(beholdning -> {
            if (!opptjening.getMerknader().contains(MerknadCode.OMSORGSOPPTJENING)) {
                merknadList.add(MerknadCode.OMSORGSOPPTJENING);
            }
        });

        pensjonsbeholdningList.stream()
                .filter(beholdning -> beholdningHarOpptjeningOBU7EllerOBU6(beholdning) && year == beholdning.getOmsorgOpptjeningBelop().getAr())
                .findFirst().ifPresent(beholdning -> {
            if (!opptjening.getMerknader().contains(MerknadCode.OVERFORE_OMSORGSOPPTJENING)) {
                merknadList.add(MerknadCode.OVERFORE_OMSORGSOPPTJENING);
            }
        });
    }

    private boolean omsorgopptjeningsbelopGreaterThanZero(Beholdning beholdning) {
        return beholdning.getOmsorgOpptjeningBelop() != null && beholdning.getOmsorgOpptjeningBelop().getBelop() != null
                && beholdning.getOmsorgOpptjeningBelop().getBelop() > 0;
    }

    private Boolean beholdningHarOpptjeningOBU7EllerOBU6(Beholdning beholdning) {
        if (beholdning.getOmsorgOpptjeningBelop() != null) {
            for (Omsorg omsorg : beholdning.getOmsorgOpptjeningBelop().getOmsorgListe()) {
                if ("OBU7".equals(omsorg.getOmsorgType()) || "OBU6".equals(omsorg.getOmsorgType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addMerknadGradertAlderspensjon(int year, List<Uttaksgrad> uttaksgradList, List<MerknadCode> merknadList) {
        uttaksgradList.stream()
                .filter(uttaksgrad -> year >= uttaksgrad.getFomDato().getYear() && (uttaksgrad.getTomDato() != null && year <= uttaksgrad.getTomDato().getYear()))
                .forEach(uttaksgrad -> {
                    if (uttaksgrad.getUttaksgrad() < 100 && uttaksgrad.getUttaksgrad() > 0 && !merknadList.contains(MerknadCode.GRADERT_UTTAK)) {
                        merknadList.add(MerknadCode.GRADERT_UTTAK);
                    } else if (!merknadList.contains(MerknadCode.HELT_UTTAK)) {
                        merknadList.add(MerknadCode.HELT_UTTAK);
                    }
                });
    }
}
