/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This project is based on, borrows heavily from, and copies the documentation of
 * https://github.com/kvalle/diy-lisp/
 */
package net.saga.diy.lisp.parser;

import java.util.HashMap;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/*Before we go on to evaluating programs using variables, we need to implement
 an envionment to store them in.

 It is time to fill in the blanks in the `Environment` class */
public class VariablesTest {

    /* An environment should store variables and provide lookup. */
    @Test
    public void testSimpleLookup() {
        Environment env = new Environment(
                vars("var", 42)
                );
        assertEquals(42, env.lookup("var"));
    }

    /*
     * When looking up an undefined symbol, an error should be raised.
     * 
     * The error message should contain the relevant symbol, and inform that it has
     * not been defined.
     */
    @Test(expected = LispException.class)
    public void testLookupOnMissingRaisesException() {
        new Environment().lookup("missingVar");
    }

    /* The `extend` function returns a new environment extended with more bindings. */
    @Test
    public void testLookupFromInner() {
        Environment env = new Environment(vars("foo", 42)).extend("bar", true);
        assertEquals(42, env.lookup("foo"));
        assertTrue((boolean) env.lookup("bar"));
    }

    /* Extending overwrites old bindings to the same variable name. */
    @Test
    public void testLookupFromDeep() {
        Environment env = new Environment(vars("a", 1)).extend("b", true).extend("c", 3).extend("foo", 100);
        assertEquals(100, env.lookup("foo"));

    }

    /* The extend method should create a new environment, leaving the old one unchanged */
    @Test
    public void extendIsNewEnv() {
        Environment env = new Environment(vars("foo", 1));
        Environment extended = env.extend("foo", 2);

        assertEquals(1, env.lookup("foo"));
        assertEquals(2, extended.lookup("foo"));

    }

    /* When calling `set` the environment should be updated */
    @Test
    public void test_set_changes_environment_in_place() {
        Environment env = new Environment();
        env.set("foo", 2);
        assertEquals(2, env.lookup("foo"));

    }

    /*
     * Variables can only be defined once.
     * 
     * Setting a variable in an environment where it is already defined should result
     * in an appropriate error.
     */
    @Test(expected = LispException.class)
    public void test_redefine_variables_illegal() {

        Environment env = new Environment(vars("foo", 1));
        env.set("foo", 2);
    }

    /*
     * """
     * With the `Environment` working, it's time to implement evaluation of expressions
     * with variables.
     * """
     */
    /*
     * Symbols (other than #t and #f) are treated as variable references.
     * 
     * When evaluating a symbol, the corresponding value should be looked up in the
     * environment.
     */
    @Test
    public void evaluateSymbol() {
        Environment env = new Environment(vars("foo", 42));
        assertEquals(42, Evaluator.evaluate(Parser.parse("foo"), env));
    }

    /*
     * Referencing undefined variables should raise an appropriate exception.
     * 
     * This test should already be working if you implemented the environment correctly.
     */
    @Test(expected = LispException.class)
    public void lookupMissingVariable() {
        Environment env = new Environment();
        assertEquals(42, Evaluator.evaluate(Parser.parse("foo"), env));
    }

    @Test
    /*
     * Test of simple define statement.
     * 
     * The `define` form is used to define new bindings in the environment.
     * A `define` call should result in a change in the environment. What you
     * return from evaluating the definition is not important
     */
    public void testDefine() {
        Environment env = new Environment();
        evaluate(parse("(define x 1000)"), env);
        assertEquals(1000, env.lookup("x"));
    }

    /* Defines should have exactly two arguments, or raise an error */
    @Test(expected = LispException.class)
    public void testDefineArguments() {
        evaluate(parse("(define x) 4"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testDefineArguments2() {
        evaluate(parse("(define x 1 2)"), new Environment());
    }

    /* Defines require the first argument to be a symbol. */
    @Test(expected = LispException.class)
    public void testDefineWithNonSymbolAsVariable() {
        evaluate(parse("(define #2 42)"), new Environment());
    }

    /*
     * Test define and lookup variable in same environment.
     * 
     * This test should already be working when the above ones are passing.
     */
    @Test
    public void testLookupAfterDefine() {
        Environment env = new Environment();
        evaluate(parse("(define foo (+ 2 2))"), env);
        assertEquals(4, evaluate(parse("foo"), env));
    }

    /*
     * 
     * 
     * def test_variable_lookup_after_define():
     * """Test define and lookup variable in same environment.
     * 
     * This test should already be working when the above ones are passing."""
     * 
     * env = Environment()
     * evaluate(parse("(define foo (+ 2 2))"), env)
     * assert_equals(4, evaluate("foo", env))
     */
    private HashMap vars(String foo, Object i) {
        HashMap<String, Object> var = new HashMap<String, Object>();
        var.put(foo, i);
        return var;
    }
}
