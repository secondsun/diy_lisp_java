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

            CodeBlock codeBlock = newCodeBlock();

            String firstMethodName = "eq_" + UUID.randomUUID().toString();
            String secondMethodName = "eq_" + UUID.randomUUID().toString();
            
            LispCompiler.compile(firstToken, context, firstMethodName);
            LispCompiler.compile(secondToken, context, secondMethodName);
            codeBlock.aload(0);
            codeBlock.invokevirtual(context.getClassName(), firstMethodName, sig(Object.class));
            codeBlock.aload(0);
            codeBlock.invokevirtual(secondContext.getClassName(), secondMethodName, sig(Object.class));

            codeBlock.invokevirtual(p(Object.class), "equals", sig(boolean.class, Object.class));
            codeBlock.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
            codeBlock.areturn();
            return codeBlock;

        };
    }

}
