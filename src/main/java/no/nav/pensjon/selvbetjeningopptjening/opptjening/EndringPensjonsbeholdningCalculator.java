package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import static no.nav.pensjon.selvbetjeningopptjening.util.Constants.REFORM_2010;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import no.nav.pensjon.selvbetjeningopptjening.model.Beholdning;
import no.nav.pensjon.selvbetjeningopptjening.model.Uttaksgrad;
import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode;

public class EndringPensjonsbeholdningCalculator {

    private LocalDate dec31PreviousYear;
    private LocalDate jan1GivenYear;
    private LocalDate april30GivenYear;
    private LocalDate may1GivenYear;
    private LocalDate dec31GivenYear;

    public List<EndringPensjonsopptjeningDto> calculateEndringPensjonsbeholdning(int year, List<Beholdning> beholdningList, List<Uttaksgrad> uttaksgradList) {
        List<Beholdning> sortedBeholdningList = sortBeholdningList(beholdningList);

        jan1GivenYear = LocalDate.of(year, Month.JANUARY, 1);
        april30GivenYear = LocalDate.of(year, Month.APRIL, 30);
        may1GivenYear = LocalDate.of(year, Month.MAY, 1);
        dec31GivenYear = LocalDate.of(year, Month.DECEMBER, 31);
        dec31PreviousYear = LocalDate.of(year - 1, Month.DECEMBER, 31);

        if (!sortedBeholdningList.isEmpty()) {
            return createPensjonsbeholdningEndringListe(sortedBeholdningList, year, uttaksgradList);
        }

        return null;
    }

    private List<Beholdning> sortBeholdningList(List<Beholdning> unsortedBeholdningList) {
        List<Beholdning> beholdningListCopy = new ArrayList<>(unsortedBeholdningList);

        if (beholdningListCopy.size() > 1) {
            beholdningListCopy.sort((b1, b2) -> {
                LocalDate date1 = b1.getFomDato();
                LocalDate date2 = b2.getFomDato();
                return date1.compareTo(date2);
            });
        }

        return beholdningListCopy;
    }

    private List<EndringPensjonsopptjeningDto> createPensjonsbeholdningEndringListe(List<Beholdning> sortedBeholdningList, Integer givenYear, List<Uttaksgrad> uttaksgradList) {
        List<EndringPensjonsopptjeningDto> endringList = new ArrayList<>();

        Beholdning forrigeBeholdning = addInngaendeBeholdning(sortedBeholdningList, endringList, uttaksgradList, givenYear);
        forrigeBeholdning = addNyOpptjening(sortedBeholdningList, endringList, forrigeBeholdning, givenYear, uttaksgradList);
        forrigeBeholdning = addChangesUttaksgradBeforeReguleringAtMay1th(sortedBeholdningList, endringList, forrigeBeholdning, uttaksgradList);
        forrigeBeholdning = addRegulering(sortedBeholdningList, endringList, forrigeBeholdning, uttaksgradList, givenYear);
        addChangesUttaksgradAfterReguleringAtMay1th(sortedBeholdningList, endringList, forrigeBeholdning, uttaksgradList);
        addUtgaendeBeholdning(sortedBeholdningList, endringList, uttaksgradList);

        return endringList;
    }

    private Beholdning addInngaendeBeholdning(List<Beholdning> beholdningList, List<EndringPensjonsopptjeningDto> endringList, List<Uttaksgrad> uttaksgradList, Integer givenYear) {
        Double pensjonsbeholdningBelop = 0.0;
        Beholdning beholdningUsed = null;
        Integer uttaksgrad = 0;

        for (Beholdning beholdning : beholdningList) {
            if (beholdning.getTomDato() != null && beholdning.getTomDato().isEqual(dec31PreviousYear)) {
                pensjonsbeholdningBelop = beholdning.getBelop();
                beholdningUsed = beholdning;
                uttaksgrad = fetchUttaksgrad(dec31PreviousYear, beholdningUsed, uttaksgradList);
            }
        }
        EndringPensjonsopptjeningDto endring = createEndring(TypeArsakCode.INNGAENDE, dec31PreviousYear, null, pensjonsbeholdningBelop, uttaksgrad);

        if (givenYear == REFORM_2010) {
            endring.setArsakDetails(Arrays.asList(DetailsArsakCode.BEHOLDNING_2010));
        }

        endringList.add(endring);

        return beholdningUsed;
    }

