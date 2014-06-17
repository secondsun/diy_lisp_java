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
import static me.qmx.jitescript.util.CodegenUtils.ci;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.diy.lisp.types.CompiledClosure;
import net.saga.diy.lisp.types.CompilerContext;

/**
 *
 * @author summers
 */
public class LookupOperation implements Operation<CodeBlock> {
    
    @Override
    public CodeBlock compile(Object symbolName, CompilerContext compilerContext) {
        int depth = compilerContext.getFieldDepth((String) symbolName);
        Object value = compilerContext.lookup((String)symbolName);
        
        if (value != null) {
            if (value.getClass() == Boolean.class) {//The variable is a constant.
                if ((Boolean) value) {
                    compilerContext.currentBlock().ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
                } else {
                    compilerContext.currentBlock().ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
                }

            } else if (value.getClass() == Integer.class) {

                compilerContext.currentBlock().ldc((int) value).invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));

            } else if (value.getClass() == CompiledClosure.class) {
                throw new RuntimeException("not implemented");
            }
            return compilerContext.currentBlock();
        }
        
        CompilerContext currentContext = compilerContext;
        
        if (depth == 0) {
            compilerContext.currentBlock().aload(0);
            return compilerContext.currentBlock().getfield(compilerContext.getClassName(), (String) symbolName, ci(Object.class));
        } else {
            
            compilerContext.currentBlock().aload(0);
            
            while (depth-- > 0) {
                compilerContext.currentBlock().getfield(currentContext.getClassName(), "context", ci(Object.class)).checkcast(currentContext.getParentContext().getClassName());
                currentContext = currentContext.getParentContext();
            }
            return compilerContext.currentBlock().getfield(currentContext.getClassName(), (String) symbolName, ci(Object.class));
        }
        
    }
    
}
