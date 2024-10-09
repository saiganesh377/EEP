import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class ObjectInstantiationInOnDraw(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ObjectInstantiationInOnDraw",
        severity = Severity.Performance,
        description = "Avoid object instantiation inside onDraw() method to improve performance.",
        debt = Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        // Check if the function is named "onDraw"
        if (function.name == "onDraw") {
            // Traverse the function body and find any constructor calls (e.g., Paint(), Rect(), etc.)
            function.bodyExpression?.forEachDescendantOfType<KtCallExpression> { callExpression ->
                if (callExpression.isConstructorCall()) {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(callExpression),
                            "Avoid object instantiation inside onDraw() method. Consider moving object creation outside the method."
                        )
                    )
                }
            }
        }
        super.visitNamedFunction(function)
    }

    private fun KtCallExpression.isConstructorCall(): Boolean {
        // Detect if the expression is a constructor call
        return calleeExpression?.text?.firstOrNull()?.isUpperCase() == true
    }
}