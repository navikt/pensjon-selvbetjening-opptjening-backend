package no.nav.pensjon.selvbetjeningopptjening.security.group;

import java.util.List;

public interface GroupApi {

    List<AadGroup> checkMemberGroups(List<AadGroup> groups, String accessToken);
}
