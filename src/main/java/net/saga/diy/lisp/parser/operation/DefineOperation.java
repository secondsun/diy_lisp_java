/**
 * Copyright Summers Pittman, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.AST;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;

public class DefineOperation implements Operation<Operation> {

    @Override
    public Operation<Void> operate(AST.Token name, Environment env) {
        
        if (name.type != String.class || !(name.value instanceof String)) {
            throw new LispException("Illegal define name token");
        }
        
        return ((value, env2) ->{
            env2.set((String) name.value, Evaluator.evaluate(new AST(value), env2));
            return null;
        });
    }
}
