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

import java.lang.reflect.Method;
import static net.saga.diy.lisp.parser.Parser.parse;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        Class<?> anonymous = Compiler.compile(parse("()"));
        Assert.assertEquals("anonymous", anonymous.getSimpleName().toLowerCase());
        Assert.assertEquals(1, anonymous.getConstructors().length);
    }

    @Test
    /**
     * The compiler should generate a main method which can be called. This
     * method will return a list or an atom representing the evaluated
     * expressions.
     */
    public void testCompilerGeneratesMainMethod() throws NoSuchMethodException {
        Class<?> anonymous = Compiler.compile(parse("()"));
        Method mainMethod = anonymous.getMethod("main", (Class<?>[]) null);
        Assert.assertNotNull(mainMethod);
        Assert.assertEquals(Object.class, (Class<Object>) mainMethod.getReturnType());
    }

    @Test
    /**
     * If you execute the main method of the compiled class, it should return a
     * value.
     */
    public void testCompileBoolean() {
        assertTrue((boolean) run("#t"));
        assertFalse((boolean) run("#f"));
    }

    public static Object run(String program) {
        throw new NotImplementedException();
    }

}
