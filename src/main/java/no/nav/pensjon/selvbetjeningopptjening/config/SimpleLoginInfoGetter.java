package no.nav.pensjon.selvbetjeningopptjening.config;

import no.nav.pensjon.selvbetjeningopptjening.opptjening.Pid;
import no.nav.pensjon.selvbetjeningopptjening.security.LoginSecurityLevel;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfo;
import no.nav.pensjon.selvbetjeningopptjening.usersession.LoginInfoGetter;

public class SimpleLoginInfoGetter implements LoginInfoGetter {

    private final String fnr;

    public SimpleLoginInfoGetter(String fnr) {
        this.fnr = fnr;
    }

    @Override
    public LoginInfo getLoginInfo() {
        return new LoginInfo(new Pid(fnr), LoginSecurityLevel.NONE);
    }
}
