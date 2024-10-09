import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class ParcelNotRecycledRule(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "ParcelNotRecycled",
        severity = Severity.Performance,
        description = "Parcel obtained but not recycled. Always recycle Parcel objects to prevent memory leaks.",
        debt = Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        var parcelObtained = false
        var parcelRecycled = false

        // Traverse the function body to check for Parcel.obtain() and recycle() calls
        function.bodyExpression?.let { body ->
            body.accept(object : KtTreeVisitorVoid() {
                override fun visitCallExpression(expression: KtCallExpression) {
                    super.visitCallExpression(expression)

                    // Check for Parcel.obtain() call
                    if (expression.calleeExpression?.text == "obtain" &&
                        expression.firstChild?.text == "Parcel") {
                        parcelObtained = true
                    }

                    // Check for recycle() call
                    if (expression.calleeExpression?.text == "recycle") {
                        parcelRecycled = true
                    }
                }
            })
        }

        // Report if Parcel was obtained but not recycled
        if (parcelObtained && !parcelRecycled) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(function),
                    "Parcel obtained but not recycled in function ${function.name}."
                )
            )
        }
    }
}