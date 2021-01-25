package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup.*;
import static no.nav.pensjon.selvbetjeningopptjening.util.ListUtil.listOf;

@Component
public class GroupChecker {

    // The list of authorized groups is the same as the internal user groups in
    // github.com/navikt/pesys/blob/master/pselv/presentation/nav-presentation-pensjon-pselv-web-core/src/main/resources/menu_pensjon.xml#L66
    private static final List<AadGroup> AUTHORIZED_GROUPS = List.of(
            BRUKERHJELPA,
            OKONOMI,
            SAKSBEHANDLER,
            VEILEDER);

    private final GroupApi groupApi;
    private final SkjermingApi skjermingApi;
    private final List<AadGroup> relevantGroups;

    public GroupChecker(GroupApi groupApi, SkjermingApi skjermingApi) {
        this.groupApi = requireNonNull(groupApi);
        this.skjermingApi = skjermingApi;
        this.relevantGroups = listOf(AUTHORIZED_GROUPS, UTVIDET);
    }

    public boolean isUserAuthorized(Pid pid, String accessToken) {
        List<AadGroup> memberGroups = groupApi.checkMemberGroups(relevantGroups, accessToken);
        boolean targetIsSkjermet = skjermingApi.isSkjermet(pid);
        return targetIsSkjermet ? hasAccessToSkjermede(memberGroups) : hasNormalAccess(memberGroups);
    }

    private boolean hasNormalAccess(List<AadGroup> memberGroups) {
        return memberGroups
                .stream()
                .anyMatch(AUTHORIZED_GROUPS::contains);
    }

    private boolean hasAccessToSkjermede(List<AadGroup> memberGroups) {
        return memberGroups.size() > 1
                &&
                memberGroups
                        .stream()
                        .anyMatch(AadGroup::hasAccessToSkjermede);
    }
}
