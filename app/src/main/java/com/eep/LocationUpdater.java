import io.gitlab.arturbosch.detekt.test.*
import io.gitlab.arturbosch.detekt.api.Rule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SetReportDelayRuleTest {

    private val rule: Rule = SetReportDelayRule(TestConfig())

    @Test
    fun `report if setReportDelay is more than 5000`() {
        val code = """
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(6000)  // Should trigger a violation
                .build()
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertEquals(1, findings.size)  // Expect 1 violation
        assertEquals("setReportDelay should be less than 5000 milliseconds to avoid performance issues.", findings[0].message)
    }

    @Test
    fun `do not report if setReportDelay is less than 5000`() {
        val code = """
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(3000)  // Should not trigger a violation
                .build()
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertEquals(0, findings.size)  // Expect no violations
    }
}