package no.nav.pensjon.selvbetjeningopptjening.security.group;

import java.util.List;

public interface GroupApi {

    List<Group> checkMemberGroups(List<String> groupIds, String accessToken);
}
