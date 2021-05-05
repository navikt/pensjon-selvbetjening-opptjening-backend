package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class PdlRequest {

    static String getPersonQuery(Pid pid) throws JSONException, IOException {
        var variables = new JSONObject();
        variables.put("ident", pid.getPid());

        return new JSONObject()
                .put("query", getQuery("person"))
                .put("variables", variables)
                .toString();
    }

    private static String getQuery(String filename) throws IOException {
        String path = "pdl/" + filename + ".graphql";
        String query;
        Resource resource = new ClassPathResource(path);

        try(InputStream inputStream = resource.getInputStream()) {
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            query = new String(bdata, StandardCharsets.UTF_8);
        }

        return query.replace("\r\n", "");
    }
}
