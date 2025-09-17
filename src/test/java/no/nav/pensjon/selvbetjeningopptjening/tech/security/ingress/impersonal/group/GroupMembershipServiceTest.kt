package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group

import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.person.AdressebeskyttelseGradering
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.SkjermingService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class GroupMembershipServiceTest {

    private lateinit var groupMembershipService: GroupMembershipService

    @Mock
    private lateinit var groupService: GroupService

    @Mock
    private lateinit var skjermingService: SkjermingService

    @Mock
    private lateinit var adresseService: FortroligAdresseService

    @BeforeEach
    fun initialize() {
        groupMembershipService = GroupMembershipService(
            brukerhjelpGroupId = "brukerhjelp-gruppa",
            oekonomiGroupId = "økonomi-gruppa",
            saksbehandlerGroupId = "saksbehandler-gruppa",
            veilederGroupId = "veileder-gruppa",
            egneAnsatteGroupId = "egne-ansatte-gruppa",
            fortroligAdresseGroupId = "fortrolig-adresse-gruppa",
            strengtFortroligAdresseGroupId = "strengt-fortrolig-adresse-gruppa",
            groupService,
            skjermingService,
            adresseService
        )
    }

    @Test
    fun `innlogget bruker har i utgangspunktet ikke tilgang`() {
        `when`(groupService.groups()).thenReturn(emptyList())
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `alminnelig veileder har tilgang til ubeskyttet person`() {
        `when`(groupService.groups()).thenReturn(listOf("veileder-gruppa"))
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertTrue(harTilgang)
    }

    @Test
    fun `alminnelig veileder har ikke tilgang til beskyttet person`() {
        `when`(groupService.groups()).thenReturn(listOf("veileder-gruppa"))
        arrangeBeskyttelse(personErSkjermet = true, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `veileder med fortrolig tilgang har ikke tilgang til personer med strengt fortrolig adresse`() {
        `when`(groupService.groups()).thenReturn(listOf("veileder-gruppa", "fortrolig-adresse-gruppa"))
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.STRENGT_FORTROLIG)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `veileder med strengt fortrolig tilgang har ikke tilgang til skjermet person`() {
        `when`(groupService.groups()).thenReturn(listOf("veileder-gruppa", "strengt-fortrolig-adresse-gruppa"))
        arrangeBeskyttelse(personErSkjermet = true, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `veileder med full tilgang har tilgang til skjermede personer med strengt fortrolig adresse`() {
        `when`(groupService.groups()).thenReturn(
            listOf(
                "veileder-gruppa",
                "egne-ansatte-gruppa",
                "fortrolig-adresse-gruppa",
                "strengt-fortrolig-adresse-gruppa"
            )
        )
        arrangeBeskyttelse(personErSkjermet = true, AdressebeskyttelseGradering.STRENGT_FORTROLIG)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertTrue(harTilgang)
    }

    @Test
    fun `veileder med kun tilleggstilganger har ikke tilgang til applikasjonen`() {
        `when`(groupService.groups()).thenReturn(
            listOf(
                "egne-ansatte-gruppa",
                "fortrolig-adresse-gruppa",
                "strengt-fortrolig-adresse-gruppa"
                // mangler grunntilgang (veileder-gruppa)
            )
        )
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }


    private fun arrangeBeskyttelse(personErSkjermet: Boolean, gradering: AdressebeskyttelseGradering) {
        `when`(skjermingService.personErTilgjengelig(pid)).thenReturn(personErSkjermet.not())
        `when`(adresseService.adressebeskyttelseGradering(pid)).thenReturn(gradering)
    }
}
