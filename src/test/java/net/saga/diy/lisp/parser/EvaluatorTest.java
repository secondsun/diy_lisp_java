/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.saga.diy.lisp.parser;

import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class EvaluatorTest {

    private static Environment ENV = new Environment();

    /*Booleans should evaluate to themselves*/
    @Test
    public void testEvaluatingBoolean() {
        assertTrue((boolean) run("#t"));
        assertFalse((boolean) run("#f"));
    }

    /*...and so should integers.*/
    @Test
    public void testEvaluateInteger() {
        assertEquals(42, (int) run("42"));
    }

    /*When a call is done to the `quote` form, the argument should be returned without
     being evaluated.

     (quote foo) -> foo*/
    @Test
    public void evaluateQuote() {
        assertEquals("foo", run("( quote foo)"));
        Assert.assertArrayEquals(new Object[]{1, 2, false}, (Object[]) run("(quote (1 2 #f))"));
    }

    /*The `atom` form is used to determine whether an expression is an atom.

     Atoms are expressions that are not list, i.e. integers, booleans or symbols.
     Remember that the argument to `atom` must be evaluated before the check is done.*/
    @Test
    public void evaluateAtom() {
        assertTrue((boolean) run("(atom #t)"));
        assertTrue((boolean) run("(atom #f)"));
        assertTrue((boolean) run("(atom 42)"));
        assertTrue((boolean) run("(atom 'foo)"));
        assertFalse((boolean) run("(atom '(1 2))"));
    }

    /*The `eq` form is used to check whether two expressions are the same atom.*/
    @Test
    public void evaluateEq() {
        assertTrue((boolean) run("(eq 1 1)"));
        assertFalse((boolean) run("(eq 1 2)"));
        assertTrue((boolean) run("(eq 'foo 'foo)"));
        assertFalse((boolean) run("(eq 'foo 'bar)"));
        assertFalse((boolean) run("(eq '(1 2 3) '(1 2 3))"));
    }

    /*To be able to do anything useful, we need some basic math operators.

     Since we only operate with integers, `/` must represent integer division.
     `mod` is the modulo operator.
     */
    @Test
    public void evaluateMath() {
        assertEquals(4, run("(+ 2 2)"));
        assertEquals(1, run("(- 2 1)"));
        assertEquals(3, run("(/ 6 2)"));
        assertEquals(3, run("(/ 7 2)"));
        assertEquals(6, run("(* 2 3)"));
        assertEquals(1, run("(mod 7 2)"));
        assertEquals(true, run("(> 7 2)"));
        assertEquals(false, run("(< 7 2)"));
        assertEquals(false, run("(> 7 7)"));
    }

    @Test
    public void evaluateNestedMath() {
        assertEquals(8, run("(+ 2 (+ 3 3))"));
        assertEquals(1, run("(- (+ 0 2) 1)"));
        assertEquals(30, run("(* 10 (/ 6 2))"));

    }

    /*The math functions should only allow numbers as arguments.*/
    @Test(expected = LispException.class)
    public void mathOnlyUsesInteger() {
        assertEquals(false, run("> 7 'foo"));
    }

    public static Object run(String program) {
        return Evaluator.evaluate(parse(program), ENV);
    }
}
