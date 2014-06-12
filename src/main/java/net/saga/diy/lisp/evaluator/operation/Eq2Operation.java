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
import static net.saga.diy.lisp.types.Utils.isList;

/**
 * 
 * @author summers
 */
public class Eq2Operation implements Operation<Boolean> {

    private final Object firstToken;

    public Eq2Operation(Object firstArgument) {
        this.firstToken = firstArgument;
    }

    @Override
    public Boolean operate(Object secondToken, Environment env) {
        AtomOperation isAtom = new AtomOperation();

        Object firstValue;
        Object secondValue;

        firstValue = (Evaluator.evaluate(firstToken, env));

        secondValue = (Evaluator.evaluate(secondToken, env));

        if (isList(firstValue)) {
            return false;
        }

        if (isList(secondValue)) {
            return false;
        }

        return firstValue.equals(secondValue);

    }

}
