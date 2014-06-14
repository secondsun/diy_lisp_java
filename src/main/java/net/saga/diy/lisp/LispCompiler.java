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
package net.saga.diy.lisp;

import java.io.FileOutputStream;
import me.qmx.jitescript.CodeBlock;
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ACC_STATIC;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.c;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static net.saga.diy.lisp.SpecialTokens.ATOM;
import static net.saga.diy.lisp.SpecialTokens.EQ;
import static net.saga.diy.lisp.SpecialTokens.IF;
import static net.saga.diy.lisp.SpecialTokens.QUOTE;
import net.saga.diy.lisp.compiler.operation.AtomOperation;
import net.saga.diy.lisp.compiler.operation.EqOperation;
import net.saga.diy.lisp.compiler.operation.IfOperation;
import net.saga.diy.lisp.compiler.operation.MathOperation;
import net.saga.diy.lisp.compiler.operation.Operation;
import net.saga.diy.lisp.compiler.operation.QuoteOperation;
import net.saga.diy.lisp.types.LispException;

/**
 *
 * @author summers
 */
public class LispCompiler {

    private static class DynamicClassLoader extends ClassLoader {

        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes();

            try (FileOutputStream fos = new FileOutputStream("/tmp/anonymous.class")) {
                fos.write(classBytes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }

    public static Class<?> compile(Object parse) {
        JiteClass jiteClass = new JiteClass("anonymous") {
            {
                defineDefaultConstructor();
            }
        };
        JiteClass klass = compile(parse, jiteClass, "main");
        Class<?> compiledClass = new DynamicClassLoader().define(klass);

        return compiledClass;
    }

    public static JiteClass compile(Object parse, JiteClass parentClass, String methodName) {

        if (parse.getClass() == Boolean.class) {
            if ((Boolean) parse) {
                parentClass.defineMethod(methodName, ACC_PUBLIC , CodegenUtils.sig(Object.class),
                        newCodeBlock().ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn());
            } else {
                parentClass.defineMethod(methodName, ACC_PUBLIC , CodegenUtils.sig(Object.class),
                        newCodeBlock().ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn());
            }

        } else if (parse.getClass() == Integer.class) {

            parentClass.defineMethod(methodName, ACC_PUBLIC , CodegenUtils.sig(Object.class),
                    newCodeBlock().ldc((int) parse).invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class)).areturn());

        } else if (parse.getClass().isArray()) {
            Object[] ast = (Object[]) parse;
            Operation operation = null;
            int length = ast.length;

            if (length == 0) {
                parentClass.defineMethod(methodName, ACC_PUBLIC , CodegenUtils.sig(Object.class),
                        newCodeBlock().aconst_null().areturn());
            }

            for (int pointer = 0; pointer < length; pointer++) {
                Object token = ast[pointer];

                if (token.getClass() == String.class) {
                    if (QUOTE.equals(token)) {
                        operation = new QuoteOperation();
                    } else if (ATOM.equals(token)) {
                        operation = new AtomOperation();
                    } else if (EQ.equals(token)) {
                        operation = new EqOperation();
                    } else if (IF.equals(token)) {
                        operation = new IfOperation();
                    } else if (SpecialTokens.MATHS.contains(token)) {
                        operation = new MathOperation(token);
                    } else {
                        throw new RuntimeException("Not implemented");
                    }

                    Object res = operation.compile(ast[++pointer], parentClass);

                    while (res instanceof Operation) {
                        if ((pointer + 1) >= ast.length) {
                            throw new LispException("Missing token");
                        }
                        res = ((Operation) res).compile(ast[++pointer], parentClass);
                    }
                    parentClass.defineMethod(methodName, ACC_PUBLIC , CodegenUtils.sig(Object.class), (CodeBlock) res);

                } else {
                    throw new RuntimeException("Not implemented");
                }
            }

        } else {
            parentClass.defineMethod(methodName, ACC_PUBLIC , CodegenUtils.sig(Object.class),
                    newCodeBlock().aconst_null().areturn());
        }

        return parentClass;
    }

}
