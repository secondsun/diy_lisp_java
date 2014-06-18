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
 *
 * This project is based on, borrows heavily from, and copies the documentation
 * of https://github.com/kvalle/diy-lisp/
 */
package net.saga.diy.lisp.compiler;

import com.google.common.collect.Maps;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.qmx.jitescript.JiteClass;
import net.saga.diy.lisp.LispCompiler;
import static net.saga.diy.lisp.Parser.parse;
import net.saga.diy.lisp.types.CompiledClosure;
import net.saga.diy.lisp.types.CompilerContext;
import net.saga.diy.lisp.types.LispException;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/*This part is all about defining and using functions.

 We'll start by implementing the `lambda` form which is used to create function closures.*/
public class FunctionsTest {

    /* The lambda form should evaluate to a Closure */
    @Test
    public void testLambdaCompilesToLambda() {
        Object ast = (Object[]) parse("(lambda () 42)");
        CompilerContext context = new CompilerContext();
        LispCompiler.compileBlock(ast, context);
        Class klass = toClass(context);
        assertEquals(1, klass.getDeclaredClasses().length);
    }

    /*
     * The closure should keep a copy of the environment where it was defined.
     * 
     * Once we start calling functions later, we'll need access to the environment
     * from when the function was created in order to resolve all free variables.
     */
    @Test
    public void testLambdaKeepsDefiningEnv() throws Exception {

        CompilerContext env = new CompilerContext().defineVariable("foo", 42);

        Object ast = (Object[]) parse("(lambda () foo)");
        LispCompiler.compileBlock(ast, env);
        Object klass = toInstance(env);
        Object childKlass = childInstance(env, klass);
        assertEquals(42, childKlass.getClass().getMethod("lambda", null).invoke(childKlass, null));
    }



    /* The `lambda` form should expect exactly two arguments. */
    @Test(expected = LispException.class)
    public void testLambdaNumbeOfArguments() {
        
        Object[] ast = (Object[]) parse("(lambda (foo) (bar) (baz))");
        
        CompilerContext env = new CompilerContext().defineVariable("foo", 42);
        CompiledClosure closure = (CompiledClosure) LispCompiler.compileBlock(ast, env);

    }

    /* The parameters of a `lambda` should be a list. */
    @Test
    public void testLambdaArgumentsAreList() {

//        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (x y) (+ x y))"), new Environment());
//
//        assertTrue(isList(closure.getParams()));
        throw new RuntimeException("Not implemented");
    }

    @Test(expected = LispException.class)
    public void testLambdaFails() {
        Object[] ast = (Object[]) parse("(lambda not-a-list (body of fn))");
        CompilerContext env = new CompilerContext().defineVariable("foo", 42);
        CompiledClosure closure = (CompiledClosure) LispCompiler.compileBlock(ast, env);
    }


