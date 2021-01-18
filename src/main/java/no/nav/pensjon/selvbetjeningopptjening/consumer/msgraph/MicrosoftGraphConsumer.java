package no.nav.pensjon.selvbetjeningopptjening.consumer.msgraph;

import no.nav.pensjon.selvbetjeningopptjening.security.group.Group;
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
    public List<Group> checkMemberGroups(List<String> groupIds, String accessToken) {
        String body = format("{\"groupIds\": [%s]}", jsonize(groupIds));

        try {
            GroupsDto groups = webClient
                    .post()
                    .uri(baseUrl + CHECK_MEMBER_GROUPS_ENDPOINT)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GroupsDto.class)
                    .block();

            return groups == null ? emptyList() : fromDto(groups.getValue());
        } catch (WebClientResponseException e) {
            log.error(format("Call to MS Graph API failed: %s. Response body: %s.",
                    e.getMessage(), e.getResponseBodyAsString()));
            return emptyList();
        }
    }

    private static String jsonize(List<String> groupIds) {
        return groupIds
                .stream()
                .map(id -> format("\"%s\"", id))
                .collect(joining(","));
    }

    private static List<Group> fromDto(List<String> groupIds) {
        return groupIds == null ? emptyList()
                :
                groupIds
                        .stream()
                        .map(Group::new)
                        .collect(toList());
    }
}
