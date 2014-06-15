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
import static me.qmx.jitescript.CodeBlock.newCodeBlock;
import me.qmx.jitescript.JiteClass;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import net.saga.diy.lisp.types.CompilerContext;

/**
 *
 * @author summers
 */
public class QuoteOperation implements Operation<CodeBlock> {

    @Override
    public CodeBlock compile(Object token, CompilerContext compilerContext) {
        
        CodeBlock codeBlock = compilerContext.currentBlock();
        
        if (token.getClass().isArray()) {
            Object[] array = (Object[]) token;
            int length = array.length;
            codeBlock.ldc(length);
            codeBlock.anewarray(p(Object.class));
            for (int i = 0; i < length; i++) {
                codeBlock.dup();
                codeBlock.ldc(i);
                if (array[i].getClass() == Integer.class) {
                    codeBlock.ldc((Integer)array[i]);
                    codeBlock.invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
                } else if (array[i].getClass() == Boolean.class) {
                    codeBlock.ldc((Boolean)array[i]);
                    codeBlock.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
                } else {
                    codeBlock.ldc(array[i]);
                }
                codeBlock.aastore();
            }

            

            return codeBlock;
        } else {
            return codeBlock.ldc(token);
        }

    }

}
