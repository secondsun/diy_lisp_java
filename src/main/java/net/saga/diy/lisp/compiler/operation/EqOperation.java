package net.saga.diy.lisp.compiler.operation;

import java.util.UUID;
import me.qmx.jitescript.CodeBlock;
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.diy.lisp.LispCompiler;
import net.saga.diy.lisp.types.CompilerContext;

/**
 *
 * @author summers
 */
public class EqOperation implements Operation<Operation> {

    @Override
    public Operation<CodeBlock> compile(final Object firstToken, CompilerContext context) {
        return (secondToken, secondContext) -> {

            CodeBlock codeBlock = context.currentBlock();

            LispCompiler.compileBlock(firstToken, context);
            LispCompiler.compileBlock(secondToken, secondContext);
            
            codeBlock.invokevirtual(p(Object.class), "equals", sig(boolean.class, Object.class));
            codeBlock.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
            return codeBlock;

        };
    }

}
