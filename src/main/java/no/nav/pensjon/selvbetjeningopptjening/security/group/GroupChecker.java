package no.nav.pensjon.selvbetjeningopptjening.security.group;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupChecker {

    private final List<String> requiredGroupIds;
    private final SkjermingApi skjermingApi;
    private final String egneAnsatteTilgangGroupId;
    private final String utvidetGroupId;

    public GroupChecker(SkjermingApi skjermingApi,
                        @Value("${brukerhjelp.group.id}") String brukerhjelpGroupId,
                        @Value("${oekonomi.group.id}") String oekonomiGroupId,
                        @Value("${saksbehandler.group.id}") String saksbehandlerGroupId,
                        @Value("${veileder.group.id}") String veilederGroupId,
                        @Value("${egne-ansatte-tilgang.group.id}") String egneAnsatteTilgangGroupId,
                        @Value("${utvidet.group.id}") String utvidetGroupId) {
        this.skjermingApi = skjermingApi;
        this.requiredGroupIds = List.of(brukerhjelpGroupId, oekonomiGroupId, saksbehandlerGroupId, veilederGroupId);
        this.egneAnsatteTilgangGroupId = egneAnsatteTilgangGroupId;
        this.utvidetGroupId = utvidetGroupId;
    }

    public boolean isUserAuthorized(Pid pid, List<String> memberOfGroupIds) {
        return skjermingApi.isSkjermet(pid)
                ? hasAccessToSkjermede(memberOfGroupIds)
                : hasNormalAccess(memberOfGroupIds);
    }

    private boolean hasNormalAccess(List<String> memberOfGroupIds) {
        return getRetainedGroupIds(memberOfGroupIds).size() > 0;
    }

    private List<String> getRetainedGroupIds(List<String> memberOfGroupIds) {
        ArrayList<String> retainedGroupIds = new ArrayList<>(requiredGroupIds);
        retainedGroupIds.retainAll(memberOfGroupIds);
        return retainedGroupIds;
    }

    private boolean hasAccessToSkjermede(List<String> memberOfGroupIds) {
        return hasNormalAccess(memberOfGroupIds)
                && (memberOfGroupIds.contains(egneAnsatteTilgangGroupId) || memberOfGroupIds.contains(utvidetGroupId));
    }
}
