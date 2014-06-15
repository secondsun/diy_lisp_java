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
import net.saga.diy.lisp.types.CompilerContext;

/**
 *
 * @author summers
 */
public class LookupOperation implements Operation<CodeBlock> {

    @Override
    public CodeBlock compile(Object symbolName, CompilerContext compilerContext) {
        compilerContext.lookup((String) symbolName);
        return compilerContext.currentBlock().aload(0).getfield(compilerContext.getClassName(), (String) symbolName, ci(Object.class));
    }

}
