package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

public class PdlRequest {

    private String ident;

    public PdlRequest(String ident) {
        this.ident = ident;
    }

    String getGraphQlQuery() throws JSONException {
        String queryValue = "query($ident: ID!){hentPerson(ident: $ident){foedsel{foedselsdato foedselsaar}}}";
        var variablesValue = new JSONObject();
        variablesValue.put("ident", ident);

        return new JSONObject()
                .put("query", queryValue)
                .put("variables", variablesValue)
                .toString();
    }
}
