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
import static net.saga.diy.lisp.SpecialTokens.ATOM;
import static net.saga.diy.lisp.SpecialTokens.QUOTE;
import net.saga.diy.lisp.compiler.operation.AtomOperation;
import net.saga.diy.lisp.compiler.operation.Operation;
import net.saga.diy.lisp.compiler.operation.QuoteOperation;

/**
 *
 * @author summers
 */
public class Compiler {

    private static class DynamicClassLoader extends ClassLoader {

        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes();
            return super.defineClass(c(jiteClass.getClassName()), classBytes, 0, classBytes.length);
        }
    }

    public static Class<?> compile(Object parse) {
        JiteClass jiteClass = new JiteClass("anonymous") {
            {
                defineDefaultConstructor();
            }
        };

        if (parse.getClass() == Boolean.class) {
            if ((Boolean) parse) {
                jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(boolean.class),
                        newCodeBlock().iconst_1().ireturn());
            } else {
                jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(boolean.class),
                        newCodeBlock().iconst_0().ireturn());
            }

        } else if (parse.getClass() == Integer.class) {

            jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(int.class),
                    newCodeBlock().ldc((int) parse).ireturn());

        } else if (parse.getClass().isArray()) {
            Object[] ast = (Object[]) parse;
            Operation operation = null;
            int length = ast.length;
            
            if (length == 0) {
                jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(Object.class),
                    newCodeBlock().aconst_null().areturn());
            }
            
            for (int pointer = 0; pointer < length; pointer++) {
                Object token = ast[pointer];

                if (token.getClass() == String.class) {
                    if (QUOTE.equals(token)) {
                        operation = new QuoteOperation();
                    } else if (ATOM.equals(token)) {
                        operation = new AtomOperation();
                    } else {
                        throw new RuntimeException("Not implemented");
                    }

                    CodeBlock block = operation.compile(ast[++pointer], newCodeBlock());

                    jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(Object.class),
                            block);

                } else {
                    throw new RuntimeException("Not implemented");
                }
            }

        } else {
            jiteClass.defineMethod("main", ACC_PUBLIC | ACC_STATIC, CodegenUtils.sig(Object.class),
                    newCodeBlock().aconst_null().areturn());
        }
        
        Class<?> compiledClass = new DynamicClassLoader().define(jiteClass);
        
        try (FileOutputStream fos = new FileOutputStream("/tmp/anonymous.class")) {
            fos.write(jiteClass.toBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        return compiledClass;
    }

}
