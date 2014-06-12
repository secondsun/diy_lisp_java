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

public class IfOperation implements Operation<Object>{

    private final Operation evaluateForFalse = (token1,env1) -> {return (Operation)(token2,env2) -> Evaluator.evaluate(token2, env2);};
    private final Operation evaluateForTrue = (token1,env1) -> {return (Operation)(token2,env2) -> Evaluator.evaluate(token1, env1);};
    
    @Override
    public Object operate(Object token, Environment env) {
        if ((boolean)Evaluator.evaluate(token, env)) {
            return evaluateForTrue;
        } else {
            return evaluateForFalse;
        }
    }
    
}
