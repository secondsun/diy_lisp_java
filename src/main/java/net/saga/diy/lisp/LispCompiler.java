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
import me.qmx.jitescript.JiteClass;
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
import net.saga.diy.lisp.compiler.operation.LambdaOperation;
import net.saga.diy.lisp.compiler.operation.LookupOperation;
import net.saga.diy.lisp.compiler.operation.MathOperation;
import net.saga.diy.lisp.compiler.operation.Operation;
import net.saga.diy.lisp.compiler.operation.QuoteOperation;
import net.saga.diy.lisp.types.CompilerContext;
import net.saga.diy.lisp.types.LispException;

/**
 *
 * @author summers
 */
public class LispCompiler {

    public static class DynamicClassLoader extends ClassLoader {

        public Class<?> define(JiteClass jiteClass) {
            byte[] classBytes = jiteClass.toBytes();

            try (FileOutputStream fos = new FileOutputStream(String.format("/tmp/%s.class", jiteClass.getClassName()))) {
                fos.write(classBytes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (jiteClass.getChildClasses().size() > 0) {
                jiteClass.getChildClasses().stream().forEach((child) -> {
                    define(child);
                });
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

                if (token.getClass().isArray()) {
                    compileBlock(token, compilerContext);
                } else if (token.getClass() == String.class) {
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
                    }  else if (SpecialTokens.LAMBDA.equals(token)) {
                        operation = new LambdaOperation();
                    } else {
                        operation = new LookupOperation();
                        CodeBlock result = (CodeBlock) operation.compile(token, compilerContext);
                        if (!(result instanceof net.saga.diy.lisp.evaluator.operation.Operation)) {
                            continue;
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
                    throw new LispException(token + "is not implemented or is not a valid token");
                }
            }

        } else {
            Operation operation = new LookupOperation();
            CodeBlock result = (CodeBlock) operation.compile(rootToken, compilerContext);
            if (!(result instanceof net.saga.diy.lisp.evaluator.operation.Operation)) {
                
            } else {
                throw new RuntimeException("Not implemented");
            }
        }
    }

}
