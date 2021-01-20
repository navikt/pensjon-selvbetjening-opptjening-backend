package no.nav.pensjon.selvbetjeningopptjening.security.group;

import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroupIds.*;

@Component
public class GroupChecker {

    // The list of groups is the same as the internal user groups in
    // github.com/navikt/pesys/blob/master/pselv/presentation/nav-presentation-pensjon-pselv-web-core/src/main/resources/menu_pensjon.xml#L66
    private static final List<String> AUTHORIZED_GROUPS = List.of(
            BRUKERHJELPA,
            OKONOMI,
            SAKSBEHANDLER,
            VEILEDER);

    private final GroupApi groupApi;

    public GroupChecker(GroupApi groupApi) {
        this.groupApi = requireNonNull(groupApi);
    }

    public boolean isUserAuthorized(String accessToken) {
        List<Group> memberGroups = groupApi.checkMemberGroups(AUTHORIZED_GROUPS, accessToken);
        return !memberGroups.isEmpty();
    }
}