    private EndringPensjonsopptjeningDto createEndring(TypeArsakCode arsakType, LocalDate dato, Double endringBelop,
            Double pensjonsbeholdningBelop, Integer uttaksgrad) {
        EndringPensjonsopptjeningDto endring = new EndringPensjonsopptjeningDto();
        endring.setArsakType(arsakType);
        endring.setDato(dato);
        endring.setEndringBelop(endringBelop);
        endring.setPensjonsbeholdningBelop(pensjonsbeholdningBelop);
        endring.setUttaksgrad(uttaksgrad);
        return endring;
    }

    private Integer fetchUttaksgrad(LocalDate date, Beholdning beholdning, List<Uttaksgrad> uttaksgradList) {
        Integer defaultUttaksgrad = 0;

        if (beholdning == null || beholdning.getVedtakId() == null) {
            return defaultUttaksgrad;
        }
        Optional<Uttaksgrad> uttaksgradForBeholdning = uttaksgradList.stream()
                .filter(uttaksgrad -> uttaksgrad.getVedtakId().equals(beholdning.getVedtakId()))
                .filter(uttaksgrad -> isDateInPeriod(date, uttaksgrad.getFomDato(), uttaksgrad.getTomDato()))
                .findFirst();

        return uttaksgradForBeholdning.isPresent() ? uttaksgradForBeholdning.get().getUttaksgrad() : defaultUttaksgrad;
    }

    private Beholdning addNyOpptjening(List<Beholdning> beholdningListe, List<EndringPensjonsopptjeningDto> endringListe,
            Beholdning forrigeBeholdning, Integer givenYear, List<Uttaksgrad> uttaksgradList) {
        Double inngaendeBelop = endringListe.get(0).getPensjonsbeholdningBelop();
        Beholdning beholdning = fetchOpptjeningsBeholdning(beholdningListe);
        if (beholdning != null) {
            Double innskudd = 0.0;
            /*
             * Det kan hende at ny opptjening ennå ikke har blitt oppdatert, men det eksisterer likevel en beholdning 01.01 hvis
             * har endret uttaksgrad.
             */
            if (beholdning.getBeholdningInnskudd() != null) {
                innskudd = beholdning.getBeholdningInnskudd();
                if (givenYear.equals(2010) && beholdning.getLonnsvekstregulering() != null
                        && beholdning.getLonnsvekstregulering().getReguleringsbelop() != null) {
                    innskudd += beholdning.getLonnsvekstregulering().getReguleringsbelop();
                }
            }

            Double pensjonsbeholdningBelop = inngaendeBelop + innskudd;

            TypeArsakCode arsakTypeForNyOpptjening = givenYear == REFORM_2010 ? TypeArsakCode.INNGAENDE_2010 : TypeArsakCode.OPPTJENING;
            if (Math.floor(beholdning.getBelop()) == (Math.floor(inngaendeBelop + innskudd))) {
                EndringPensjonsopptjeningDto endringNyOpptjening =
                        createEndring(arsakTypeForNyOpptjening, jan1GivenYear, innskudd, pensjonsbeholdningBelop, fetchUttaksgrad(jan1GivenYear, beholdning, uttaksgradList));

                addDetailsToNyOpptjeningEndring(endringNyOpptjening, givenYear);
                endringListe.add(endringNyOpptjening);
            } else {
                EndringPensjonsopptjeningDto endringNyOpptjening = createEndring(arsakTypeForNyOpptjening, jan1GivenYear, innskudd, pensjonsbeholdningBelop,
                        fetchUttaksgrad(dec31PreviousYear, beholdning, uttaksgradList));

                addDetailsToNyOpptjeningEndring(endringNyOpptjening, givenYear);
                endringListe.add(endringNyOpptjening);

                EndringPensjonsopptjeningDto endringUttak = createEndring(TypeArsakCode.UTTAK, jan1GivenYear, beholdning.getBelop() - pensjonsbeholdningBelop,
                        beholdning.getBelop(), fetchUttaksgrad(jan1GivenYear, beholdning, uttaksgradList));

                endringUttak.setArsakDetails(Arrays.asList(DetailsArsakCode.UTTAK));
                endringListe.add(endringUttak);
            }
            forrigeBeholdning = beholdning;
        }
        return forrigeBeholdning;
    }

