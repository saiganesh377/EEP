import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext

class ParcelNotRecycled(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports Parcel objects that are obtained but not recycled.",
        Debt.TWENTY_MINS)

    private val obtainFunctionNames = listOf("obtain") // Method name to check
    private val recycleFunctionName = "recycle" // Method name to check

    private val parcels: MutableList<KtCallExpression> = mutableListOf()

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        // Report any un-recycled Parcel objects
        parcels.forEach { parcel ->
            report(CodeSmell(issue, Entity.from(parcel),
                "Parcel obtained but 'recycle()' not called."))
        }
        parcels.clear() // Clear the list after processing the file
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        function.bodyExpression?.accept(this)
    }

    override fun visitCallExpression(call: KtCallExpression) {
        super.visitCallExpression(call)

        val functionName = call.calleeExpression?.text

        // Check if the call is for obtaining a Parcel
        if (obtainFunctionNames.contains(functionName) && isParcelObtained(call)) {
            parcels.add(call) // Add to the list of obtained Parcels
        }

        // Check if the call is for recycling a Parcel
        if (functionName == recycleFunctionName && isParcelRecycled(call)) {
            parcels.removeIf { it.getParent() == call.getParent() } // Remove the associated Parcel obtain call
        }
    }

    private fun isParcelObtained(call: KtCallExpression): Boolean {
        // Ensure the call is from a Parcel object
        val receiver = call.parent as? KtQualifiedExpression
        return receiver?.receiverExpression?.text == "Parcel"
    }

    private fun isParcelRecycled(call: KtCallExpression): Boolean {
        // Ensure the call is from a Parcel object
        val receiver = call.parent as? KtQualifiedExpression
        return receiver?.receiverExpression?.text == "Parcel"
    }
}