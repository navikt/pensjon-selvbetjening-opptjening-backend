package no.nav.pensjon.selvbetjeningopptjening.consumer.msgraph;

import java.util.List;

public class GroupsDto {

    // Do not rename unless MS Graph API changes
    private List<String> value;

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
