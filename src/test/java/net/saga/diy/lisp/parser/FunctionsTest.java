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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/*This part is all about defining and using functions.

 We'll start by implementing the `lambda` form which is used to create function closures.*/
public class FunctionsTest {

    /* The lambda form should evaluate to a Closure */
    @Test
    public void testLambdaEvaluateToLambda() {
        Object ast = (Object[]) parse("(lambda () 42)");
        Closure closure = (Closure) evaluate(ast, new Environment());
        assertTrue(closure instanceof Closure);
    }

    /*
     * The closure should keep a copy of the environment where it was defined.
     * 
     * Once we start calling functions later, we'll need access to the environment
     * from when the function was created in order to resolve all free variables.
     */
    @Test
    public void testLambdaKeepsDefiningEnv() {

        Environment env = new Environment(map(entry("foo", 1), entry("bar", 2)));
        Object ast = (Object[]) parse("(lambda () 42)");
        Closure closure = (Closure) evaluate(ast, env);
        assertEquals(env, closure.getEnv());
    }

    /* The closure contains the parameter list and function body too. */
    @Test
    public void testLambdaClosureHoldsFunction() {
        Object ast = (Object[]) parse("(lambda (x y) (+ x y))");
        Closure closure = (Closure) evaluate(ast, new Environment());

        Object[] expected = new Object[] { "+", "x", "y" };

        assertArrayEquals(new Object[] { "x", "y" }, closure.getParams());
        assertArrayEquals(expected, (Object[]) closure.getBody());

    }

    /* The parameters of a `lambda` should be a list. */
    @Test
    public void testLambdaArgumentsAreList() {

        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (x y) (+ x y))"), new Environment());

