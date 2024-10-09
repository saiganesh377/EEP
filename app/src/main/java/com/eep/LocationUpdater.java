import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*

class ParcelNotRecycledRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ParcelNotRecycled",
        severity = Severity.CodeSmell,
        description = "Parcel obtained but not recycled. Always recycle Parcel objects after use to prevent memory leaks.",
        debt = Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        // Check if the method call is to `Parcel.obtain()`
        if (expression.calleeExpression?.text == "Parcel.obtain") {
            val containingFunction = expression.getStrictParentOfType<KtNamedFunction>()
            if (containingFunction != null && !isParcelRecycled(containingFunction, expression)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        "Parcel obtained but not recycled in the function ${containingFunction.name}."
                    )
                )
            }
        }
    }

    // Helper function to check if `recycle()` is called on the Parcel
    private fun isParcelRecycled(function: KtNamedFunction, parcelObtainExpression: KtCallExpression): Boolean {
        val parcelVariableName = parcelObtainExpression.getParentOfType<KtBinaryExpression>(true)
            ?.left?.text ?: return false
        
        var isRecycled = false
        function.bodyExpression?.forEachDescendantOfType<KtCallExpression> { callExpression ->
            if (callExpression.calleeExpression?.text == "recycle") {
                // Check if recycle() is called on the same Parcel object
                val receiverExpression = callExpression.getReceiverExpression()
                if (receiverExpression?.text == parcelVariableName) {
                    isRecycled = true
                    return@forEachDescendantOfType
                }
            }
        }
        return isRecycled
    }

    // Helper function to get the receiver expression (if any)
    private fun KtCallExpression.getReceiverExpression(): KtExpression? {
        return (this.parent as? KtDotQualifiedExpression)?.receiverExpression
    }
}