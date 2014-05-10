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

public class VariablesTest {

    @Test
    public void testSimpleLookup() {
        Environment env = new Environment(
                vars("var", 42)
                );
        assertEquals(42, env.lookup("var"));
    }

    @Test(expected = LispException.class)
    public void testLookupOnMissingRaisesException() {
        new Environment().lookup("missingVar");
    }

    @Test
    public void testLookupFromInner() {
        Environment env = new Environment(vars("foo", 42)).extend("bar", true);
        assertEquals(42, env.lookup("foo"));
        assertTrue((boolean) env.lookup("bar"));
    }

    @Test
    public void testLookupFromDeep() {
        Environment env = new Environment(vars("a", 1)).extend("b", true).extend("c", 3).extend("foo", 100);
        assertEquals(100, env.lookup("foo"));

    }

    @Test
    public void extendIsNewEnv() {
        Environment env = new Environment(vars("foo", 1));
        Environment extended = env.extend("foo", 2);

        assertEquals(1, env.lookup("foo"));
        assertEquals(2, extended.lookup("foo"));

    }

    @Test
    public void test_set_changes_environment_in_place() {
        Environment env = new Environment();
        env.set("foo", 2);
        assertEquals(2, env.lookup("foo"));

    }

    @Test(expected = LispException.class)
    public void test_redefine_variables_illegal() {

        Environment env = new Environment(vars("foo", 1));
        env.set("foo", 2);
    }

    @Test
    public void evaluateSymbol() {
        Environment env = new Environment(vars("foo", 42));
        assertEquals(42, Evaluator.evaluate(Parser.parse("foo"), env));
    }

    @Test(expected = LispException.class)
    public void lookupMissingVariable() {
        Environment env = new Environment();
        assertEquals(42, Evaluator.evaluate(Parser.parse("foo"), env));
    }

    @Test
    public void testDefine() {
        Environment env = new Environment();
        evaluate(parse("(define x 1000)"), env);
        assertEquals(1000, env.lookup("x"));
    }

    @Test(expected = LispException.class)
    public void testDefineArguments() {
        evaluate(parse("(define x) 4"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testDefineArguments2() {
        evaluate(parse("(define x 1 2)"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testDefineWithNonSymbolAsVariable() {
        evaluate(parse("(define #2 42)"), new Environment());
    }

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