        assertTrue(isList(closure.getParams()));
    }

    @Test(expected = LispException.class)
    public void testLambdaFails() {
        evaluate((Object[]) parse("(lambda not-a-list (body of fn))"), new Environment());
    }

    /* The `lambda` form should expect exactly two arguments. */
    @Test(expected = LispException.class)
    public void testLambdaNumbeOfArguments() {
        evaluate((Object[]) parse("(lambda (foo) (bar) (baz))"), new Environment());
    }

    /*
     * The function body should not be evaluated when the lambda is defined.
     * 
     * The call to `lambda` should return a function closure holding, among other things
     * the function body. The body should not be evaluated before the function is called.
     */
    @Test
    public void testBodyIsNotEvaluated() {
        evaluate((Object[]) parse("(lambda (foo) (function body ((that) would never) work))"), new Environment());
    }

    /*
     * """
     * Now that we have the `lambda` form implemented, let's see if we can call some functions.
     * 
     * When evaluating ASTs which are lists, if the first element isn't one of the special forms
     * we have been working with so far, it is a function call. The first element of the list is
     * the function, and the rest of the elements are arguments.
     * """
     */

    /*
     * The first case we'll handle is when the AST is a list with an actual closure
     * as the first element.
     * 
     * In this first test, we'll start with a closure with no arguments and no free
     * variables. All we need to do is to evaluate and return the function body.
     */
    @Test
    public void testCallToClosure() {
        Closure closure = (Closure) evaluate((Object[]) parse("(lambda () (+ 1 2))"), new Environment());
        Object[] ast = new Object[] { closure };
        assertEquals(3, evaluate(ast, new Environment()));
    }

    /*
     * The function body must be evaluated in an environment where the parameters are bound.
     * 
     * Create an environment where the function parameters (which are stored in the closure)
     * are bound to the actual argument values in the function call. Use this environment
     * when evaluating the function body.
     */
    @Test
    public void testCallWithArguments() {
        Environment env = new Environment();
        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (a b) (+ a b))"), env);
        Object[] ast = new Object[] { closure, 4, 5 };

        assertEquals(9, evaluate(ast, env));
    }

    /*
     * Call to function should evaluate all arguments.
     * 
     * When a function is applied, the arguments should be evaluated before being bound
     * to the parameter names.
     */
    @Test
    public void testCallToFunctionShouldEvaluateArguments() {
        Environment env = new Environment();
        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (a) (+ a 5))"), env);

        List<Object> list = new ArrayList(2);
        list.add(closure);
        list.add(parse("(if #f 0 (+ 10 10))"));
        assertEquals(25, evaluate(list.toArray(), env));
    }

    /*
     * The body should be evaluated in the environment from the closure.
     * 
     * The function's free variables, i.e. those not specified as part of the parameter list,
     * should be looked up in the environment from where the function was defined. This is
     * the environment included in the closure. Make sure this environment is used when
     * evaluating the body.
     */
    @Test
    public void testCallToFunctionWithFreeVariables() {
        Environment env = new Environment();
        env.set("y", 1);
        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (x) (+ x y))"), env);
        Object[] ast = new Object[] { closure, 0 };

        env = new Environment();
        env.set("y", 2);

        assertEquals(1, evaluate(ast, env));
    }

    /*
     * """
     * Okay, now we're able to evaluate ASTs with closures as the first element. But normally
     * the closures don't just happen to be there all by themselves. Generally we'll find some
     * expression, evaluate it to a closure, and then evaluate a new AST with the closure just
     * like we did above.
     * 
     * (some-exp arg1 arg2 ...) -> (closure arg1 arg2 ...) -> result-of-function-call
     * 
     * """
     */
    /*
     * A call to a symbol corresponds to a call to its value in the environment.
     * 
     * When a symbol is the first element of the AST list, it is resolved to its value in
     * the environment (which should be a function closure). An AST with the variables
     * replaced with its value should then be evaluated instead
     */
    @Test
    public void testSimpleFunctionInEnvironment() {

        Environment env = new Environment();
        env.set("y", 1);

        evaluate((Object[]) parse("(define add (lambda (x y) (+ x y)))"), env);

        assertTrue(env.lookup("add") instanceof Closure);
        assertEquals(3, evaluate((Object[]) parse("(add 1 2)"), env));
    }

    /*
     * It should be possible to define and call functions directly.
     * 
     * A lambda definition in the call position of an AST should be evaluated, and then
     * evaluated as before.
     */
    @Test
    public void testCallingLambdaDirectly() {
        Object[] ast = (Object[]) (Object[]) parse("((lambda (x) x) 42)");
        Object result = evaluate(ast, new Environment());
        assertEquals(42, result);
    }

    /*
     * Actually, all ASTs that are not atoms should be evaluated and then called.
     * 
     * In this test, a call is done to the if-expression. The `if` should be evaluated,
     * which will result in a `lambda` expression. The lambda is evaluated, giving a
     * closure. The result is an AST with a `closure` as the first element, which we
     * already know how to evaluate.
     */
    @Test
    public void testCallingComplexExpressionWhichEvaluatesToFunction() {
        Object[] ast = (Object[]) (Object[]) parse(" ((if #f\n"
                + "wont-evaluate-this-branch\n"
                + "(lambda (x) (+ x y)))\n"
                + "2)");

        Environment env = new Environment();
        env.set("y", 3);
        assertEquals(5, evaluate(ast, env));
    }

    /*
     * """
     * Now that we have the happy cases working, let's see what should happen when
     * function calls are done incorrectly.
     * """
     */
    /*
     * A function call to a non-function should result in an error
     */
    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException1() {
        evaluate((Object[]) parse("(#t 'foo 'bar)"), new Environment());
    }

    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException2() {
        evaluate((Object[]) parse("(42)"), new Environment());
    }

    /*
     * The arguments passed to functions should be evaluated
     * 
     * We should accept parameters that are produced through function
     * calls.
     */
    @Test
    public void testArgumentsAreEvaluated() {
        assertEquals(3, evaluate((Object[]) parse("((lambda (x) x) (+ 1 2))"), new Environment()));
    }

    /* Functions should raise exceptions when called with wrong number of arguments. */
    @Test(expected = LispException.class)
    public void testCallingWithWrongNumberOfArguments() {
        Environment env = new Environment();

        evaluate((Object[]) parse("(define fn (lambda (p1 p2) 'whatwever))"), env);

        evaluate((Object[]) parse("(fn 1 2 3)"), env);
    }

    /*
     * One final test to see that recursive functions are working as expected.
     * The good news: this should already be working by now :)
     */
    @Test
    public void testRecursion() {
        Environment env = new Environment();

        evaluate((Object[]) parse(" (define my-fn\n"
                + ";; A meaningless, but recursive, function\n"
                + "(lambda (x)\n"
                + "(if (eq x 0)\n"
                + "42\n"
                + "(my-fn (- x 1)))))"), env);

        assertEquals(42, evaluate((Object[]) parse("(my-fn 0)"), env));
        assertEquals(42, evaluate((Object[]) parse("(my-fn 10)"), env));
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
