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
import net.saga.diy.lisp.SpecialTokens;
import net.saga.diy.lisp.types.CompilerContext;

/**
 *
 * @author summers
 */
public class AtomOperation implements Operation<CodeBlock> {

    @Override
    public CodeBlock compile(Object token, CompilerContext context) {
        
        CodeBlock codeBlock = newCodeBlock();
        
        if (!token.getClass().isArray()) {
            return codeBlock.ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn();
        }

        Object[] tokenArr = (Object[]) token;

        if (SpecialTokens.QUOTE.equals(tokenArr[0])
                && tokenArr.length == 2
                && !tokenArr[1].getClass().isArray()) {
            return codeBlock.ldc(Boolean.TRUE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn();
        } else {
            return codeBlock.ldc(Boolean.FALSE).invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class)).areturn();
        }
    }

}
