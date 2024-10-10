package com.custom.detekt.rules

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class ObjectAllocationInOnDrawRule(config: Config) : Rule(config) {
    
    override val issue: Issue = Issue(
        id = "ObjectAllocationInOnDraw",
        severity = Severity.Performance,
        description = "Object allocations inside onDraw function can cause performance issues.",
        debt = Debt.TWENTY_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        // Check if the function is named 'onDraw'
        if (function.name == "onDraw") {
            // Visit the body of the onDraw function
            function.bodyBlockExpression?.let { body ->
                body.statements.forEach { statement ->
                    // Check for variable declarations (e.g., val or var)
                    if (statement is KtProperty) {
                        statement.initializer?.let { initializer ->
                            // Look for any object instantiation
                            if (initializer is KtCallExpression) {
                                // Report object allocation inside onDraw
                                report(
                                    CodeSmell(
                                        issue,
                                        Entity.from(statement),
                                        "Avoid object allocations inside onDraw. Allocating objects like '${initializer.calleeExpression?.text}' may cause performance issues."
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        super.visitNamedFunction(function)
    }
}