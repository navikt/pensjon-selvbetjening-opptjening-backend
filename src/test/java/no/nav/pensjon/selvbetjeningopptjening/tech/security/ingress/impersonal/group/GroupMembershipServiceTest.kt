package no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.group

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjeningopptjening.mock.TestObjects.pid
import no.nav.pensjon.selvbetjeningopptjening.person.AdressebeskyttelseGradering
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.selvbetjeningopptjening.tech.security.ingress.impersonal.skjerming.SkjermingService

class GroupMembershipServiceTest : ShouldSpec({

    context("innlogget bruker") {
        should("i utgangspunktet ikke ha tilgang") {
            val membershipService = arrangeMembershipService(
                groups = emptyList(),
                personErSkjermet = false,
                gradering = AdressebeskyttelseGradering.UGRADERT
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe false
        }
    }

    context("alminnelig veileder") {
        should("ha tilgang til ubeskyttet person") {
            val membershipService = arrangeMembershipService(
                groups = listOf("veileder-gruppa"),
                personErSkjermet = false,
                gradering = AdressebeskyttelseGradering.UGRADERT
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe true
        }

        should("ikke ha tilgang til beskyttet person") {
            val membershipService = arrangeMembershipService(
                groups = listOf("veileder-gruppa"),
                personErSkjermet = true,
                gradering = AdressebeskyttelseGradering.UGRADERT
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe false
        }
    }

    context("veileder med fortrolig tilgang") {
        should("ikke ha tilgang til personer med strengt fortrolig adresse") {
            val membershipService = arrangeMembershipService(
                groups = listOf("veileder-gruppa", "fortrolig-adresse-gruppa"),
                personErSkjermet = false,
                gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe false
        }
    }

    context("veileder med strengt fortrolig tilgang") {
        should("ikke ha tilgang til skjermet person") {
            val membershipService = arrangeMembershipService(
                groups = listOf("veileder-gruppa", "strengt-fortrolig-adresse-gruppa"),
                personErSkjermet = true,
                gradering = AdressebeskyttelseGradering.UGRADERT
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe false
        }
    }

    context("veileder med full tilgang") {
        should("ha tilgang til skjermede personer med strengt fortrolig adresse") {
            val membershipService = arrangeMembershipService(
                groups = listOf(
                    "veileder-gruppa",
                    "egne-ansatte-gruppa",
                    "fortrolig-adresse-gruppa",
                    "strengt-fortrolig-adresse-gruppa"
                ),
                personErSkjermet = true,
                gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe true
        }
    }

    context("veileder med kun tilleggstilganger") {
        should("ikke ha tilgang til applikasjonen") {
            val membershipService = arrangeMembershipService(
                groups = listOf(
                    "egne-ansatte-gruppa",
                    "fortrolig-adresse-gruppa",
                    "strengt-fortrolig-adresse-gruppa"
                    // mangler grunntilgang (veileder-gruppa)
                ),
                personErSkjermet = false,
                gradering = AdressebeskyttelseGradering.UGRADERT
            )

            membershipService.innloggetBrukerHarTilgang(pid) shouldBe false
        }
    }
})

private fun arrangeMembershipService(
    groups: List<String>,
    personErSkjermet: Boolean,
    gradering: AdressebeskyttelseGradering
) =
    GroupMembershipService(
        brukerhjelpGroupId = "brukerhjelp-gruppa",
        oekonomiGroupId = "økonomi-gruppa",
        saksbehandlerGroupId = "saksbehandler-gruppa",
        veilederGroupId = "veileder-gruppa",
        egneAnsatteGroupId = "egne-ansatte-gruppa",
        fortroligAdresseGroupId = "fortrolig-adresse-gruppa",
        strengtFortroligAdresseGroupId = "strengt-fortrolig-adresse-gruppa",
        groupService = arrangeGroupService(groups),
        skjermingService = arrangeSkjerming(personErSkjermet),
        adresseService = arrangeAdressebeskyttelse(gradering)
    )

private fun arrangeAdressebeskyttelse(gradering: AdressebeskyttelseGradering) =
    mockk<FortroligAdresseService>().apply {
        every { adressebeskyttelseGradering(any()) } returns gradering
    }

private fun arrangeGroupService(groups: List<String>) =
    mockk<GroupService>().apply {
        every { groups() } returns groups
    }

private fun arrangeSkjerming(personErSkjermet: Boolean) =
    mockk<SkjermingService>().apply {
        every { personErTilgjengelig(any()) } returns personErSkjermet.not()
    }
