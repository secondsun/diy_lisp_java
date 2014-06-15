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
import me.qmx.jitescript.util.CodegenUtils;
import static me.qmx.jitescript.util.CodegenUtils.*;
import net.saga.diy.lisp.LispCompiler;
import net.saga.diy.lisp.types.CompilerContext;
import net.saga.diy.lisp.types.LispException;

/**
 *
 * @author summers
 */
public class DefineOperation implements Operation<Operation<CodeBlock>> {

    @Override
    public Operation<CodeBlock> compile(Object name, CompilerContext context) {

        if (!(name instanceof String)) {
            throw new LispException("Illegal define name token");
        }

        return ((value, env2) -> {
            String evaluateMethod = "evaluate" + UUID.randomUUID().toString();
            LispCompiler.compileMethod(value, context, evaluateMethod);
            context.defineVariable((String) name, null);
            CodeBlock block = context.currentBlock();
            block.aload(0);
            block.aload(0);
            block.invokevirtual(context.getClassName(), evaluateMethod, CodegenUtils.sig(Object.class));
            block.putfield(context.getClassName(), (String) name, ci(Object.class));
            return block;
        });

        
    }

}
