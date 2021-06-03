package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.nav.pensjon.selvbetjeningopptjening.security.group.AadGroup.*;

@Component
public class GroupChecker {

    // The list of authorized groups is the same as the internal user groups in
    // github.com/navikt/pesys/blob/master/pselv/presentation/nav-presentation-pensjon-pselv-web-core/src/main/resources/menu_pensjon.xml#L66
    private static final List<AadGroup> AUTHORIZED_GROUPS = List.of(
            BRUKERHJELPA,
            OKONOMI,
            SAKSBEHANDLER,
            VEILEDER);

    private final SkjermingApi skjermingApi;

    public GroupChecker(SkjermingApi skjermingApi) {
        this.skjermingApi = skjermingApi;
    }

    public boolean isUserAuthorized(Pid pid, List<AadGroup> memberGroups) {
        return skjermingApi.isSkjermet(pid)
                ? hasAccessToSkjermede(memberGroups)
                : hasNormalAccess(memberGroups);
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
