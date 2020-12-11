package no.nav.pensjon.selvbetjeningopptjening.usersession;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;

public class LoginInfo {

    private final Pid pid;
    private final LoginSecurityLevel securityLevel;

    public LoginInfo(Pid pid, LoginSecurityLevel securityLevel) {
        this.pid = pid;
        this.securityLevel = securityLevel;
    }

    public Pid getPid() {
        return pid;
    }

    public LoginSecurityLevel getSecurityLevel() {
        return securityLevel;
    }
}
