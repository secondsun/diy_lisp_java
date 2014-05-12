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
 */
package net.saga.diy.lisp.parser.operation;

import java.util.List;
import net.saga.diy.lisp.parser.Evaluator;
import net.saga.diy.lisp.parser.SpecialTokens;
import net.saga.diy.lisp.parser.types.Environment;
import net.saga.diy.lisp.parser.types.LispException;
import static net.saga.diy.lisp.parser.types.Utils.isList;

public class ConsOperation implements Operation<Operation> {

    @Override
    public Operation operate(Object toAddToken, Environment env) {

        return ((listToken, listEnv) -> {

            Object toAddValue;

            toAddValue = Evaluator.evaluate(toAddToken, listEnv);

            if (!isList(listToken)) {
                throw new LispException(listToken + " is not a list");
            }

            Object[] listTokens = (Object[]) listToken;
            Object[] tokens;

            if (SpecialTokens.ALL_TOKENS.contains(listTokens[0])) {
                Object[] listOfTokens = ((Object[]) Evaluator.evaluate(listTokens, listEnv));
                tokens = new Object[listOfTokens.length + 1];
                tokens[0] = toAddValue;
                for (int i = 1; i < tokens.length; i++) {
                    tokens[i] = listOfTokens[i - 1];
                }
            } else {
                tokens = new Object[listTokens.length + 1];
                tokens[0] = toAddValue;
                for (int i = 1; i < tokens.length; i++) {
                    tokens[i] = (Object) Evaluator.evaluate(listTokens[i - 1], listEnv);
                }
            }

            return tokens;
        });
    }
}
