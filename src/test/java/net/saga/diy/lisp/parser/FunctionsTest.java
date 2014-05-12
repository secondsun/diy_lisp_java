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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.saga.diy.lisp.parser.AST.Token;
import static net.saga.diy.lisp.parser.AST.Token.create;
import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import static net.saga.diy.lisp.parser.Parser.parse;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FunctionsTest {

    @Test
    public void testLambdaEvaluateToLambda() {
        Token ast = parse("(lambda () 42)");
        Closure closure = (Closure) evaluate(ast, new Environment());
        assertTrue(closure instanceof Closure);
    }

    @Test
    public void testLambdaKeepsDefiningEnv() {

        Environment env = new Environment(map(entry("foo", 1), entry("bar", 2)));
        Token ast = parse("(lambda () 42)");
        Closure closure = (Closure) evaluate(ast, env);
        assertEquals(env, closure.getEnv());
    }

    @Test
    public void testLambdaClosureHoldsFunction() {
        Token ast = parse("(lambda (x y) (+ x y))");
        Closure closure = (Closure) evaluate(ast, new Environment());

        AST expected = new AST(create(String.class, "+"), create(String.class, "x"), create(String.class, "y"));

        assertEquals(Lists.newArrayList("x", "y"), closure.getParams());
        assertEquals(expected, closure.getBody());

    }

    @Test
    public void testLambdaArgumentsAreList() {

        Closure closure = (Closure) evaluate(parse("(lambda (x y) (+ x y))"), new Environment());

        assertTrue(closure.getParams() instanceof List);
    }

    @Test(expected = LispException.class)
    public void testLambdaFails() {
        evaluate(parse("(lambda not-a-list (body of fn))"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testLambdaNumbeOfArguments() {
        evaluate(parse("(lambda (foo) (bar) (baz))"), new Environment());
    }

    @Test
    public void testBodyIsNotEvaluated() {
        evaluate(parse("(lambda (foo) (function body ((that) would never) work))"), new Environment());
    }

    @Test
    public void testCallToClosure() {
        Closure closure = (Closure) evaluate(parse("(lambda () (+ 1 2))"), new Environment());
        AST ast = new AST(create(Closure.class, closure));
        assertEquals(3, evaluate(ast, new Environment()));
    }

    @Test
    public void testCallWithVariables() {
        Environment env = new Environment();
        Closure closure = (Closure) evaluate(parse("(lambda (a b) (+ a b))"), env);
        AST ast = new AST(create(Closure.class, closure), create(Integer.class, 4), create(Integer.class, 5));

        assertEquals(9, evaluate(ast, env));
    }

    @Test
    public void testCallToFunctionShouldEvaluateArguments() {
        Environment env = new Environment();
        Closure closure = (Closure) evaluate(parse("(lambda (a) (+ a 5))"), env);
        AST ast = new AST(create(Closure.class, closure), (parse("(if #f 0 (+ 10 10))")));

        assertEquals(25, evaluate(ast, env));
    }

    @Test
    public void testCallToFunctionWithFreeVariables() {
        Environment env = new Environment();
        env.set("y", 1);
        Closure closure = (Closure) evaluate(parse("(lambda (x) (+ x y))"), env);
        AST ast = new AST(create(Closure.class, closure), create(Integer.class, 0));

        env = new Environment();
        env.set("y", 2);

        assertEquals(1, evaluate(ast, env));
    }

    @Test
    public void testSimpleFunctionInEnvironment() {

        Environment env = new Environment();
        env.set("y", 1);

        evaluate(parse("(define add (lambda (x y) (+ x y)))"), env);

        assertTrue(env.lookup("add") instanceof Closure);
        assertEquals(3, evaluate(parse("(add 1 2)"), env));
    }

    @Test
    public void testCallingLambdaDirectly() {
        Token ast = parse("((lambda (x) x) 42)");
        Object result = evaluate(ast, new Environment());
        assertEquals(42, result);
    }

    @Test
    public void testCallingComplexExpressionWhichEvaluatesToFunction() {
        Token ast = parse(" ((if #f\n"
                + "wont-evaluate-this-branch\n"
                + "(lambda (x) (+ x y)))\n"
                + "2)");

        Environment env = new Environment();
        env.set("y", 3);
        assertEquals(5, evaluate(ast, env));
    }

    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException1() {
        evaluate(parse("(#t 'foo 'bar)"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException2() {
        evaluate(parse("(42)"), new Environment());
    }

    @Test
    public void testArgumentsAreEvaluated() {
        assertEquals(3, evaluate(parse("((lambda (x) x) (+ 1 2))"), new Environment()));
    }

    @Test(expected = LispException.class)
    public void testCallingWithWrongNumberOfArguments() {
        Environment env = new Environment();

        evaluate(parse("(define fn (lambda (p1 p2) 'whatwever))"), env);

        evaluate(parse("(fn 1 2 3)"), env);
    }

    @Test
    public void testRecursion() {
        Environment env = new Environment();

        evaluate(parse(" (define my-fn\n"
                + ";; A meaningless, but recursive, function\n"
                + "(lambda (x)\n"
                + "(if (eq x 0)\n"
                + "42\n"
                + "(my-fn (- x 1)))))"), env);

        assertEquals(42, evaluate(parse("(my-fn 0)"), env));
        assertEquals(42, evaluate(parse("(my-fn 10)"), env));
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
