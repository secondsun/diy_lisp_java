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

import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.internal.org.objectweb.asm.tree.LabelNode;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.diy.lisp.LispCompiler;
import net.saga.diy.lisp.evaluator.operation.Operand;
import net.saga.diy.lisp.types.CompilerContext;
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
    public Operation<CodeBlock> compile(Object token, CompilerContext compilerContext) {
        JiteClass env = compilerContext.jiteClass;

        verifyAndCompile(token, compilerContext);
        compilerContext.currentBlock().checkcast(p(Integer.class))
                .invokevirtual(p(Integer.class), "intValue", sig(int.class));

        switch (op) {
            case ADD:
                return ((nextToken, context2) -> {
                    verifyAndCompile(nextToken, context2);
                    CodeBlock addCodeBlock = context2.currentBlock();

                    addCodeBlock
                            .checkcast(p(Integer.class))
                            .invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .iadd()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
                    return addCodeBlock;
                });

            case SUB:
                return ((nextToken, context2) -> {
                    verifyAndCompile(nextToken, context2);
                    CodeBlock subCodeBlock = context2.currentBlock();

                    subCodeBlock
                            .checkcast(p(Integer.class))
                            .invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .isub()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
                    return subCodeBlock;
                });
            case DIV:
                return ((nextToken, context2) -> {
                    verifyAndCompile(nextToken, context2);
                    CodeBlock divCodeBlock = context2.currentBlock();

                    divCodeBlock
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .idiv()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
                    return divCodeBlock;
                });
            case MOD:
                return ((nextToken, context2) -> {
                    verifyAndCompile(nextToken, context2);
                    CodeBlock modCodeBlock = context2.currentBlock();

                    modCodeBlock
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .irem()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
                    return modCodeBlock;
                });
            case GT:
                return ((nextToken, context2) -> {
                    LabelNode isGreater = new LabelNode();
                    verifyAndCompile(nextToken, context2);
                    CodeBlock gtCodeBlock = context2.currentBlock();

                    gtCodeBlock
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .if_icmpgt(isGreater)
                            .ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn()
                            .label(isGreater)
                            .ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
                    return gtCodeBlock;
                });
            case LT:
                return ((nextToken, context2) -> {
                    LabelNode isLesser = new LabelNode();
                    verifyAndCompile(nextToken, context2);
                    CodeBlock ltCodeBlock = context2.currentBlock();

                    ltCodeBlock
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .if_icmplt(isLesser)
                            .ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn()
                            .label(isLesser)
                            .ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
                    return ltCodeBlock;
                });
            case MULT:
                return ((nextToken, context2) -> {
                    verifyAndCompile(nextToken, context2);
                    CodeBlock multCodeBlock = context2.currentBlock();

                    multCodeBlock
                            .checkcast(p(Integer.class)).invokevirtual(p(Integer.class), "intValue", sig(int.class))
                            .imul()
                            .invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
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
    private void verifyAndCompile(Object token, CompilerContext env) {

        if (token instanceof Integer) {
            LispCompiler.compileBlock(token, env);
            return;
        }

        if (token instanceof String) {
            throw new RuntimeException("Not implemented");
        }

        if (token.getClass().isArray()) {
            LispCompiler.compileBlock(token, env);
            return;
        }

        throw new LispException("Math operations expect a Integer");

    }

}
