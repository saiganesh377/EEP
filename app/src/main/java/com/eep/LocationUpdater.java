import com.custom.detekt.rules.SetReportDelayRule
import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import org.junit.jupiter.api.Test

class SetReportDelayRuleManualTest {

    @Test
    fun `manual test for SetReportDelayRule`() {
        // Example Kotlin code that will trigger the rule
        val code = """
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(6000)  // This should trigger a report
                .build()
        """.trimIndent()

        // Compile the code into a KtFile object
        val ktFile: KtFile = KtTestCompiler.compileFromContent(code)

        // Initialize the custom rule
        val rule = SetReportDelayRule(TestConfig())

        // Visit the file using the rule and collect the findings
        rule.visit(ktFile)

        // Manually print findings to inspect them
        rule.findings.forEach { finding ->
            println("Detected issue: ${finding.message}")
        }

        // Optionally, you can log or print other aspects, like the full code positions or types
        println("Total findings: ${rule.findings.size}")
    }
}