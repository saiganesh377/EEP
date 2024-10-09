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
            // Traverse the function body and find constructor calls
            function.bodyExpression?.forEachDescendantOfType<KtCallExpression> { callExpression ->
                if (callExpression.isConstructorCall()) {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(callExpression),
                            "Object instantiation inside onDraw() method can lead to performance issues."
                        )
                    )
                }
            }
        }
        super.visitNamedFunction(function)
    }

    // Helper function to check if a call is a constructor call (e.g., Paint(), Rect())
    private fun KtCallExpression.isConstructorCall(): Boolean {
        // A constructor call would have an uppercase callee (class names in Kotlin start with uppercase letters)
        val calleeName = calleeExpression?.text ?: return false
        return calleeName.isNotEmpty() && calleeName[0].isUpperCase()
    }
}