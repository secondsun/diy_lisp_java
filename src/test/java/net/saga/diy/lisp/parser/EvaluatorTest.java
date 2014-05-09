package net.saga.diy.lisp.parser;

import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class EvaluatorTest {
    
    private static Environment ENV = new Environment();
    
    @Test
    public void testEvaluatingBoolean() {
        assertTrue((boolean) run("#t"));
        assertFalse((boolean) run("#f"));
    }
    
    @Test
    public void testEvaluateInteger() {
        assertEquals(42, (int) run("42"));
    }
    
    @Test
    public void evaluateQuote() {
        assertEquals("foo", run("( quote foo)"));
        assertArrayEquals(new Object[] {1, 2, false}, (Object[])run("( quote (1 2 #f))"));
    }
    
    @Test
    public void evaluateAtom() {
        assertTrue((boolean) run("(atom #t)"));
        assertTrue((boolean) run("(atom #f)"));
        assertTrue((boolean) run("(atom 42)"));
        assertTrue((boolean) run("(atom 'foo)"));
        assertFalse((boolean) run("(atom '(1 2))"));
    }

    @Test
    public void evaluateEq() {
        assertTrue((boolean) run("(eq 1 1)"));
        assertFalse((boolean) run("(eq 1 2)"));
        assertTrue((boolean) run("(eq 'foo 'foo)"));
        assertFalse((boolean) run("(eq 'foo 'bar)"));
        assertFalse((boolean) run("(eq '(1 2 3) '(1 2 3))"));
    }

    
    private Object run(String program) {
        return Evaluator.evaluate(parse(program), ENV);
    }
}
