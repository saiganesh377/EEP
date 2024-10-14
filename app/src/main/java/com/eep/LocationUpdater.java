package com.custom.detekt.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class SetReportDelayRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = "SetReportDelayThreshold",
        severity = Severity.Performance,
        description = "The setReportDelay value in ScanSettings should be less than 5000 milliseconds.",
        debt = Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        // Check if the method called is 'setReportDelay'
        val methodName = expression.calleeExpression?.text
        if (methodName == "setReportDelay") {
            // Extract the argument passed to setReportDelay
            val argument = expression.valueArguments.firstOrNull()?.getArgumentExpression()
            if (argument is KtConstantExpression) {
                // Parse the value of the argument
                val delayValue = argument.text.toLongOrNull()
                // Check if the delay value exceeds 5000
                if (delayValue != null && delayValue >= 5000) {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(expression),
                            "setReportDelay should be less than 5000 milliseconds to avoid performance issues."
                        )
                    )
                }
            }
        }
        super.visitCallExpression(expression)
    }
}