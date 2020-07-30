package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import java.time.LocalDate;
import java.util.List;

import no.nav.pensjon.selvbetjeningopptjening.model.code.DetailsArsakCode;
import no.nav.pensjon.selvbetjeningopptjening.model.code.TypeArsakCode;

public class EndringPensjonsopptjeningDto {

    /**
     * Dato for endring av pensjonsbeholdning.
     */
    private LocalDate dato;


    private TypeArsakCode arsakType;

    /**
     * Årsak til endring av pensjonsbeholdningen.
     */
    private List<DetailsArsakCode> arsakDetails;

    /**
     * Endring (kronebeløp) i pensjonsbeholdningen i forhold til den forrige pensjonsbeholdningen.
     */
    private Double endringBelop;

    /**
     * Beløpet for pensjonsbeholdning for den gitte datoen.
     */
    private Double pensjonsbeholdningBelop;

    /**
     * Uttaksgrad for alderspensjonen ved den gitte datoen.
     */
    private Integer uttaksgrad;

    public LocalDate getDato() {
        return dato;
    }

    public void setDato(LocalDate dato) {
        this.dato = dato;
    }

    public List<DetailsArsakCode> getArsakDetails() {
        return arsakDetails;
    }

    public TypeArsakCode getArsakType() {
        return arsakType;
    }

    public void setArsakType(TypeArsakCode arsakType) {
        this.arsakType = arsakType;
    }

    public void setArsakDetails(List<DetailsArsakCode> arsakDetails) {
        this.arsakDetails = arsakDetails;
    }

    public Double getEndringBelop() {
        return endringBelop;
    }

    public void setEndringBelop(Double endringBelop) {
        this.endringBelop = endringBelop;
    }

    public Double getPensjonsbeholdningBelop() {
        return pensjonsbeholdningBelop;
    }

    public void setPensjonsbeholdningBelop(Double pensjonsbeholdningBelop) {
        this.pensjonsbeholdningBelop = pensjonsbeholdningBelop;
    }

    public Integer getUttaksgrad() {
        return uttaksgrad;
    }

    public void setUttaksgrad(Integer uttaksgrad) {
        this.uttaksgrad = uttaksgrad;
    }
}
