import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class ParcelNotRecycled(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports Parcel objects that are obtained but not recycled.",
        Debt.TWENTY_MINS)

    private val parcelFunctionNames = listOf("obtain") // You can add more if needed
    private val recycleFunctionName = "recycle"

    private val parcels: MutableList<KtCallExpression> = mutableListOf()

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        // Check for any un-recycled parcels
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
        if (parcelFunctionNames.contains(functionName)) {
            parcels.add(call) // Add to the list of obtained parcels
        }

        // Check if the call is for recycling a Parcel
        if (functionName == recycleFunctionName) {
            parcels.clear() // Clear the list since a recycle was found
        }
    }
}