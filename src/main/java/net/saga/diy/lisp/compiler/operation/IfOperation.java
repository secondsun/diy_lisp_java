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
import me.qmx.jitescript.internal.org.objectweb.asm.tree.LabelNode;
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.p;
import net.saga.diy.lisp.LispCompiler;
import net.saga.diy.lisp.types.CompilerContext;

public class IfOperation implements Operation<Operation<Operation<CodeBlock>>> {

    @Override
    public Operation<Operation<CodeBlock>> compile(Object ifExpression, CompilerContext context) {

        return ((trueBlock, trueClass) -> {
            return ((falseBlock, falseClass) -> {
                LabelNode trueNode = new LabelNode();
                LabelNode falseNode = new LabelNode();
                LabelNode afterNode = new LabelNode();

                CodeBlock codeBlock = context.currentBlock();
                
                LispCompiler.compileBlock(ifExpression, context);
                codeBlock.checkcast(p(Boolean.class));
                codeBlock.invokevirtual(p(Boolean.class), "booleanValue", CodegenUtils.sig(boolean.class));
                codeBlock.iftrue(trueNode);
                codeBlock.label(falseNode);
                LispCompiler.compileBlock(falseBlock, falseClass);
                codeBlock.go_to(afterNode);
                codeBlock.label(trueNode);
                LispCompiler.compileBlock(trueBlock, trueClass);
                codeBlock.label(afterNode);
                return codeBlock;
            });
        });

    }

}
