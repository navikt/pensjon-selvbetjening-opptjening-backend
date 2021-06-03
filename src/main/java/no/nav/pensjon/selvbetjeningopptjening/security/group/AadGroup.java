package no.nav.pensjon.selvbetjeningopptjening.security.group;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 * Azure Active Directory (AAD) groups.
 * The IDs are the groups' object ID, available from:
 * aad.portal.azure.com/#blade/Microsoft_AAD_IAM/GroupsManagementMenuBlade/AllGroups
 */
public enum AadGroup {

    BRUKERHJELPA("7845a796-1516-4d14-b500-fd65c001f35c", "0000-GA-PENSJON_BRUKERHJELPA", false),
    OKONOMI("70ef8e7f-7456-4298-95e0-b13c0ef2422b", "0000-GA-Pensjon_Okonomi", false),
    SAKSBEHANDLER("0af3955f-df85-4eb0-b5b2-45bf2c8aeb9e", "0000-GA-PENSJON_SAKSBEHANDLER", false),
    VEILEDER("959ead5b-99b5-466b-a0ff-5fdbc687517b", "0000-GA-Pensjon_VEILEDER", false),
    UTVIDET("676b5e1f-84e6-46e5-8814-04233699ed4b", "0000-GA-Pensjon_UTVIDET", true);

    private static final Map<String, AadGroup> GROUPS_BY_ID = new HashMap<>();

    static {
        stream(values()).forEach(group -> GROUPS_BY_ID.put(group.id, group));
    }

    private final String id;
    private final String name;
    private final boolean accessToSkjermede;

    AadGroup(String id, String name, boolean accessToSkjermede) {
        this.id = id;
        this.name = name;
        this.accessToSkjermede = accessToSkjermede;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasAccessToSkjermede() {
        return accessToSkjermede;
    }

    public static boolean exists(String id) {
        return GROUPS_BY_ID.containsKey(id);
    }

    public static AadGroup findById(String id) {
        AadGroup group = GROUPS_BY_ID.get(id);
        return group == null ? noSuchElement(id) : group;
    }

    private static AadGroup noSuchElement(String id) {
        throw new NoSuchElementException(format("No '%s' value present", id));
    }
}
