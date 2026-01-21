package no.nav.pensjon.selvbetjeningopptjening.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PeriodeUtilTest : ShouldSpec({

    context("sortPerioderByFomDate") {
        should("sort by f.o.m.-dato") {
            val actual: List<TestPeriode> = PeriodeUtil.sortPerioderByFomDate(
                perioder = listOf(
                    TestPeriode(
                        start = LocalDate.of(2020, 1, 1),
                        end = LocalDate.of(2020, 1, 31)
                    ),
                    TestPeriode(
                        start = LocalDate.of(2019, 1, 1),
                        end = LocalDate.of(2019, 1, 31)
                    )
                )
            )

            actual[0].getFomDato().year shouldBe 2019
            actual[1].getFomDato().year shouldBe 2020
        }

        should("disregard t.o.m.-dato") {
            val actual: List<TestPeriode> = PeriodeUtil.sortPerioderByFomDate(
                perioder = listOf(
                    TestPeriode(
                        start = LocalDate.of(2020, 1, 1),
                        end = LocalDate.of(2020, 1, 31)
                    ),
                    TestPeriode(
                        start = LocalDate.of(2019, 1, 1),
                        end = LocalDate.of(2020, 2, 1)
                    )
                )
            )

            actual[0].getFomDato().year shouldBe 2019
            actual[1].getFomDato().year shouldBe 2020
        }
    }

    context("isPeriodeWithinInterval") {
        should("give 'false' when the period is equal to the interval") {
            PeriodeUtil.isPeriodeWithinInterval(
                periode = TestPeriode(
                    start = LocalDate.of(2020, 1, 1),
                    end = LocalDate.of(2020, 1, 31)
                ),
                start = LocalDate.of(2020, 1, 1),
                end = LocalDate.of(2020, 1, 31)
            ) shouldBe false
        }

        should("give 'false' when the period is outside the interval on both sides") {
            PeriodeUtil.isPeriodeWithinInterval(
                periode = TestPeriode(
                    start = LocalDate.of(2020, 1, 1),
                    end = LocalDate.of(2020, 1, 31)
                ),
                start = LocalDate.of(2020, 1, 2),
                end = LocalDate.of(2020, 1, 30)
            ) shouldBe false
        }

        should("give 'false' when the period is outside the interval on start side") {
            PeriodeUtil.isPeriodeWithinInterval(
                periode = TestPeriode(
                    start = LocalDate.of(2020, 1, 1),
                    end = LocalDate.of(2020, 1, 30)
                ),
                start = LocalDate.of(2020, 1, 2),
                end = LocalDate.of(2020, 1, 31)
            ) shouldBe false
        }

        should("give 'false' when the period is outside the interval on end side") {
            PeriodeUtil.isPeriodeWithinInterval(
                periode = TestPeriode(
                    start = LocalDate.of(2020, 1, 2),
                    end = LocalDate.of(2020, 1, 31)
                ),
                start = LocalDate.of(2020, 1, 1),
                end = LocalDate.of(2020, 1, 30)
            ) shouldBe false
        }

        should("give 'true' when the period is strictly within the interval") {
            PeriodeUtil.isPeriodeWithinInterval(
                periode = TestPeriode(
                    start = LocalDate.of(2020, 1, 2),
                    end = LocalDate.of(2020, 1, 30)
                ),
                start = LocalDate.of(2020, 1, 1),
                end = LocalDate.of(2020, 1, 31)
            ) shouldBe true
        }
    }

})

data class TestPeriode(
    val start: LocalDate,
    val end: LocalDate
) : Periode {
    override fun getFomDato(): LocalDate = start

    override fun getTomDato(): LocalDate = end
}
