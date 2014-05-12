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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static net.saga.diy.lisp.parser.types.Utils.isList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FunctionsTest {

    @Test
    public void testLambdaEvaluateToLambda() {
        Object ast = (Object[])parse("(lambda () 42)");
        Closure closure = (Closure) evaluate(ast, new Environment());
        assertTrue(closure instanceof Closure);
    }

    @Test
    public void testLambdaKeepsDefiningEnv() {

        Environment env = new Environment(map(entry("foo", 1), entry("bar", 2)));
        Object ast = (Object[])parse("(lambda () 42)");
        Closure closure = (Closure) evaluate(ast, env);
        assertEquals(env, closure.getEnv());
    }

    @Test
    public void testLambdaClosureHoldsFunction() {
        Object ast = (Object[])parse("(lambda (x y) (+ x y))");
        Closure closure = (Closure) evaluate(ast, new Environment());

        Object[] expected = new Object[] {"+", "x", "y"};

        assertArrayEquals(new Object[]{"x", "y"}, closure.getParams());
        assertArrayEquals(expected, (Object[])closure.getBody());

    }

    @Test
    public void testLambdaArgumentsAreList() {

        Closure closure = (Closure) evaluate((Object[])parse("(lambda (x y) (+ x y))"), new Environment());

        assertTrue(isList(closure.getParams()));
    }

    @Test(expected = LispException.class)
    public void testLambdaFails() {
        evaluate((Object[])parse("(lambda not-a-list (body of fn))"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testLambdaNumbeOfArguments() {
        evaluate((Object[])parse("(lambda (foo) (bar) (baz))"), new Environment());
    }

    @Test
    public void testBodyIsNotEvaluated() {
        evaluate((Object[])parse("(lambda (foo) (function body ((that) would never) work))"), new Environment());
    }

    @Test
    public void testCallToClosure() {
        Closure closure = (Closure) evaluate((Object[])parse("(lambda () (+ 1 2))"), new Environment());
        Object[] ast = new Object[]{closure};
        assertEquals(3, evaluate(ast, new Environment()));
    }

    @Test
    public void testCallWithVariables() {
        Environment env = new Environment();
        Closure closure = (Closure) evaluate((Object[])parse("(lambda (a b) (+ a b))"), env);
        Object[] ast = new Object[]{closure, 4, 5};

        assertEquals(9, evaluate(ast, env));
    }

    @Test
    public void testCallToFunctionShouldEvaluateArguments() {
        Environment env = new Environment();
        Closure closure = (Closure) evaluate((Object[])parse("(lambda (a) (+ a 5))"), env);
        
        List<Object> list = new ArrayList(2);
        list.add(closure);
        list.add(parse("(if #f 0 (+ 10 10))"));
        assertEquals(25, evaluate(list.toArray(), env));
    }

    @Test
    public void testCallToFunctionWithFreeVariables() {
        Environment env = new Environment();
        env.set("y", 1);
        Closure closure = (Closure) evaluate((Object[])parse("(lambda (x) (+ x y))"), env);
        Object[] ast = new Object[]{closure, 0};

        env = new Environment();
        env.set("y", 2);

        assertEquals(1, evaluate(ast, env));
    }

    @Test
    public void testSimpleFunctionInEnvironment() {

        Environment env = new Environment();
        env.set("y", 1);

        evaluate((Object[])parse("(define add (lambda (x y) (+ x y)))"), env);

        assertTrue(env.lookup("add") instanceof Closure);
        assertEquals(3, evaluate((Object[])parse("(add 1 2)"), env));
    }

    @Test
    public void testCallingLambdaDirectly() {
        Object[] ast = (Object[]) (Object[])parse("((lambda (x) x) 42)");
        Object result = evaluate(ast, new Environment());
        assertEquals(42, result);
    }

    @Test
    public void testCallingComplexExpressionWhichEvaluatesToFunction() {
        Object[] ast = (Object[]) (Object[])parse(" ((if #f\n"
                + "wont-evaluate-this-branch\n"
                + "(lambda (x) (+ x y)))\n"
                + "2)");

        Environment env = new Environment();
        env.set("y", 3);
        assertEquals(5, evaluate(ast, env));
    }

    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException1() {
        evaluate((Object[])parse("(#t 'foo 'bar)"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException2() {
        evaluate((Object[])parse("(42)"), new Environment());
    }

    @Test
    public void testArgumentsAreEvaluated() {
        assertEquals(3, evaluate((Object[])parse("((lambda (x) x) (+ 1 2))"), new Environment()));
    }

    @Test(expected = LispException.class)
    public void testCallingWithWrongNumberOfArguments() {
        Environment env = new Environment();

        evaluate((Object[])parse("(define fn (lambda (p1 p2) 'whatwever))"), env);

        evaluate((Object[])parse("(fn 1 2 3)"), env);
    }

    @Test
    public void testRecursion() {
        Environment env = new Environment();

        evaluate((Object[])parse(" (define my-fn\n"
                + ";; A meaningless, but recursive, function\n"
                + "(lambda (x)\n"
                + "(if (eq x 0)\n"
                + "42\n"
                + "(my-fn (- x 1)))))"), env);

        assertEquals(42, evaluate((Object[])parse("(my-fn 0)"), env));
        assertEquals(42, evaluate((Object[])parse("(my-fn 10)"), env));
    }

    private <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new SimpleEntry<K, V>(key, value);
    }

    private <K, V> HashMap<K, V> map(Map.Entry<K, V>... entries) {
        HashMap<K, V> toReturn = Maps.newHashMapWithExpectedSize(entries.length);

        Arrays.stream(entries)
                .forEach(entry -> toReturn.put(entry.getKey(), entry.getValue()));

        return toReturn;
    }
}
