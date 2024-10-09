import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class ObjectInstantiationInOnDraw(config: Config) : Rule(config) {

    override val issue: Issue = Issue(
        id = "ObjectInstantiationInOnDraw",
        severity = Severity.Performance,
        description = "Avoid object instantiation inside onDraw() method to improve performance.",
        debt = Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        // Check if the method is named "onDraw"
        if (function.name == "onDraw") {
            // Traverse through the function body to find object instantiations
            function.bodyExpression?.forEachDescendantOfType<KtCallExpression> { callExpression ->
                // Checking if the call expression is an object creation (constructor call)
                val callee = callExpression.calleeExpression
                if (callee is KtNameReferenceExpression && callExpression.isConstructorCall()) {
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

    private fun KtCallExpression.isConstructorCall(): Boolean {
        // Check if this call expression is a constructor call (e.g., Paint(), Rect())
        val type = calleeExpression?.text ?: return false
        return type.isNotEmpty() && !type[0].isLowerCase() // Class names in Kotlin start with uppercase letters
    }
}