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
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.p;
import net.saga.diy.lisp.LispCompiler;

public class IfOperation implements Operation<Operation<Operation<CodeBlock>>> {

    @Override
    public Operation<Operation<CodeBlock>> compile(Object token, JiteClass jiteClass) {

        return ((trueToken, trueClass) -> {
            return ((falseToken, falseClass) -> {
                LabelNode trueNode = new LabelNode();
                LabelNode falseNode = new LabelNode();

                String evaluateIfMethod = "if_" + UUID.randomUUID().toString();
                LispCompiler.compile(token, jiteClass, evaluateIfMethod);

                String trueMethod = "true_" + UUID.randomUUID().toString();
                LispCompiler.compile(trueToken, jiteClass, trueMethod);

                String falseMethod = "false_" + UUID.randomUUID().toString();
                LispCompiler.compile(falseToken, jiteClass, falseMethod);
                
                CodeBlock codeBlock = newCodeBlock();
                codeBlock.aload(0);
                codeBlock.invokevirtual(jiteClass.getClassName(), evaluateIfMethod, CodegenUtils.sig(Object.class));
                codeBlock.checkcast(p(Boolean.class));
                codeBlock.invokevirtual(p(Boolean.class), "booleanValue", CodegenUtils.sig(boolean.class));
                codeBlock.iftrue(trueNode);
                codeBlock.label(falseNode);
                codeBlock.aload(0);
                codeBlock.invokevirtual(jiteClass.getClassName(), falseMethod, CodegenUtils.sig(Object.class)).areturn();
                codeBlock.label(trueNode);
                codeBlock.aload(0);
                codeBlock.invokevirtual(jiteClass.getClassName(), trueMethod, CodegenUtils.sig(Object.class)).areturn();
                
                return codeBlock;
            });
        });

    }

}