    /**
     * Retrieves the beholdning which contains the update data due ny opptjening.
     *
     * @param beholdningListe the users beholdingliste for the given year.
     * @return the Beholdning object with fomDato january the first the given year.
     */
    private Beholdning fetchOpptjeningsBeholdning(List<Beholdning> beholdningListe) {
        Beholdning opptjeningsBeholdning = null;
        for (Beholdning beholdning : beholdningListe) {
            if (beholdning.getFomDato().isEqual(jan1GivenYear)) {
                opptjeningsBeholdning = beholdning;
            }
        }
        return opptjeningsBeholdning;
    }

    private void addDetailsToNyOpptjeningEndring(EndringPensjonsopptjeningDto endring, Integer givenYear) {
        List<DetailsArsakCode> arsakDetailsList = new ArrayList<>();
        Integer uttaksgrad = endring.getUttaksgrad();
        if (givenYear == 2011) {
            arsakDetailsList.add(DetailsArsakCode.OPPTJENING_2011);
        }
        if (uttaksgrad != null) {
            if (uttaksgrad > 0 && uttaksgrad < 100) {
                arsakDetailsList.add(DetailsArsakCode.OPPTJENING_GRADERT);
            } else if (uttaksgrad == 100) {
                arsakDetailsList.add(DetailsArsakCode.OPPTJENING_HEL);
            } else if (givenYear >= 2012) {
                arsakDetailsList.add(DetailsArsakCode.OPPTJENING_2012);
            }
        }

        endring.setArsakDetails(arsakDetailsList);
    }

    /**
     * Adding the changes on beholdning due to changes on uttaksgrad, between january the 1st and 31st of april, to the
     * PensjonsbeholdningEndring list, if uttaksgradchanges exists.
     *
     * @param beholdningListe   the sorted beholdningListe
     * @param endringListe      the PensjonsbeholdningEndring
     * @param forrigeBeholdning The previous beholdning
     * @return forrigeBeholdning
     */
    private Beholdning addChangesUttaksgradBeforeReguleringAtMay1th(List<Beholdning> beholdningListe,
            List<EndringPensjonsopptjeningDto> endringListe, Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgradList) {
        Double previousBeholdningBelop = 0.0;

        for (Beholdning beholdning : beholdningListe) {
            if (beholdning.getFomDato().isEqual(jan1GivenYear)) {
                previousBeholdningBelop = beholdning.getBelop();
                forrigeBeholdning = beholdning;
            } else if (isBeholdningWithinPeriode(beholdning, jan1GivenYear, may1GivenYear)) {
                EndringPensjonsopptjeningDto endringUttak = createEndring(TypeArsakCode.UTTAK, beholdning.getFomDato(),
                        beholdning.getBelop() - previousBeholdningBelop, beholdning.getBelop(), fetchUttaksgrad(beholdning.getFomDato(), beholdning, uttaksgradList));

                endringUttak.setArsakDetails(Arrays.asList(DetailsArsakCode.UTTAK));
                endringListe.add(endringUttak);

                previousBeholdningBelop = beholdning.getBelop();
                forrigeBeholdning = beholdning;
            }
        }
        return forrigeBeholdning;
    }

    private boolean isBeholdningWithinPeriode(Beholdning beholdning, LocalDate start, LocalDate end) {
        return beholdning.getFomDato().isAfter(start)
                && (beholdning.getTomDato() == null && end.isAfter(beholdning.getFomDato())
                || (beholdning.getTomDato() != null && beholdning.getTomDato().isBefore(end)));
    }

