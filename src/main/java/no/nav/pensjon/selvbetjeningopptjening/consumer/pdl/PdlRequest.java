package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.helper.PdlQueryFileReader;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.IOException;

class PdlRequest {

    static String getPersonQuery(Pid pid) throws JSONException, IOException {
        var variables = new JSONObject();
        variables.put("ident", pid.getPid());

        return new JSONObject()
                .put("query", PdlQueryFileReader.getQuery("person"))
                .put("variables", variables)
                .toString();
    }
}
