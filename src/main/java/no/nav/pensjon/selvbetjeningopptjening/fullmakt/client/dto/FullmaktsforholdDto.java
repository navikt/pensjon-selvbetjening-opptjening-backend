package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FullmaktsforholdDto {
    private final Boolean harFullmaktsforhold;
    private final Boolean erPersonligFullmakt;

    @JsonCreator
    public FullmaktsforholdDto(@JsonProperty("harFullmaktsforhold") Boolean harFullmaktsforhold,
                               @JsonProperty("erPersonligFullmakt")Boolean erPersonligFullmakt) {
        this.harFullmaktsforhold = harFullmaktsforhold;
        this.erPersonligFullmakt = erPersonligFullmakt;
    }

    public Boolean getHarFullmaktsforhold() {
        return harFullmaktsforhold;
    }

    public Boolean getErPersonligFullmakt() {
        return erPersonligFullmakt;
    }
}
