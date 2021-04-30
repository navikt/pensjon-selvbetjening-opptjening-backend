package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdlRequestTest {

    @Test
    void getBirthQuery_shall_return_graphQlQuery() throws JSONException {
        String query = PdlRequest.getBirthQuery(new Pid(TestFnrs.NORMAL));

        assertEquals("{\"query\":\"query($ident: ID!){hentPerson(ident: $ident){navn(historikk: false){fornavn mellomnavn etternavn} foedsel{foedselsdato foedselsaar}}}\"," +
                "\"variables\":{\"ident\":\"03029119367\"}}", query);
    }
}
