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
package net.saga.diy.lisp.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.saga.diy.lisp.LispCompiler;
import static net.saga.diy.lisp.Parser.parse;
import static net.saga.diy.lisp.parser.EvaluatorTest.run;
import net.saga.diy.lisp.types.LispException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author summers
 */
public class CompilerTest {

    @Test
    /**
     * Java Bytecode is based around executing classes. Our program should be in
     * an class named anonymous with a default constructor.
     */
    public void testCompilerGeneratesAnonymousClass() {
        Class<?> anonymous = LispCompiler.compile(parse("()"));
        Assert.assertEquals("anonymous", anonymous.getSimpleName().toLowerCase());
        Assert.assertEquals(1, anonymous.getConstructors().length);
    }

    @Test
    /**
     * The compiler should generate a main method which can be called. This
     * method will return a list or an atom representing the compiled
     * expressions.
     */
    public void testCompilerGeneratesMainMethod() throws NoSuchMethodException {
        Class<?> anonymous = LispCompiler.compile(parse("()"));
        Method mainMethod = anonymous.getMethod("main", (Class<?>[]) null);
        Assert.assertNotNull(mainMethod);
        Assert.assertEquals(Object.class, (Class<Object>) mainMethod.getReturnType());
    }

    @Test
    /**
     * If you execute the main method of the compiled class, it should return a
     * value.
     */
    public void testCompileBoolean() throws Exception {
        assertTrue((boolean) run("#t"));
        assertFalse((boolean) run("#f"));
    }

    /* ...and so should integers. */
    @Test
    public void testCompileInteger() {
        assertEquals(42, (int) run("42"));
    }

    /*
     * When a call is done to the `quote` form, the argument should be returned without
     * being compiled.
     * 
     * (quote foo) -> foo
     */
    @Test
    public void compileQuote() {
        assertEquals("foo", run("( quote foo)"));
        Assert.assertArrayEquals(new Object[]{1, 2, false}, (Object[]) run("(quote (1 2 #f))"));
    }

    /*
     * The `atom` form is used to determine whether an expression is an atom.
     * 
     * Atoms are expressions that are not list, i.e. integers, booleans or symbols.
     * Remember that the argument to `atom` must be compiled before the check is done.
     */
    @Test
    public void compileAtom() {
        assertTrue((boolean) run("(atom #t)"));
        assertTrue((boolean) run("(atom #f)"));
        assertTrue((boolean) run("(atom 42)"));
        assertTrue((boolean) run("(atom 'foo)"));
        assertFalse((boolean) run("(atom '(1 2))"));
    }

    /* The `eq` form is used to check whether two expressions are the same atom. */
    @Test
    public void compileEq() {
        assertTrue((boolean) run("(eq 1 1)"));
        assertFalse((boolean) run("(eq 1 2)"));
        assertTrue((boolean) run("(eq 'foo 'foo)"));
        assertFalse((boolean) run("(eq 'foo 'bar)"));
        assertFalse((boolean) run("(eq '(1 2 3) '(1 2 3))"));
    }

    /*
     * To be able to do anything useful, we need some basic math operators.
     * 
     * Since we only operate with integers, `/` must represent integer division.
     * `mod` is the modulo operator.
     */
    @Test
    public void compileMath() {
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
    public void compileNestedMath() {
        assertEquals(8, run("(+ 2 (+ 3 3))"));
        assertEquals(1, run("(- (+ 0 2) 1)"));
        assertEquals(30, run("(* 10 (/ 6 2))"));

    }

    /* The math functions should only allow numbers as arguments. */
    @Test()
    public void mathOnlyUsesInteger() {
        try {
            run("(> 7 'foo)");
        } catch (RuntimeException ex) {
            //You will have to fiddle with this depending on your implementation most likely
            assertTrue(ex.getCause().getCause() instanceof ClassCastException);
            return;
        }
        fail();
    }

    public static Object run(String program) {
        try {
            Class<?> klass = LispCompiler.compile(parse(program));
            Object instance = klass.newInstance();
            Method method = klass.getMethod("main", (Class[]) null);
            return method.invoke(instance, (Object[]) null);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

}
