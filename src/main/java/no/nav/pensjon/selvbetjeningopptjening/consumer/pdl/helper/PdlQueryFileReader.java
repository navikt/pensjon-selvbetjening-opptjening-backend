package no.nav.pensjon.selvbetjeningopptjening.consumer.pdl.helper;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PdlQueryFileReader {
    public static String getQuery(String filename) throws IOException {
        String path = "pdl/" + filename + ".graphql";
        String query;
        Resource resource = new ClassPathResource(path);
        InputStream inputStream = resource.getInputStream();
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            query = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("Failed when trying to read file on path " + path);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return query.replace("\r\n", "");
    }
}
