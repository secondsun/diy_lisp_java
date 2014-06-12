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
import net.saga.diy.lisp.types.Utils;
import static net.saga.diy.lisp.types.Utils.isList;

public class EmptyOperation implements Operation<Boolean> {

    @Override
    public Boolean operate(Object listToken, Environment firstEnv) {
        if (!isList(listToken)) {
            throw new LispException(listToken + " is not a list");
        }
        Object res = Evaluator.evaluate((Object[]) listToken, firstEnv);

        return Utils.isEmptyList(res);

    }

}
