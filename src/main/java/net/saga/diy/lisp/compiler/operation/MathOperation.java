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
package net.saga.diy.lisp.compiler.operation;

import java.util.UUID;
import me.qmx.jitescript.CodeBlock;
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.LabelNode;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.diy.lisp.LispCompiler;
import net.saga.diy.lisp.evaluator.operation.Operand;
import net.saga.diy.lisp.types.LispException;

/**
 *
 * @author summers
 */
public class MathOperation implements Operation<Operation<CodeBlock>> {

    private final Operand op;

    public MathOperation(Object token) {
        op = Operand.fromSymbol((String) token);
    }

    @Override
    public Operation<CodeBlock> compile(Object token, JiteClass env) {

        final String firstMethodName = verifyAndCompile(token, env);

        switch (op) {
            case ADD:
                return ((nextToken, env2) -> {
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock addCodeBlock = newCodeBlock();

                    addCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class))
                            .invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class))
                            .invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .iadd()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class))
                            .areturn();
                    return addCodeBlock;
                });

            case SUB:
                return ((nextToken, env2) -> {
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock subCodeBlock = newCodeBlock();

                    subCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class))
                            .invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class))
                            .invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .isub()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class))
                            .areturn();
                    return subCodeBlock;
                });
            case DIV:
                return ((nextToken, env2) -> {
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock divCodeBlock = newCodeBlock();

                    divCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .idiv()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class))
                            .areturn();
                    return divCodeBlock;
                });
            case MOD:
                return ((nextToken, env2) -> {
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock modCodeBlock = newCodeBlock();

                    modCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .irem()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class))
                            .areturn();
                    return modCodeBlock;
                });
            case GT:
                return ((nextToken, env2) -> {
                    LabelNode isGreater = new LabelNode();
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock gtCodeBlock = newCodeBlock();

                    gtCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .if_icmpgt(isGreater)
                            .ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn()
                            .label(isGreater)
                            .ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn();
                    return gtCodeBlock;
                });
            case LT:
                return ((nextToken, env2) -> {
                    LabelNode isLesser = new LabelNode();
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock ltCodeBlock = newCodeBlock();

                    ltCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .if_icmplt(isLesser)
                            .ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn()
                            .label(isLesser)
                            .ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn();
                    return ltCodeBlock;
                });
            case MULT:
                return ((nextToken, env2) -> {
                    String secondMethodName = verifyAndCompile(nextToken, env2);
                    CodeBlock multCodeBlock = newCodeBlock();

                    multCodeBlock
                            .invokestatic(env.getClassName(), firstMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .invokestatic(env.getClassName(), secondMethodName, sig(Object.class))
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .imul()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class))
                            .areturn();
                    return multCodeBlock;
                });
            default:
                throw new AssertionError(op.name());
        }
    }

    ;

    /**
     * 
     * compiles a method to evaluate a term and returns the string method name
     * 
     * @param token
     * @param env
     * @return 
     */
    private String verifyAndCompile(Object token, JiteClass env) {

        String methodName = "operand" + UUID.randomUUID().toString();

        if (token instanceof Integer) {
            LispCompiler.compile(token, env, methodName);
            return methodName;
        }

        if (token instanceof String) {
            throw new RuntimeException("Not implemented");
        }

        if (token.getClass().isArray()) {
            LispCompiler.compile(token, env, methodName);
            return methodName;
        }

        throw new LispException("Math operations expect a Integer");

    }

}
