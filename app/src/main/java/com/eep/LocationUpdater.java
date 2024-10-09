import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtQualifiedExpression

class MotionEventNotRecycled(config: Config) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
        Severity.CodeSmell,
        "This rule reports MotionEvent objects that are obtained but not recycled.",
        Debt.TWENTY_MINS)

    private val obtainFunctionNames = listOf("obtain") // Method name to check
    private val recycleFunctionName = "recycle" // Method name to check

    private val motionEvents: MutableList<KtCallExpression> = mutableListOf()

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        // Report any un-recycled MotionEvent objects
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
        if (obtainFunctionNames.contains(functionName) && isMotionEventObtained(call)) {
            motionEvents.add(call) // Add to the list of obtained MotionEvents
        }

        // Check if the call is for recycling a MotionEvent
        if (functionName == recycleFunctionName && isMotionEventRecycled(call)) {
            motionEvents.removeIf { it.getParent() == call.getParent() } // Remove the associated MotionEvent obtain call
        }
    }

    private fun isMotionEventObtained(call: KtCallExpression): Boolean {
        // Ensure the call is from a MotionEvent object
        val receiver = call.parent as? KtQualifiedExpression
        return receiver?.receiverExpression?.text == "MotionEvent"
    }

    private fun isMotionEventRecycled(call: KtCallExpression): Boolean {
        // Ensure the call is from a MotionEvent object
        val receiver = call.parent as? KtQualifiedExpression
        return receiver?.receiverExpression?.text == "MotionEvent"
    }
}