    private Beholdning addRegulering(List<Beholdning> beholdningListe, List<EndringPensjonsopptjeningDto> endringListe,
            Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgradList, Integer givenYear) {
        Double tempBelop = endringListe.get(endringListe.size() - 1).getPensjonsbeholdningBelop();

        for (Beholdning beholdning : beholdningListe) {
            if (beholdning.getFomDato().isEqual(may1GivenYear)) {
                LocalDate gyldigPaDato;
                Double vedtakPensjonseringsBelop = null;
                if (beholdning.getLonnsvekstregulering() != null
                        && beholdning.getLonnsvekstregulering().getReguleringsbelop() != null) {
                    vedtakPensjonseringsBelop = beholdning.getLonnsvekstregulering().getReguleringsbelop() + tempBelop;

                    if (!beholdning.getBelop().equals(vedtakPensjonseringsBelop)) {
                        gyldigPaDato = april30GivenYear;
                    } else {
                        gyldigPaDato = may1GivenYear;
                    }

                    EndringPensjonsopptjeningDto endringRegulering = createEndring(TypeArsakCode.REGULERING, may1GivenYear, beholdning.getLonnsvekstregulering()
                            .getReguleringsbelop(), vedtakPensjonseringsBelop, fetchUttaksgrad(gyldigPaDato, beholdning, uttaksgradList));

                    addDetailsToReguleringEndring(endringRegulering, givenYear);
                    endringListe.add(endringRegulering);

                    forrigeBeholdning = beholdning;
                }

                if (!(Math.floor(beholdning.getBelop()) == (Math.floor(vedtakPensjonseringsBelop)))) {
                    Double belop = beholdning.getBelop();
                    if (vedtakPensjonseringsBelop != null) {
                        belop -= vedtakPensjonseringsBelop;
                    }
                    EndringPensjonsopptjeningDto endringUttak = createEndring(TypeArsakCode.UTTAK, may1GivenYear, belop, beholdning.getBelop(), fetchUttaksgrad(
                            may1GivenYear, beholdning, uttaksgradList));

                    endringUttak.setArsakDetails(Arrays.asList(DetailsArsakCode.UTTAK));
                    endringListe.add(endringUttak);

                    forrigeBeholdning = beholdning;
                }
            }
        }
        return forrigeBeholdning;
    }

    private void addDetailsToReguleringEndring(EndringPensjonsopptjeningDto endring, Integer givenYear) {
        if (givenYear == REFORM_2010) {
            endring.setArsakDetails(Arrays.asList(DetailsArsakCode.REGULERING_2010));
        } else if (givenYear > REFORM_2010) {
            endring.setArsakDetails(Arrays.asList(DetailsArsakCode.REGULERING));
        }
    }

    /**
     * Adding the changes on beholdning due to changes on uttaksgrad, between may the 1st and 31st of december, to the
     * PensjonsbeholdningEndring list, if uttaksgradchanges exists.
     *
     * @param beholdningListe   the sorted beholdningListe
     * @param endringListe      the PensjonsbeholdningEndring
     * @param forrigeBeholdning the previous beholdning
     */
    private void addChangesUttaksgradAfterReguleringAtMay1th(List<Beholdning> beholdningListe,
            List<EndringPensjonsopptjeningDto> endringListe, Beholdning forrigeBeholdning, List<Uttaksgrad> uttaksgradList) {
        Beholdning prevBeholdning = forrigeBeholdning;

        for (Beholdning beholdning : beholdningListe) {
            if (beholdning.getFomDato().isEqual(may1GivenYear)) {
                prevBeholdning = beholdning;
            } else if (isBeholdningWithinPeriode(beholdning, may1GivenYear, dec31GivenYear)) {
                Double prevBelop = prevBeholdning != null ? prevBeholdning.getBelop() : 0D;
                Double endringBelop = beholdning.getBelop() - prevBelop;

                EndringPensjonsopptjeningDto endringUttak = createEndring(TypeArsakCode.UTTAK, beholdning.getFomDato(), endringBelop, beholdning.getBelop(),
                        fetchUttaksgrad(beholdning.getFomDato(), beholdning, uttaksgradList));
                endringUttak.setArsakDetails(Arrays.asList(DetailsArsakCode.UTTAK));
                endringListe.add(endringUttak);

                prevBeholdning = beholdning;
            }
        }
    }

    private void addUtgaendeBeholdning(List<Beholdning> sortedBeholdningListe, List<EndringPensjonsopptjeningDto> endringListe, List<Uttaksgrad> sakLuttaksgradList) {
        Beholdning lastBeholdning = sortedBeholdningListe.get(sortedBeholdningListe.size() - 1);
        if (lastBeholdning.getTomDato() != null && lastBeholdning.getTomDato().isEqual(dec31GivenYear)) {
            endringListe.add(createEndring(TypeArsakCode.UTGAENDE, dec31GivenYear, null, lastBeholdning.getBelop(), fetchUttaksgrad(
                    dec31GivenYear, lastBeholdning, sakLuttaksgradList)));
        }
    }

    private boolean isDateInPeriod(LocalDate date, LocalDate periodFom, LocalDate periodTom) {
        return (date.isAfter(periodFom) && (periodTom == null || date.isBefore(periodTom))) || date.isEqual(periodFom) || (periodTom != null && date.isEqual(periodTom));
    }
}
