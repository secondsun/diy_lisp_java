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
 *
 * This project is based on, borrows heavily from, and copies the documentation of
 * https://github.com/kvalle/diy-lisp/
 */
package net.saga.diy.lisp.evaluator.operation;

import net.saga.diy.lisp.Evaluator;
import net.saga.diy.lisp.types.Environment;
import net.saga.diy.lisp.types.LispException;

public class DefineOperation implements Operation<Operation> {

    @Override
    public Operation<Void> operate(Object name, Environment env) {
        
        if (!(name instanceof String)) {
            throw new LispException("Illegal define name token");
        }
        
        return ((value, env2) ->{
            env2.set((String) name, Evaluator.evaluate(value, env2));
            return null;
        });
    }
}
