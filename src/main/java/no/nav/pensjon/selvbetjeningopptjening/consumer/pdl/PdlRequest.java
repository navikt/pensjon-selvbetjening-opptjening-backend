package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

class PdlRequest {

    static String getPersonQuery(Pid pid) throws JSONException {
        String query = "query($ident: ID!){hentPerson(ident: $ident){navn(historikk: false){fornavn mellomnavn etternavn} foedsel{foedselsdato foedselsaar}}}";
        var variables = new JSONObject();
        variables.put("ident", pid.getPid());

        return new JSONObject()
                .put("query", query)
                .put("variables", variables)
                .toString();
    }
}
