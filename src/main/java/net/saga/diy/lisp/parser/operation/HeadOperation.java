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

import static net.saga.diy.lisp.parser.Evaluator.evaluate;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static net.saga.diy.lisp.parser.types.Utils.isEmptyList;
import static net.saga.diy.lisp.parser.types.Utils.isList;

public class HeadOperation implements Operation<Object> {

    @Override
    public Object operate(Object listToken, Environment env) {
        if (!isList(listToken)) {
            throw new LispException(listToken + " is not a list");
        } else if (isEmptyList(listToken)) {
            throw new LispException(listToken + " is empty");
        }

        Object result = evaluate((Object[]) listToken, env);

        if (isList(result)) {
            Object[] ast = (Object[]) result;
            if (isEmptyList(ast)) {
                throw new LispException(listToken + " is empty");
            }
            return evaluate(ast[0], env);
        } else if (result.getClass().isArray()) {
            return ((Object[]) result)[0];
        }

        throw new LispException(listToken + " is not a list");
    }

}
