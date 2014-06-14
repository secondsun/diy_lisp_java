package net.saga.diy.lisp.compiler.operation;

import java.util.UUID;
import me.qmx.jitescript.CodeBlock;
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.diy.lisp.LispCompiler;

/**
 *
 * @author summers
 */
public class EqOperation implements Operation<Operation> {

    @Override
    public Operation<CodeBlock> compile(final Object firstToken, JiteClass jiteClass) {
        return (secondToken, secondJiteClass) -> {

            CodeBlock codeBlock = newCodeBlock();

            String firstMethodName = "eq_" + UUID.randomUUID().toString();
            String secondMethodName = "eq_" + UUID.randomUUID().toString();
            
            LispCompiler.compile(firstToken, jiteClass, firstMethodName);
            LispCompiler.compile(secondToken, jiteClass, secondMethodName);
            codeBlock.aload(0);
            codeBlock.invokevirtual(jiteClass.getClassName(), firstMethodName, sig(Object.class));
            codeBlock.aload(0);
            codeBlock.invokevirtual(jiteClass.getClassName(), secondMethodName, sig(Object.class));

            codeBlock.invokevirtual(p(Object.class), "equals", sig(boolean.class, Object.class));
            codeBlock.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
            codeBlock.areturn();
            return codeBlock;

        };
    }

}
