package no.nav.pensjon.selvbetjeningopptjening.security.filter;

import java.io.Serializable;
import java.util.Objects;

public class ByttBrukerRequest implements Serializable {

    public String fullmaktsgiverPid;
    public String fullmektigPid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByttBrukerRequest that = (ByttBrukerRequest) o;
        return fullmaktsgiverPid.equals(that.fullmaktsgiverPid) && fullmektigPid.equals(that.fullmektigPid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullmaktsgiverPid, fullmektigPid);
    }

    @Override
    public String toString() {
        return "ByttBrukerRequest{" +
                "fullmaktsgiverPid='" + fullmaktsgiverPid + '\'' +
                ", fullmektigPid='" + fullmektigPid + '\'' +
                '}';
    }
}
