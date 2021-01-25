package no.nav.pensjon.selvbetjeningopptjening.consumer.msgraph;

import no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup;
import no.nav.pensjon.selvbetjeningopptjening.security.group.GroupApi;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Component
public class MicrosoftGraphConsumer implements GroupApi {

    private static final String CHECK_MEMBER_GROUPS_ENDPOINT = "me/checkMemberGroups";
    private static final String CONTENT_TYPE = "application/json";
    private final WebClient webClient;
    private final String baseUrl;
    private final Log log = LogFactory.getLog(getClass());

    public MicrosoftGraphConsumer(@Qualifier("external-call") WebClient webClient,
                                  @Value("${msgraph.endpoint.url}") String baseUrl) {
        this.webClient = requireNonNull(webClient);
        this.baseUrl = requireNonNull(baseUrl);
    }

    /**
     * https://docs.microsoft.com/en-us/graph/api/user-checkmembergroups
     */
    @Override
    public List<AadGroup> checkMemberGroups(List<AadGroup> groups, String accessToken) {
        String body = format("{\"groupIds\": [%s]}", idsAsJson(groups));

        try {
            GroupsDto dtos = webClient
                    .post()
                    .uri(baseUrl + CHECK_MEMBER_GROUPS_ENDPOINT)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GroupsDto.class)
                    .block();

            return dtos == null ? emptyList() : fromDto(dtos.getValue());
        } catch (WebClientResponseException e) {
            log.error(format("Call to MS Graph API failed: %s. Response body: %s.",
                    e.getMessage(), e.getResponseBodyAsString()));
            return emptyList();
        }
    }

    private static String idsAsJson(List<AadGroup> groups) {
        return groups
                .stream()
                .map(group -> format("\"%s\"", group.getId()))
                .collect(joining(","));
    }

    private static List<AadGroup> fromDto(List<String> groupIds) {
        return groupIds == null ? emptyList()
                :
                groupIds
                        .stream()
                        .map(AadGroup::findById)
                        .collect(toList());
    }
}
