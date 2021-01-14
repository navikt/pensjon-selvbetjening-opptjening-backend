package no.nav.pensjon.selvbetjeningopptjening.common.selvtest;

import java.util.Objects;

/**
 * Information regarding the target of a ping operation
 */
public class PingInfo {

    private final String resourceType;

    private final String endpoint;

    private final String description;
    /**
     * @param resourceType resourcetype
     * @param description other information
     * @param endpoint the url of the target
     */
    public PingInfo(final String resourceType, final String description, final String endpoint) {
        this.resourceType = resourceType;
        this.endpoint = endpoint;
        this.description = description;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PingInfo pingInfo = (PingInfo) o;
        return resourceType.equals(pingInfo.resourceType) &&
            Objects.equals(endpoint, pingInfo.endpoint) &&
            Objects.equals(description, pingInfo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceType, endpoint, description);
    }

    @Override
    public String toString() {
        return "PingInfo{" +
            "resourceType=" + resourceType +
            ", endpoint='" + endpoint + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
