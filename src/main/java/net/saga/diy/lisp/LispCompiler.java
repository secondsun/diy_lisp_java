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
import static javax.management.Query.value;
import me.qmx.jitescript.CodeBlock;
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import static me.qmx.jitescript.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.c;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static net.saga.diy.lisp.SpecialTokens.ATOM;
import static net.saga.diy.lisp.SpecialTokens.EQ;
import static net.saga.diy.lisp.SpecialTokens.IF;
import static net.saga.diy.lisp.SpecialTokens.QUOTE;
import net.saga.diy.lisp.compiler.operation.AtomOperation;
import net.saga.diy.lisp.compiler.operation.DefineOperation;
import net.saga.diy.lisp.compiler.operation.EqOperation;
import net.saga.diy.lisp.compiler.operation.IfOperation;
import net.saga.diy.lisp.compiler.operation.MathOperation;
import net.saga.diy.lisp.compiler.operation.Operation;
import net.saga.diy.lisp.compiler.operation.QuoteOperation;
import net.saga.diy.lisp.compiler.operation.LookupOperation;
import net.saga.diy.lisp.types.CompilerContext;
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
        CompilerContext context = new CompilerContext();
        JiteClass klass = compileMethod(parse, context, "main");
        Class<?> compiledClass = new DynamicClassLoader().define(klass);

        return compiledClass;
    }

    public static JiteClass compileMethod(Object rootToken, CompilerContext compilerContext, String methodName) {

        JiteClass parentClass = compilerContext.jiteClass;

        compileBlock(rootToken, compilerContext); //Compile expression
        
        compilerContext.currentBlock().areturn(); // return value of expression
        
        compilerContext.blockToMethod(methodName);

        return parentClass;
    }

    
    public static void compileBlock(Object rootToken, CompilerContext compilerContext) {
        
        if (rootToken.getClass() == Boolean.class) {
            if ((Boolean) rootToken) {
                compilerContext.currentBlock().ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
            } else {
                compilerContext.currentBlock().ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
            }

        } else if (rootToken.getClass() == Integer.class) {

            compilerContext.currentBlock().ldc((int) rootToken).invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
            

        } else if (rootToken.getClass().isArray()) {
            Object[] ast = (Object[]) rootToken;
            Operation operation = null;
            int length = ast.length;

            if (length == 0) {
                compilerContext.currentBlock().aconst_null();
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
                    } else if (SpecialTokens.DEFINE.equals(token)) {
                        operation = new DefineOperation();
                    } else if (SpecialTokens.MATHS.contains(token)) {
                        operation = new MathOperation(token);
                    } else {
                        operation = new LookupOperation();
                        CodeBlock result = (CodeBlock) operation.compile(token, compilerContext);
                        if (!(result instanceof net.saga.diy.lisp.evaluator.operation.Operation)) {
                            throw new RuntimeException("This won't work because it will create tons of extra methods exception");

                        } else {
                            throw new RuntimeException("Not implemented");
                        }
                    }

                    Object res = operation.compile(ast[++pointer], compilerContext);

                    while (res instanceof Operation) {
                        if ((pointer + 1) >= ast.length) {
                            throw new LispException("Missing token");
                        }
                        res = ((Operation) res).compile(ast[++pointer], compilerContext);
                    }

                } else {
                    throw new RuntimeException("Not implemented");
                }
            }

        } else {
            compilerContext.currentBlock().aconst_null();
        }
    }

    
}
