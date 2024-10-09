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

        // Traverse all the call expressions within the function body
        function.bodyExpression?.forEachDescendantOfType<KtCallExpression> { callExpression ->
            // Check if Parcel.obtain() is called
            if (callExpression.calleeExpression?.text == "obtain" &&
                (callExpression.firstChild.text == "Parcel" || callExpression.firstChild.text == "android.os.Parcel")) {
                parcelObtained = true
            }
            // Check if recycle() is called on a Parcel object
            if (callExpression.calleeExpression?.text == "recycle") {
                parcelRecycled = true
            }
        }

        // Report an issue if Parcel.obtain() was called but recycle() wasn't
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