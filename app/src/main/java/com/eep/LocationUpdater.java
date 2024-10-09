import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MotionEventNotRecycled(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports MotionEvent objects that are obtained but not recycled.",
        Debt.TWENTY_MINS)

    private val motionEventFunctionNames = listOf("obtain") // You can add more if needed
    private val recycleFunctionName = "recycle"

    private val motionEvents: MutableList<KtCallExpression> = mutableListOf()

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        // Check for any un-recycled MotionEvent objects
        motionEvents.forEach { motionEvent ->
            report(CodeSmell(issue, Entity.from(motionEvent),
                "MotionEvent obtained but 'recycle()' not called."))
        }
        motionEvents.clear() // Clear the list after processing the file
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        function.bodyExpression?.accept(this)
    }

    override fun visitCallExpression(call: KtCallExpression) {
        super.visitCallExpression(call)

        val functionName = call.calleeExpression?.text

        // Check if the call is for obtaining a MotionEvent
        if (motionEventFunctionNames.contains(functionName)) {
            motionEvents.add(call) // Add to the list of obtained MotionEvents
        }

        // Check if the call is for recycling a MotionEvent
        if (functionName == recycleFunctionName) {
            motionEvents.clear() // Clear the list since a recycle was found
        }
    }
}