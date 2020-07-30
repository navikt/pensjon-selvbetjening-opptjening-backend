package no.nav.pensjon.selvbetjeningopptjening.model;

import java.util.List;

public class Sak {
    private String SakId;
    private Long vedtakId;
    private List<Uttaksgrad> uttaksgradhistorikk;

    public String getSakId() {
        return SakId;
    }

    public void setSakId(String sakId) {
        SakId = sakId;
    }

    public Long getVedtakId() {
        return vedtakId;
    }

    public void setVedtakId(Long vedtakId) {
        this.vedtakId = vedtakId;
    }

    public List<Uttaksgrad> getUttaksgradhistorikk() {
        return uttaksgradhistorikk;
    }

    public void setUttaksgradhistorikk(List<Uttaksgrad> uttaksgradhistorikk) {
        this.uttaksgradhistorikk = uttaksgradhistorikk;
    }
}
