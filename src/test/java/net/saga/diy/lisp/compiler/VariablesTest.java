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

import java.lang.reflect.InvocationTargetException;
import me.qmx.jitescript.JiteClass;
import net.saga.diy.lisp.LispCompiler;
import static net.saga.diy.lisp.LispCompiler.compile;
import static net.saga.diy.lisp.LispCompiler.compileBlock;
import static net.saga.diy.lisp.Parser.parse;
import net.saga.diy.lisp.types.CompilerContext;
import net.saga.diy.lisp.types.LispException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/*

 This class will include variables but also define the compiler context which 
 will be used to, among other things, help with compile time checks 
 of the code.
 */
public class VariablesTest {

    /* An environment should store variables and provide lookup. */
    @Test
    public void testSimpleLookup() {
        CompilerContext context = new CompilerContext();
        context.defineVariable("var", 42);
        assertEquals(42, context.lookup("var"));

    }

    /*
     * When looking up an undefined symbol, an error should be raised.
     * 
     * The error message should contain the relevant symbol, and inform that it has
     * not been defined.
     */
    @Test(expected = LispException.class)
    public void testLookupOnMissingRaisesException() {
        new CompilerContext().lookup("missingVar");
    }

    /* The `extend` function returns a new environment extended with more bindings. */
    @Test
    public void testLookupFromInner() {
        CompilerContext env = new CompilerContext().defineVariable("foo", 42).extend("bar", true);
        assertEquals(42, env.lookup("foo"));
        assertTrue((boolean) env.lookup("bar"));

    }

    /* Extending overwrites old bindings to the same variable name. */
    @Test
    public void testLookupFromDeep() {
        CompilerContext env = new CompilerContext().defineVariable("a", 1).extend("b", true).extend("c", 3).extend("foo", 100);
        assertEquals(100, env.lookup("foo"));

    }

    /* The extend method should create a new environment, leaving the old one unchanged */
    @Test
    public void extendIsNewEnv() {
        CompilerContext env = new CompilerContext().defineVariable("foo", 1);
        CompilerContext extended = env.extend("foo", 2);

        assertEquals(1, env.lookup("foo"));
        assertEquals(2, extended.lookup("foo"));

    }

    /*
     * Variables can only be defined once.
     * 
     * Setting a variable in an environment where it is already defined should result
     * in an appropriate error.
     */
    @Test(expected = LispException.class)
    public void test_redefine_variables_illegal() {
        compile("((define x 4)(define x 4))");
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
        assertEquals(42, CompilerTest.run("((define foo 42)(foo))"));
    }

    /*
     * Referencing undefined variables should raise an appropriate exception.
     * 
     * This test should already be working if you implemented the environment correctly.
     */
    @Test(expected = LispException.class)
    public void lookupMissingVariable() {
        assertEquals(42, compile(parse("foo")));
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
        CompilerContext env = new CompilerContext();
        JiteClass jiteclass = LispCompiler.compileMethod(parse("(define x 1000)"), env, "main");
        env.lookup("x");
    }

    /* Defines should have exactly two arguments, or raise an error */
    @Test(expected = LispException.class)
    public void testDefineArguments() {
        compile(parse("(define x) 4"));
    }

    @Test(expected = LispException.class)
    public void testDefineArguments2() {
        compile(parse("(define x 1 2)"));
    }

    /* Defines require the first argument to be a symbol. */
    @Test(expected = LispException.class)
    public void testDefineWithNonSymbolAsVariable() {
        compile(parse("(define #2 42)"));
    }

    /*
     * Test define and lookup variable in same environment.
     * 
     * This test should already be working when the above ones are passing.
     */
    @Test
    public void testLookupAfterDefine() throws IllegalAccessException, InstantiationException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        CompilerContext env = new CompilerContext();
        compileBlock(parse("(define foo (+ 2 2))"), env);
        JiteClass compiledClass = LispCompiler.compileMethod(parse("foo"), env, "main");
        Class klass = new LispCompiler.DynamicClassLoader().define(compiledClass);
        Object instance = klass.newInstance();
        Object result = klass.getMethod("main", null).invoke(instance, null);
        assertEquals(4, result);

    }

}
