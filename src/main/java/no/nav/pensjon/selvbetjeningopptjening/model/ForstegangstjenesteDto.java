package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForstegangstjenesteDto {

    private Long forstegangstjenesteId;
    private String fnr;
    private List<ForstegangstjenestePeriodeDto> forstegangstjenestePeriodeListe;
    private String kilde;
    private String rapportType;
    private Date tjenestestartDato;
    private Date dimitteringDato;
}
