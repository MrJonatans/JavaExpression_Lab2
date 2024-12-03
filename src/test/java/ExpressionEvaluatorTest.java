import org.expressions.ExpressionEvaluator;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    @Test
    void testSimpleExpressions() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertEquals(7.0, evaluator.evaluate("3 + 4"));
        assertEquals(6.0, evaluator.evaluate("2 * 3"));
        assertEquals(2.0, evaluator.evaluate("8 / 4"));
        assertEquals(-1.0, evaluator.evaluate("2 - 3"));
    }

    @Test
    void testComplexExpressions() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertEquals(14.0, evaluator.evaluate("2 + 3 * 4"));
        assertEquals(20.0, evaluator.evaluate("(2 + 3) * 4"));
        assertEquals(5.0, evaluator.evaluate("10 / (5 - 3)"));
    }

    @Test
    void testVariables() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        System.setIn(new ByteArrayInputStream("5\n3\n".getBytes())); // Mock user input
        assertEquals(13.0, evaluator.evaluate("2 * x + y"));
    }

    @Test
    void testInvalidExpressions() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("2 + * 3"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("(2 + 3"));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("2 3"));
    }
}
