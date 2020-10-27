package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.GrunnlagTypeCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EndringPensjonsopptjening {

    private final TypeArsakCode arsakType;
    private final LocalDate dato;
    private final Double endringBelop;
    private final Double pensjonsbeholdningBelop;
    private final Integer uttaksgrad;
    private final Double grunnlag;
    private List<DetailsArsakCode> arsaksDetails;
    private List<GrunnlagTypeCode> grunnlagsTypes;

    public EndringPensjonsopptjening(TypeArsakCode arsakType, LocalDate dato, Double endringBelop, Double pensjonsbeholdningBelop, Integer uttaksgrad, Double grunnlag) {
        this.arsakType = arsakType;
        this.dato = dato;
        this.endringBelop = endringBelop;
        this.pensjonsbeholdningBelop = pensjonsbeholdningBelop;
        this.uttaksgrad = uttaksgrad;
        this.grunnlag = grunnlag;
    }

    TypeArsakCode getArsakType() {
        return arsakType;
    }

    LocalDate getDato() {
        return dato;
    }

    Double getEndringBelop() {
        return endringBelop;
    }

    Double getPensjonsbeholdningBelop() {
        return pensjonsbeholdningBelop;
    }

    Integer getUttaksgrad() {
        return uttaksgrad;
    }

    Double getGrunnlag() {
        return grunnlag;
    }

    void setArsakDetails(List<DetailsArsakCode> arsaksDetails) {
        this.arsaksDetails = arsaksDetails;
    }

    void setGrunnlagTypes(List<GrunnlagTypeCode> grunnlagsTypes) {
        this.grunnlagsTypes = grunnlagsTypes;
    }

    List<DetailsArsakCode> getArsakDetails() {
        return arsaksDetails;
    }

    List<GrunnlagTypeCode> getGrunnlagsTypes() {
        return grunnlagsTypes;
    }

    void addDetailsToNyOpptjeningEndring(Integer givenYear) {
        arsaksDetails = new ArrayList<>();

        if (givenYear == 2011) {
            arsaksDetails.add(DetailsArsakCode.OPPTJENING_2011);
        }

        if (uttaksgrad == null) {
            return;
        }

        if (uttaksgrad > 0 && uttaksgrad < 100) {
            arsaksDetails.add(DetailsArsakCode.OPPTJENING_GRADERT);
        } else if (uttaksgrad == 100) {
            arsaksDetails.add(DetailsArsakCode.OPPTJENING_HEL);
        } else if (givenYear >= 2012) {
            arsaksDetails.add(DetailsArsakCode.OPPTJENING_2012);
        }
    }
}
