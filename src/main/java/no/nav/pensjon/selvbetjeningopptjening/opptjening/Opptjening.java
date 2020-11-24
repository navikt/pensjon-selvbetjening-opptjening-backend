package no.nav.pensjon.selvbetjeningopptjening.opptjening;

import no.nav.pensjon.selvbetjeningopptjening.model.code.MerknadCode;

import java.util.ArrayList;
import java.util.List;

public class Opptjening {

    private final List<MerknadCode> merknader = new ArrayList<>();
    private boolean hasPensjonsgivendeInntekt;
    private boolean hasPensjonsbeholdning;
    private boolean hasPensjonspoeng;
    private boolean hasOmsorgspoeng;
    private boolean hasRestpensjon;
    private long pensjonsgivendeInntekt;
    private long pensjonsbeholdning;
    private double pensjonspoeng;
    private double omsorgspoeng;
    private double restpensjon;
    private int maxUforegrad;
    private String omsorgspoengType;
    private List<EndringPensjonsopptjening> opptjeningsendringer;

    public Opptjening(Long pensjonsgivendeInntekt, Double pensjonspoeng) {
        this.pensjonsgivendeInntekt = pensjonsgivendeInntekt == null ? 0L : pensjonsgivendeInntekt;
        this.hasPensjonsgivendeInntekt = pensjonsgivendeInntekt != null;
        this.pensjonspoeng = pensjonspoeng == null ? 0D : pensjonspoeng;
        this.hasPensjonspoeng = pensjonspoeng != null;
    }

    public long getPensjonsgivendeInntekt() {
        return pensjonsgivendeInntekt;
    }

    void setPensjonsgivendeInntekt(Long value) {
        pensjonsgivendeInntekt = value == null ? 0L : value;
        hasPensjonsgivendeInntekt = value != null;
    }

    public double getPensjonspoeng() {
        return pensjonspoeng;
    }

    public void setPensjonspoeng(Double value) {
        pensjonspoeng = value == null ? 0D : value;
        hasPensjonspoeng = value != null;
    }

    public double getOmsorgspoeng() {
        return omsorgspoeng;
    }

    public void setOmsorgspoeng(Double value) {
        omsorgspoeng = value == null ? 0D : value;
        hasOmsorgspoeng = value != null;
    }

    public String getOmsorgspoengType() {
        return omsorgspoengType;
    }

    public void setOmsorgspoengType(String value) {
        omsorgspoengType = value;
    }

    public List<EndringPensjonsopptjening> getOpptjeningsendringer() {
        return opptjeningsendringer;
    }

    public void setOpptjeningsendringer(List<EndringPensjonsopptjening> values) {
        opptjeningsendringer = values;
    }

    public boolean hasPensjonsbeholdning() {
        return hasPensjonsbeholdning;
    }

    public long getPensjonsbeholdning() {
        return pensjonsbeholdning;
    }

    public void setPensjonsbeholdning(Long value) {
        pensjonsbeholdning = value == null ? 0L : value;
        hasPensjonsbeholdning = value != null;
    }

    public boolean hasRestpensjon() {
        return hasRestpensjon;
    }

    public double getRestpensjon() {
        return restpensjon;
    }

    public void setRestpensjon(Double value) {
        restpensjon = value == null ? 0D : value;
        hasRestpensjon = value != null;
    }

    public int getMaxUforegrad() {
        return maxUforegrad;
    }

    public void setMaxUforegrad(Integer value) {
        maxUforegrad = value == null ? 0 : value;
    }

    public boolean hasPensjonspoeng() {
        return hasPensjonspoeng;
    }

    public boolean hasOmsorgspoeng() {
        return hasOmsorgspoeng;
    }

    public List<MerknadCode> getMerknader() {
        return merknader;
    }

    boolean hasMerknad(MerknadCode value) {
        return merknader.contains(value);
    }

    public void addMerknad(MerknadCode value) {
        merknader.add(value);
    }

    public void addMerknader(List<MerknadCode> values) {
        merknader.addAll(values);
    }

    public boolean hasPensjonsgivendeInntekt() {
        return hasPensjonsgivendeInntekt;
    }

    boolean isNotPositive() {
        return pensjonsgivendeInntekt <= 0
                && pensjonsbeholdning <= 0
                && pensjonspoeng <= 0;
    }

    boolean isOmsorgspoengLessThanOrEqualToPensjonspoeng() {
        return hasOmsorgspoeng && hasPensjonspoeng
                && omsorgspoeng <= pensjonspoeng;
    }
}
