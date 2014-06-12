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

import java.util.ArrayList;
import java.util.List;
import net.saga.diy.lisp.Evaluator;
import net.saga.diy.lisp.types.Closure;
import net.saga.diy.lisp.types.Environment;

public class ClosureOperation implements Operation<Object> {

    private final Closure closure;
    private final List<Object> tokens = new ArrayList<Object>();

    public ClosureOperation(Closure closure) {
        this.closure = closure;
    }

    @Override
    public Object operate(Object token, Environment env) {
        tokens.add(token);
        if (tokens.size() == closure.getParams().length) {
            Environment closureEnvironment = closure.getEnv();
            for (int i = 0; i < tokens.size(); i++) {
                String varname = (String) closure.getParams()[i];
                Object variableToken = tokens.get(i);
                Object tokenValue = Evaluator.evaluate(variableToken, env);
                closureEnvironment = closureEnvironment.extend(varname, tokenValue);

            }

            return Evaluator.evaluate(closure.getBody(), closureEnvironment);

        } else {
            return this;
        }

    }

}
