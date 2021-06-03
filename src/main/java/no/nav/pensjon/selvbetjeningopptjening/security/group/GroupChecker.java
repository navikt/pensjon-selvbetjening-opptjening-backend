package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupChecker {

    // Members of this group has access to skjermede personer if also member of group giving regular access:
    private static final String UTVIDET = "676b5e1f-84e6-46e5-8814-04233699ed4b"; // 0000-GA-Pensjon_UTVIDET

    private final SkjermingApi skjermingApi;

    public GroupChecker(SkjermingApi skjermingApi) {
        this.skjermingApi = skjermingApi;
    }

    public boolean isUserAuthorized(Pid pid, List<String> groupIds) {
        return skjermingApi.isSkjermet(pid)
                ? hasAccessToSkjermede(groupIds)
                : hasNormalAccess(groupIds);
    }

    private boolean hasNormalAccess(List<String> groupIds) {
        return groupIds.size() > 1
                || groupIds.size() == 1 && !groupIds.get(0).equals(UTVIDET);
    }

    private boolean hasAccessToSkjermede(List<String> groupIds) {
        return groupIds.size() > 1 && groupIds.contains(UTVIDET);
    }
}
