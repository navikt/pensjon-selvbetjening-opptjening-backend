package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class PdlRequest {

    private static final String QUERY_TEMPLATE_PATH = "pdl/person.graphql";

    static String getPersonQuery(Pid pid) throws IOException {
        var variables = new JSONObject();
        variables.put("ident", pid.getPid());

        return new JSONObject()
                .put("query", getQuery())
                .put("variables", variables)
                .toString();
    }

    private static String getQuery() throws IOException {
        var resource = new ClassPathResource(QUERY_TEMPLATE_PATH);

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);

            return new String(bytes, StandardCharsets.UTF_8)
                    .replace("\r", "")
                    .replace("\n", "");
        }
    }
}
