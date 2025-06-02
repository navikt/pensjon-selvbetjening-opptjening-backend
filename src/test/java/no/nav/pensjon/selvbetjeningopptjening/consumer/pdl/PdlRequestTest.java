package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.TestFnrs;
import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdlRequestTest {

    @Test
    void getPersonQuery_should_return_graphQL_query_from_file_together_with_variables() throws IOException {
        String query = PdlRequest.getPersonQuery(new Pid(TestFnrs.NORMAL));

        assertEquals("{\"variables\":{\"ident\":\"03029119367\"}," +
                "\"query\":\"query($ident: ID!){    hentPerson(ident: $ident){        navn(historikk: false){            fornavn            mellomnavn            etternavn            folkeregistermetadata {                ajourholdstidspunkt            }" +
                "            metadata {                master                endringer {                    registrert                }            }        }        foedselsdato{            foedselsdato            foedselsaar        }    }}\"}",
                query);
    }
}
