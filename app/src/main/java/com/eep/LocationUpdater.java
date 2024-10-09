import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class ObjectInstantiationInOnDrawTest {

    private val rule = ObjectInstantiationInOnDraw(Config.empty)

    @Test
    fun `no object instantiation in onDraw should pass`() {
        val code = """
            import android.content.Context
            import android.graphics.Canvas
            import android.view.View

            class CustomView(context: Context) : View(context) {
                override fun onDraw(canvas: Canvas?) {
                    // No object instantiation here
                    canvas?.drawColor(android.graphics.Color.BLACK)
                }
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assert(findings.isEmpty()) { "Expected no findings, but found ${findings.size}" }
    }

    @Test
    fun `object instantiation in onDraw should fail`() {
        val code = """
            import android.content.Context
            import android.graphics.Canvas
            import android.graphics.Paint
            import android.view.View

            class CustomView(context: Context) : View(context) {
                override fun onDraw(canvas: Canvas?) {
                    // Object instantiation inside onDraw
                    val paint = Paint()
                    canvas?.drawColor(android.graphics.Color.BLACK)
                }
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assert(findings.size == 1) { "Expected 1 finding, but found ${findings.size}" }
        assert(findings[0].message.contains("Object instantiation inside onDraw() method can lead to performance issues.")) {
            "Expected message to contain information about performance issues, but found: ${findings[0].message}"
        }
    }

    @Test
    fun `multiple object instantiations in onDraw should fail`() {
        val code = """
            import android.content.Context
            import android.graphics.Canvas
            import android.graphics.Paint
            import android.graphics.Rect
            import android.view.View

            class CustomView(context: Context) : View(context) {
                override fun onDraw(canvas: Canvas?) {
                    val paint = Paint()
                    val rect = Rect()
                    canvas?.drawRect(rect, paint)
                }
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)
        assert(findings.size == 2) { "Expected 2 findings, but found ${findings.size}" }
    }
}