    /**
     * @TODO: UPdate doc
     * 
     * One of the tricks is that a body IS cmpiled as opposed to the evaluator where it is interpreted lazily.
    */
    @Test(expected = LispException.class)
    public void testBodyIsCompiled() {
        Object[] ast = (Object[]) parse("(lambda (foo) (function body ((that) would never) work))");
        CompilerContext env = new CompilerContext().defineVariable("foo", 42);
        CompiledClosure closure = (CompiledClosure) LispCompiler.compileBlock(ast, env);
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
     * The function body must be evaluated in an environment where the parameters are bound.
     * 
     * Create an environment where the function parameters (which are stored in the closure)
     * are bound to the actual argument values in the function call. Use this environment
     * when evaluating the function body.
     */
    @Test
    public void testLambdaClosureWithParams() throws Exception {
        Object ast = (Object[]) parse("(lambda (x y) (+ x y))");
        CompilerContext env = new CompilerContext().defineVariable("x", 42);
        CompiledClosure closure = (CompiledClosure) LispCompiler.compileBlock(ast, env);
        assertArrayEquals(new Object[]{"x", "y"}, closure.getParams());

        Object klass = toInstance(env);
        Object childInstance = childInstance(env, klass);
        Class<?> childClass = childInstance.getClass();

        childClass.getField("x").set(childInstance, 21);
        childClass.getField("y").set(childInstance, 21);
        Method lambdaMethod = childInstance.getClass().getMethod("lambda", new Class<?>[0]);

        assertEquals(42, lambdaMethod.invoke(childInstance, new Object[0]));
    }

    @Test
    public void testCallLambdaClosure() throws Exception {
        Object ast = (Object[]) parse("((lambda () 42))");
        
        Class klass = LispCompiler.compile(ast);

        Object instance = klass.newInstance();
        
        Method lambdaMethod = klass.getMethod("main", new Class<?>[0]);

        assertEquals(42, lambdaMethod.invoke(instance, new Object[0]));
    }
    
    /*
     * Call to function should evaluate all arguments.
     * 
     * When a function is applied, the arguments should be evaluated before being bound
     * to the parameter names.
     */
    @Test
    public void testCallToFunctionShouldEvaluateArguments() {
//        Environment env = new Environment();
//        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (a) (+ a 5))"), env);
//
//        List<Object> list = new ArrayList(2);
//        list.add(closure);
//        list.add(parse("(if #f 0 (+ 10 10))"));
//        assertEquals(25, evaluate(list.toArray(), env));
        throw new RuntimeException("Not implemented");
        
        
        
        
        
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
//        Environment env = new Environment();
//        env.set("y", 1);
//        Closure closure = (Closure) evaluate((Object[]) parse("(lambda (x) (+ x y))"), env);
//        Object[] ast = new Object[]{closure, 0};
//
//        env = new Environment();
//        env.set("y", 2);
//
//        assertEquals(1, evaluate(ast, env));
        throw new RuntimeException("Not implemented");
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

//        Environment env = new Environment();
//        env.set("y", 1);
//
//        evaluate((Object[]) parse("(define add (lambda (x y) (+ x y)))"), env);
//
//        assertTrue(env.lookup("add") instanceof Closure);
//        assertEquals(3, evaluate((Object[]) parse("(add 1 2)"), env));
        throw new RuntimeException("Not implemented");
    }

    /*
     * It should be possible to define and call functions directly.
     * 
     * A lambda definition in the call position of an AST should be evaluated, and then
     * evaluated as before.
     */
    @Test
    public void testCallingLambdaDirectly() {
//        Object[] ast = (Object[]) (Object[]) parse("((lambda (x) x) 42)");
//        Object result = evaluate(ast, new Environment());
//        assertEquals(42, result);
        throw new RuntimeException("Not implemented");
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
//        Object[] ast = (Object[]) (Object[]) parse(" ((if #f\n"
//                + "wont-evaluate-this-branch\n"
//                + "(lambda (x) (+ x y)))\n"
//                + "2)");
//
//        Environment env = new Environment();
//        env.set("y", 3);
//        assertEquals(5, evaluate(ast, env));
        throw new RuntimeException("Not implemented");
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
//        evaluate((Object[]) parse("(#t 'foo 'bar)"), new Environment());
        throw new RuntimeException("Not implemented");
    }

    @Test(expected = LispException.class)
    public void testCallingAtomRaisesException2() {
//        evaluate((Object[]) parse("(42)"), new Environment());
        throw new RuntimeException("Not implemented");
    }

    /*
     * The arguments passed to functions should be evaluated
     * 
     * We should accept parameters that are produced through function
     * calls.
     */
    @Test
    public void testArgumentsAreEvaluated() {
//        assertEquals(3, evaluate((Object[]) parse("((lambda (x) x) (+ 1 2))"), new Environment()));
        throw new RuntimeException("Not implemented");
    }

    /* Functions should raise exceptions when called with wrong number of arguments. */
    @Test(expected = LispException.class)
    public void testCallingWithWrongNumberOfArguments() {
//        Environment env = new Environment();
//
//        evaluate((Object[]) parse("(define fn (lambda (p1 p2) 'whatwever))"), env);
//
//        evaluate((Object[]) parse("(fn 1 2 3)"), env);
        throw new RuntimeException("Not implemented");
    }

    /*
     * One final test to see that recursive functions are working as expected.
     * The good news: this should already be working by now :)
     */
    @Test
    public void testRecursion() {
//        Environment env = new Environment();
//
//        evaluate((Object[]) parse(" (define my-fn\n"
//                + ";; A meaningless, but recursive, function\n"
//                + "(lambda (x)\n"
//                + "(if (eq x 0)\n"
//                + "42\n"
//                + "(my-fn (- x 1)))))"), env);
//
//        assertEquals(42, evaluate((Object[]) parse("(my-fn 0)"), env));
//        assertEquals(42, evaluate((Object[]) parse("(my-fn 10)"), env));
        throw new RuntimeException("Not implemented");
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

    private Class toClass(CompilerContext context) {
        return new LispCompiler.DynamicClassLoader().define(context.jiteClass);
    }

    private Object toInstance(CompilerContext context) {
        try {
            JiteClass compiledClass = context.jiteClass;
            Class klass = new LispCompiler.DynamicClassLoader().define(compiledClass);
            Object instance = klass.newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(FunctionsTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    private Object childInstance(CompilerContext env, Object parentInstance) throws Exception {
        Class<?> childClass = parentInstance.getClass().getDeclaredClasses()[0];
        Constructor<?> constructor = childClass.getConstructor(Object.class);
        return constructor.newInstance(parentInstance);
    }
}
