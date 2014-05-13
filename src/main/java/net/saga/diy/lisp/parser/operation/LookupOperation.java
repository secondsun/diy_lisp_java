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
package net.saga.diy.lisp.parser.operation;

import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.types.Closure;
import net.saga.diy.lisp.parser.types.Environment;

/**
 * 
 * @author summers
 */
public class LookupOperation implements Operation {

    public LookupOperation() {
    }

    @Override
    public Object operate(Object token, Environment env) {
        Object result = env.lookup((String) token);
        if (result instanceof Closure) {
            Closure closure = (Closure) result;
            if (closure.getParams().length == 0) {
                return Evaluator.evaluate(closure.getBody(), closure.getEnv());
            } else {
                return new ClosureOperation(closure);
            }
        } else {
            return result;
        }
    }

}
