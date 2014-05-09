package net.saga.diy.lisp.parser;

import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
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

    @Test
    public void evaluateMath() { 
        assertEquals(4, run("+ 2 2"));
        assertEquals(1, run("- 2 1"));
        assertEquals(3, run("/ 6 2"));
        assertEquals(3, run("/ 7 2"));
        assertEquals(6, run("* 2 3"));
        assertEquals(1, run("mod 7 2"));
        assertEquals(true, run("> 7 2"));
        assertEquals(false, run("< 7 2"));
        assertEquals(false, run("> 7 7"));
    }
    
    @Test
    public void evaluateNestedMath() { 
        assertEquals(8, run("+ 2 (+ 3 3)"));
        assertEquals(1, run("- (+ 0 2) 1"));
        assertEquals(30, run("* 10 (/ 6 2)"));
        
    }
    
    @Test(expected = LispException.class)
    public void mathOnlyUsesInteger() {
        assertEquals(false, run("> 7 'foo"));
    }
    
    
    /*
    assert_equals(4, evaluate(["+", 2, 2], Environment()))
    assert_equals(1, evaluate(["-", 2, 1], Environment()))
    assert_equals(3, evaluate(["/", 6, 2], Environment()))
    assert_equals(3, evaluate(["/", 7, 2], Environment()))
    assert_equals(6, evaluate(["*", 2, 3], Environment()))
    assert_equals(1, evaluate(["mod", 7, 2], Environment()))
    assert_equals(True, evaluate([">", 7, 2], Environment()))
    assert_equals(False, evaluate([">", 2, 7], Environment()))
    assert_equals(False, evaluate([">", 7, 7], Environment()))
    */
    public static Object run(String program) {
        return Evaluator.evaluate(parse(program), ENV);
    }
}
